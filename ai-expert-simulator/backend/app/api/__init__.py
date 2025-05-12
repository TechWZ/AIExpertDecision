from flask import Blueprint

api = Blueprint('api', __name__)

# Import routes here to register them with the blueprint
from . import content_input # Example: importing routes from content_input.py 
from . import models # Import the new models endpoint file 
from . import simulation # Import the new simulation endpoint file 