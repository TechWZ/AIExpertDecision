import os

class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'a_very_secret_key'
    # Add other common config variables here
    # Example: MONGO_URI = os.environ.get('MONGO_URI')

    @staticmethod
    def init_app(app):
        pass

class DevelopmentConfig(Config):
    DEBUG = True
    # Development specific configs
    # Example: MONGO_URI = os.environ.get('DEV_MONGO_URI') or 'mongodb://localhost:27017/ai_dev'

class ProductionConfig(Config):
    DEBUG = False
    # Production specific configs (use environment variables!)
    # Example: MONGO_URI = os.environ.get('MONGO_URI')

config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig,
    'default': DevelopmentConfig
} 