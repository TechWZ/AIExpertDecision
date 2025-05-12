import os
import re # Import regex module
from openai import OpenAI
from dotenv import load_dotenv

# Load .env file at the beginning of the script
# Construct path relative to this file (app/services/llm_service.py)
# Go up two levels (to backend/) and then find .env
dotenv_path = os.path.join(os.path.dirname(__file__), '..', '..', '.env') 
# print(f"[DEBUG] Attempting to load .env from: {dotenv_path}") # Remove/comment out debug print

# Check if .env file exists before loading
if os.path.exists(dotenv_path):
    # print("[DEBUG] .env file found.") # Remove/comment out debug print
    # Explicitly tell load_dotenv to override existing vars (optional, but can help debugging)
    # Also capture the return value to see if it loaded successfully
    loaded_ok = load_dotenv(dotenv_path=dotenv_path, override=True) # Remove verbose=True
    # print(f"[DEBUG] load_dotenv returned: {loaded_ok}") # Remove/comment out debug print
else:
    # print("[DEBUG] .env file NOT found at the specified path.") # Remove/comment out debug print
    pass # Or handle error appropriately if .env is strictly required

# --- Remove Debug Block --- 
# print("\n[DEBUG] Checking Environment Variables after load_dotenv:")
# print(f"[DEBUG] SILICONFLOW_API_KEY: {os.getenv('SILICONFLOW_API_KEY')}")
# print(f"[DEBUG] SILICONFLOW_API_BASE: {os.getenv('SILICONFLOW_API_BASE')}")
# print(f"[DEBUG] FLASK_CONFIG: {os.getenv('FLASK_CONFIG')}") # Check another variable from .env
# print("---- End Debug ----\n")
# --- End Debug ---

# Load credentials from environment variables (now .env should be loaded)
api_key = os.getenv("SILICONFLOW_API_KEY")
base_url = os.getenv("SILICONFLOW_API_BASE")

if not api_key:
    raise ValueError("API key for SiliconFlow not found. Ensure SILICONFLOW_API_KEY is in the .env file.")
if not base_url:
    raise ValueError("API base URL for SiliconFlow not found. Ensure SILICONFLOW_API_BASE is in the .env file.")

# print("Credentials loaded successfully at top level.") # Remove/comment out debug print

# Initialize the OpenAI client pointing to the SiliconFlow API
client = OpenAI(
    api_key=api_key,
    base_url=base_url,
)

# Define the expected return type for clarity
from typing import List, Dict, Union
RoleRecommendation = Dict[str, Union[str, int]]

def get_expert_role_recommendations(content: str, model_name: str = "deepseek-ai/DeepSeek-V3", num_roles: int = 5) -> List[RoleRecommendation]:
    """ 
    Uses the specified LLM via SiliconFlow API to recommend expert roles with matching scores.
    
    Args:
        content: The research content text.
        model_name: The name of the model to use.
        num_roles: The desired number of roles.
        
    Returns:
        A list of dictionaries, each containing 'role_name' (str) and 'score' (int),
        or an empty list if an error occurs or parsing fails.
    """
    # Updated prompt to request score in parentheses
    system_prompt = (
        f"Based on the following research content, please recommend {num_roles} relevant expert roles or job titles. "
        f"For each role, provide a matching score as a percentage in parentheses next to the role name, like 'Role Name (Score%)'. "
        f"Provide only the list, separated by commas, without any introductory text or numbering. Example: Materials Scientist (85%), Photovoltaic Engineer (78%)\""
    )
    
    recommended_roles_data: List[RoleRecommendation] = []

    try:
        response = client.chat.completions.create(
            model=model_name,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": content}
            ],
            temperature=0.5, # Adjust temperature for creativity vs determinism
            max_tokens=150 # Increased slightly for scores
        )
        
        if response.choices:
            raw_response = response.choices[0].message.content.strip()
            print(f"[LLM Raw Response for Roles]: {raw_response}") # Debug raw response
            
            # Split potential roles separated by commas
            potential_roles = raw_response.split(',')
            
            for item in potential_roles:
                item = item.strip()
                if not item: continue
                
                # Use regex to find Role Name and Score% pattern
                match = re.match(r'(.+)\s*\((\d{1,3})%\)$', item)
                if match:
                    role_name = match.group(1).strip()
                    score_str = match.group(2)
                    try:
                        score = int(score_str)
                        recommended_roles_data.append({"role_name": role_name, "score": score})
                        if len(recommended_roles_data) >= num_roles: # Stop if we have enough
                            break
                    except ValueError:
                        print(f"Warning: Could not parse score '{score_str}' for role chunk '{item}'")
                else:
                    # Fallback: If pattern doesn't match, maybe LLM just gave the name
                    # We can add it with a default/null score or ignore it.
                    # Let's add with a default score of 0 for now, or adjust as needed.
                    # recommended_roles_data.append({"role_name": item, "score": 0})
                    print(f"Warning: Could not parse role and score from chunk: '{item}'. Expected format: 'Role Name (Score%)'")
        else:
            print("LLM response did not contain expected choices.")
            
    except Exception as e:
        print(f"Error calling SiliconFlow API for role recommendation: {e}")

    return recommended_roles_data

def run_simulation_for_role(content: str, role_name: str, user_prompt: str, model_name: str) -> str:
    """
    Runs the LLM simulation for a single expert role.

    Args:
        content: The original research content.
        role_name: The expert role being simulated.
        user_prompt: The specific prompt/instruction defined for this role.
        model_name: The LLM model to use for this simulation.

    Returns:
        The LLM's generated response string, or an error message string.
    """
    system_prompt = f"You are simulating an expert in the role of '{role_name}'. Based on the following research content, provide a response according to the instructions. Research Content: {content}\n\nInstructions: {user_prompt}"
    
    print(f"\n--- Running simulation for role: {role_name} using model: {model_name} ---")
    # print(f"System Prompt: {system_prompt}") # Uncomment for debugging prompt construction

    try:
        response = client.chat.completions.create(
            model=model_name,
            messages=[
                {"role": "system", "content": system_prompt},
                # Note: We are putting everything in the system prompt for this structure.
                # Alternatively, you could keep a shorter system prompt defining the role 
                # and put the content + user_prompt in the 'user' message.
            ],
            temperature=0.7, # May want slightly higher temp for simulation responses
            max_tokens=500   # Allow for longer responses
        )
        
        if response.choices:
            result_text = response.choices[0].message.content.strip()
            print(f"Simulation result for {role_name}: {result_text[:100]}...")
            return result_text
        else:
            print(f"LLM response for {role_name} did not contain expected choices.")
            return "Error: LLM response was empty."
            
    except Exception as e:
        print(f"Error calling SiliconFlow API for role {role_name}: {e}")
        return f"Error: API call failed for role {role_name}. Details: {e}"

# Example usage (for testing purposes)
if __name__ == '__main__':
    # No need to load .env again here, it's loaded at the top
    # No need to re-initialize client, use the global one
    
    # print("\n--- Running Test Block ---") # Remove/comment out debug print
    test_content = "Research on novel photovoltaic materials focuses on perovskite stability and efficiency improvements for next-generation solar cells."
    # Use the main function and the globally initialized client
    recommended_roles = get_expert_role_recommendations(test_content) 
    print(f"Test Content: {test_content}")
    print(f"Recommended Roles (with scores): {recommended_roles}") 

    print("\n--- Testing Simulation --- ")
    sim_content = "The paper proposes a novel deep learning architecture using attention mechanisms for improved time-series forecasting in financial markets."
    sim_role = "Quantitative Analyst"
    sim_prompt = "Critique the methodology. What are potential pitfalls or alternative approaches?"
    sim_model = "deepseek-ai/DeepSeek-V3"
    simulation_result = run_simulation_for_role(sim_content, sim_role, sim_prompt, sim_model)
    print(f"\nTest Simulation Result for {sim_role}:\n{simulation_result}") 