from flask import request, jsonify
from . import api
from ..services.llm_service import run_simulation_for_role
import concurrent.futures

@api.route('/run_simulation', methods=['POST'])
def run_simulation():
    """ 
    Receives simulation parameters (content, roles with prompts, model)
    and runs the simulation for each role, returning the collected results.
    """
    data = request.get_json()
    
    # --- Input Validation ---
    required_fields = ['content', 'selected_roles']
    if not data or not all(field in data for field in required_fields):
        return jsonify({'error': 'Missing required fields: content, selected_roles (list of objects)'}), 400
        
    if not isinstance(data['selected_roles'], list) or not data['selected_roles']:
        return jsonify({'error': 'selected_roles must be a non-empty list'}), 400

    for role_data in data['selected_roles']:
        if not isinstance(role_data, dict) or \
           'role_name' not in role_data or \
           'prompt' not in role_data or \
           'model_id' not in role_data:
            return jsonify({'error': 'Each item in selected_roles must be an object with role_name, prompt, and model_id'}), 400

    content = data['content']
    selected_roles_with_models = data['selected_roles'] # List like [{'role_name': 'X', 'prompt': 'Y', 'model_id': 'Z'}]
    
    print(f"Received simulation request for {len(selected_roles_with_models)} roles with individual models.")
    
    results = {}
    
    # --- Run simulations in parallel --- 
    with concurrent.futures.ThreadPoolExecutor(max_workers=5) as executor:
        future_to_role = { 
            executor.submit(
                run_simulation_for_role, 
                content, 
                role['role_name'], 
                role['prompt'], 
                role['model_id']
            ): role['role_name'] 
            for role in selected_roles_with_models
        }
        
        # Collect results as they complete
        for future in concurrent.futures.as_completed(future_to_role):
            role_name = future_to_role[future]
            try:
                result_text = future.result() # Get result from the future
                results[role_name] = result_text
                print(f"Completed simulation for role: {role_name}")
            except Exception as exc:
                print(f'Role {role_name} generated an exception: {exc}')
                results[role_name] = f"Error during simulation: {exc}"

    print(f"Finished all simulations. Returning {len(results)} results.")
    
    return jsonify({
        'message': 'Simulations completed',
        'results': results
    }), 200 