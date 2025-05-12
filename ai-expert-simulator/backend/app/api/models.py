from flask import jsonify
from . import api

# TODO: Load available models from config or database in the future
AVAILABLE_MODELS = [
    {
        "id": "deepseek-ai/DeepSeek-V3",
        "name": "DeepSeek V3 (via SiliconFlow)",
        "provider": "SiliconFlow"
    },
    # Add other models here as needed
    # {
    #     "id": "openai/gpt-4", 
    #     "name": "GPT-4 (via OpenAI)",
    #     "provider": "OpenAI"
    # },
]

@api.route('/available_models', methods=['GET'])
def get_available_models():
    """Returns a list of LLM models available for simulation."""
    return jsonify(AVAILABLE_MODELS) 