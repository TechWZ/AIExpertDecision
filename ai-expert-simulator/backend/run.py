import os
from dotenv import load_dotenv
from app import create_app

load_dotenv() # Load environment variables from .env file

config_name = os.getenv('FLASK_CONFIG', 'development')
app = create_app(config_name)

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5001)) # Using 5001 to avoid potential conflict with frontend dev server
    app.run(host='0.0.0.0', port=port, debug=True) 