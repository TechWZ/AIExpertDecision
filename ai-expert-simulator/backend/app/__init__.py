from flask import Flask
from flask_cors import CORS
from .config import config # Assuming config.py exists

def create_app(config_name):
    app = Flask(__name__)
    app.config.from_object(config[config_name])
    config[config_name].init_app(app)

    # Enable CORS for frontend requests (adjust origins in production)
    CORS(app, resources={r"/api/*": {"origins": "*"}}) 

    # Register blueprints here
    from .api import api as api_blueprint
    app.register_blueprint(api_blueprint, url_prefix='/api')

    # Other initializations (database, etc.) can go here

    return app 