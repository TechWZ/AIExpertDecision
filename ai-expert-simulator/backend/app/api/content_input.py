from flask import request, jsonify
from . import api
from ..services.llm_service import get_expert_role_recommendations # Import the service function

@api.route('/submit_content', methods=['POST'])
def submit_content():
    data = request.get_json()
    if not data or 'content' not in data:
        return jsonify({'error': 'Missing content'}), 400

    content = data['content']
    print(f"Received content: {content[:100]}...") # Keep logging received content
    
    # --- Call LLM Service for Role Recommendation (now returns list of dicts) ---
    recommended_roles_data = get_expert_role_recommendations(content)
    
    if not recommended_roles_data:
        # Handle case where LLM call failed or returned no roles
        print("Failed to get role recommendations from LLM service or parsing failed.")
        # Optionally, you could still return a success message to the user, 
        # or return a specific error.
        # For now, let's return an empty list but indicate success in receiving content.
        return jsonify({
            'message': 'Content received, but could not generate valid role recommendations.',
            'roles': [] # Keep consistent return structure
        }), 500 # Use 500 Internal Server Error or maybe 200 with message

    # --- TODO: Process the content further if needed ---
    # e.g., save content and roles to database
    
    print(f"Recommended roles data: {recommended_roles_data}")

    # Return roles data to the frontend
    return jsonify({
        'message': 'Content received and roles recommended successfully',
        'roles': recommended_roles_data # Return the list of dicts
        }), 200 