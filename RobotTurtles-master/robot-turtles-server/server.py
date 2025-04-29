from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
from flask_swagger_ui import get_swaggerui_blueprint
import torch
import sys
import os
import numpy as np
import random
import uuid
import traceback
import json

# Debug information
print("Starting server...")
print(f"Current working directory: {os.getcwd()}")
print(f"Python path: {sys.path}")

# Add parent directory to sys.path to import models
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
try:
    print("Trying to import ImprovedPolicy and ImprovedEnv...")
    from improved_training import ImprovedPolicy, ImprovedEnv
    print("Successfully imported ImprovedPolicy and ImprovedEnv")
    USING_MOCK = False
except ImportError as e:
    print(f"Warning: Could not import ImprovedPolicy or ImprovedEnv: {e}")
    print("Creating mock classes...")
    USING_MOCK = True
    
    # Mock environment class for when real models can't be imported
    class ImprovedEnv:
        def __init__(self):
            print("Initializing mock environment...")
            self.agent_pos = [7, 3]
            self.agent_dir = 0
            self.target_pos = [0, 3]
            self.walls = [[2, 1], [2, 2], [2, 5], [2, 6], [4, 1], [4, 2], [4, 5], [4, 6]]
            self.done = False
        
        def reset(self):
            print("Resetting mock environment...")
            self.agent_pos = [7, 3]
            self.agent_dir = 0
            self.done = False
            return self.get_state()
            
        def get_state(self):
            print("Getting mock state...")
            return {
                'board': [[0] * 8 for _ in range(8)],
                'agent_pos': self.agent_pos,
                'agent_dir': self.agent_dir,
                'target_pos': self.target_pos,
                'walls': self.walls
            }
            
        def step(self, action):
            print(f"Mock environment executing action: {action}")
            if action == 0:  # Forward
                if self.agent_dir == 0:  # Up
                    self.agent_pos[0] = max(0, self.agent_pos[0] - 1)
                elif self.agent_dir == 1:  # Right
                    self.agent_pos[1] = min(7, self.agent_pos[1] + 1)
                elif self.agent_dir == 2:  # Down
                    self.agent_pos[0] = min(7, self.agent_pos[0] + 1)
                elif self.agent_dir == 3:  # Left
                    self.agent_pos[1] = max(0, self.agent_pos[1] - 1)
            elif action == 1:  # Turn left
                self.agent_dir = (self.agent_dir - 1) % 4
            elif action == 2:  # Turn right
                self.agent_dir = (self.agent_dir + 1) % 4
            
            # Check if target reached
            if self.agent_pos == self.target_pos:
                self.done = True
                reward = 10
            else:
                reward = -1
                
            return self.get_state(), reward, self.done, {}
    
    # Mock policy class
    class ImprovedPolicy:
        def __init__(self):
            print("Initializing mock policy...")
            
        def load_state_dict(self, state_dict):
            print("Loading mock state...")
            
        def eval(self):
            print("Setting mock policy to evaluation mode...")
            
        def __call__(self, obs):
            print("Executing mock policy prediction...")
            # Return random action probabilities and value
            logits = np.random.randn(4)
            value = np.random.randn(1)
            return torch.tensor(logits), torch.tensor(value)

app = Flask(__name__, static_url_path='/static')
CORS(app)  # Enable CORS

# Swagger configuration
SWAGGER_URL = '/api/docs'
API_URL = '/static/swagger.json'

# Configure Swagger UI
swagger_ui_blueprint = get_swaggerui_blueprint(
    SWAGGER_URL,
    API_URL,
    config={
        'app_name': "Robot Turtles API"
    }
)
app.register_blueprint(swagger_ui_blueprint, url_prefix=SWAGGER_URL)

# Store game sessions
game_sessions = {}

# Get model path
MODEL_PATH = os.environ.get('MODEL_PATH', '../improved_model/improved_model.pt')

# Load AI model
try:
    print(f"Trying to load model: {MODEL_PATH}")
    if os.path.exists(MODEL_PATH):
        model = torch.load(MODEL_PATH)
        if isinstance(model, dict) and 'model_state_dict' in model:
            # If checkpoint format, load state_dict
            policy = ImprovedPolicy()
            policy.load_state_dict(model['model_state_dict'])
            model = policy
        model.eval()
        print(f"Successfully loaded model: {MODEL_PATH}")
    else:
        print(f"Model file doesn't exist: {MODEL_PATH}")
        model = ImprovedPolicy()
        model.eval()
except Exception as e:
    print(f"Model loading failed: {e}")
    traceback.print_exc()
    # Create an empty model as fallback
    model = ImprovedPolicy()
    model.eval()

# Create the Swagger specification JSON
@app.route('/static/swagger.json')
def swagger_spec():
    swagger_doc = {
        "openapi": "3.0.0",
        "info": {
            "title": "Robot Turtles API",
            "description": "API for the Robot Turtles game",
            "version": "1.0.0"
        },
        "servers": [
            {
                "url": "/",
                "description": "Local server"
            }
        ],
        "paths": {
            "/api/games/new": {
                "post": {
                    "summary": "Create a new game",
                    "description": "Creates a new game session",
                    "requestBody": {
                        "required": True,
                        "content": {
                            "application/json": {
                                "schema": {
                                    "type": "object",
                                    "properties": {
                                        "players": {
                                            "type": "integer",
                                            "description": "Number of players"
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": "Game created successfully",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "type": "object",
                                        "properties": {
                                            "id": {"type": "string"},
                                            "board": {"type": "array"},
                                            "turtlePos": {"type": "array"},
                                            "turtleDirection": {"type": "integer"},
                                            "jewelPos": {"type": "array"},
                                            "walls": {"type": "array"},
                                            "status": {"type": "string"},
                                            "moves": {"type": "integer"}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            "/api/games/{game_id}/action": {
                "post": {
                    "summary": "Perform a game action",
                    "description": "Execute an action in the game",
                    "parameters": [
                        {
                            "name": "game_id",
                            "in": "path",
                            "required": True,
                            "schema": {
                                "type": "string"
                            }
                        }
                    ],
                    "requestBody": {
                        "required": True,
                        "content": {
                            "application/json": {
                                "schema": {
                                    "type": "object",
                                    "properties": {
                                        "action": {
                                            "type": "string",
                                            "enum": ["FORWARD", "LEFT", "RIGHT", "LASER"]
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": "Action performed successfully"
                        }
                    }
                }
            },
            "/api/ai/predict": {
                "post": {
                    "summary": "Get AI prediction",
                    "description": "Get AI recommendations for the next move",
                    "requestBody": {
                        "required": True,
                        "content": {
                            "application/json": {
                                "schema": {
                                    "type": "object",
                                    "properties": {
                                        "state": {
                                            "type": "object",
                                            "description": "Game state"
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": "AI prediction retrieved successfully"
                        }
                    }
                }
            },
            "/api/games/history": {
                "get": {
                    "summary": "Get game history",
                    "description": "Get history of games played",
                    "responses": {
                        "200": {
                            "description": "Game history retrieved successfully"
                        }
                    }
                }
            }
        }
    }
    return jsonify(swagger_doc)

# Root path - return API status and available endpoints
@app.route('/', methods=['GET'])
def index():
    return jsonify({
        "status": "API service running normally",
        "message": "Welcome to the Robot Turtles Game API",
        "endpoints": [
            {"path": "/api/games/new", "method": "POST", "description": "Create a new game"},
            {"path": "/api/games/<game_id>/action", "method": "POST", "description": "Perform a game action"},
            {"path": "/api/ai/predict", "method": "POST", "description": "Get AI prediction"},
            {"path": "/api/games/history", "method": "GET", "description": "Get game history"},
            {"path": "/api/docs", "method": "GET", "description": "API documentation"}
        ]
    })

# Get debug information
@app.route('/debug', methods=['GET'])
def debug_info():
    env_info = {
        "using_mock": USING_MOCK,
        "python_version": sys.version,
        "working_directory": os.getcwd(),
        "python_path": sys.path,
        "model_path": MODEL_PATH,
        "model_exists": os.path.exists(MODEL_PATH) if MODEL_PATH else False
    }
    
    # Try to add some environment class information
    if not USING_MOCK:
        try:
            env = ImprovedEnv()
            env_info["env_attributes"] = dir(env)
        except:
            env_info["env_attributes"] = "Could not retrieve"
    
    return jsonify(env_info)

# Create a new game
@app.route('/api/games/new', methods=['POST'])
def new_game():
    """Create a new game session."""
    try:
        # Handle invalid JSON
        try:
            data = request.get_json()
        except:
            return jsonify({'error': 'Invalid JSON format'}), 400
        
        # Validate input
        if not data or 'players' not in data:
            return jsonify({'error': 'Missing players field'}), 400
            
        num_players = data['players']
        if not isinstance(num_players, int) or num_players < 1 or num_players > 4:
            return jsonify({'error': 'Invalid number of players. Must be between 1 and 4'}), 400

        # Create new game environment
        env = ImprovedEnv()
        game_id = str(uuid.uuid4())
        
        # Initialize game state
        state = env.reset()
        if isinstance(state, dict):
            game_state = state
        else:
            # If state is not a dict (e.g., numpy array), get state from env attributes
            game_state = {
                'board': [[0] * 8 for _ in range(8)],
                'agent_pos': env.agent_pos,
                'agent_dir': env.agent_dir,
                'target_pos': env.target_pos,
                'walls': env.walls
            }
            
        # Store game session
        game_sessions[game_id] = {
            'env': env,
            'moves': 0,
            'players': num_players,
            'current_player': 0,
            'status': 'ACTIVE'
        }
        
        # Convert direction number to string
        direction_map = {0: 'NORTH', 1: 'EAST', 2: 'SOUTH', 3: 'WEST'}
        turtle_direction = direction_map[game_state['agent_dir']]
        
        # Prepare response
        response = {
            'id': game_id,
            'board': game_state,
            'turtlePos': game_state['agent_pos'],
            'turtleDirection': turtle_direction,
            'jewelPos': game_state['target_pos'],
            'walls': game_state['walls'],
            'status': 'ACTIVE',
            'moves': 0
        }
        
        # Add multiplayer specific fields
        if num_players > 1:
            response['currentPlayer'] = 0
            response['players'] = num_players
            
            # In multiplayer, assign different positions for each turtle
            turtle_positions = []
            if num_players == 2:
                turtle_positions = [[0, 0], [7, 7]]  # 左上角和右下角
            elif num_players == 3:
                turtle_positions = [[0, 0], [7, 0], [3, 7]]  # 左上角、右上角和中下
            elif num_players == 4:
                turtle_positions = [[0, 0], [7, 0], [0, 7], [7, 7]]  # 四个角落
            
            response['turtlePos'] = turtle_positions
            # 在多人游戏中，将宝石放在中间位置
            response['jewelPos'] = [3, 3]
            
        print(f"Returning response: {json.dumps(response)}")
        return jsonify(response)
        
    except Exception as e:
        print(f"Error creating game: {e}")
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

# Perform an action
@app.route('/api/games/<game_id>/action', methods=['POST'])
def perform_action(game_id):
    """Perform a game action."""
    try:
        # Validate game exists
        if game_id not in game_sessions:
            return jsonify({'error': 'Game not found'}), 404
            
        # Get game session
        session = game_sessions[game_id]
        env = session['env']
        
        # Handle invalid JSON
        try:
            data = request.get_json()
        except:
            return jsonify({'error': 'Invalid JSON format'}), 400
            
        # Get and validate action
        if not data or 'action' not in data:
            return jsonify({'error': 'Missing action field'}), 400
            
        action = data['action'].upper()
        action_map = {
            'FORWARD': 0,
            'LEFT': 1,
            'RIGHT': 2,
            'LASER': 3
        }
        
        if action not in action_map:
            return jsonify({'error': 'Invalid action. Must be one of: FORWARD, LEFT, RIGHT, LASER'}), 400
            
        # Execute action
        state, reward, done, _ = env.step(action_map[action])
        
        # Update game state
        session['moves'] += 1
        if done:
            session['status'] = 'COMPLETED'
            
        # Get current state
        if isinstance(state, dict):
            game_state = state
        else:
            game_state = {
                'board': [[0] * 8 for _ in range(8)],
                'agent_pos': env.agent_pos,
                'agent_dir': env.agent_dir,
                'target_pos': env.target_pos,
                'walls': env.walls
            }
            
        # Convert direction number to string
        direction_map = {0: 'NORTH', 1: 'EAST', 2: 'SOUTH', 3: 'WEST'}
        turtle_direction = direction_map[game_state['agent_dir'] % 4]  # Ensure direction is in range 0-3
        
        # Prepare response
        response = {
            'id': game_id,
            'board': game_state,
            'turtlePos': game_state['agent_pos'],
            'turtleDirection': turtle_direction,
            'jewelPos': game_state['target_pos'],
            'walls': game_state['walls'],
            'status': session['status'],
            'moves': session['moves']
        }
        
        # Add multiplayer specific fields
        if session['players'] > 1:
            # Update current player
            current_player = session['current_player']
            
            # 检查是否有存储所有玩家位置的属性
            if not hasattr(session, 'turtle_positions'):
                # 初始化玩家位置
                num_players = session['players']
                if num_players == 2:
                    session['turtle_positions'] = [[0, 0], [7, 7]]
                elif num_players == 3:
                    session['turtle_positions'] = [[0, 0], [7, 0], [3, 7]]
                elif num_players == 4:
                    session['turtle_positions'] = [[0, 0], [7, 0], [0, 7], [7, 7]]
                else:
                    session['turtle_positions'] = [[0, 0]] * num_players
                    
                # 中间位置的宝石
                if not hasattr(session, 'jewel_pos'):
                    session['jewel_pos'] = [3, 3]
            
            # 更新当前玩家的位置
            session['turtle_positions'][current_player] = game_state['agent_pos']
            
            # 检查是否达成目标
            if game_state['agent_pos'] == session.get('jewel_pos', [3, 3]):
                session['status'] = 'COMPLETED'
                session['winner'] = current_player
            
            # 轮到下一个玩家
            session['current_player'] = (current_player + 1) % session['players']
            
            # 添加到响应
            response['currentPlayer'] = session['current_player']
            response['players'] = session['players']
            response['turtlePos'] = session['turtle_positions']
            if hasattr(session, 'jewel_pos'):
                response['jewelPos'] = session['jewel_pos']
            if hasattr(session, 'winner'):
                response['winner'] = session['winner']
            
        return jsonify(response)
        
    except Exception as e:
        print(f"Error performing action: {e}")
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

# Get AI prediction
@app.route('/api/ai/predict', methods=['POST'])
def predict():
    try:
        data = request.json
        state = data.get('state')
        
        if not state:
            return jsonify({"error": "Missing state parameter"}), 400
        
        # Convert state to tensor observation if needed
        try:
            # This is a placeholder. In a real implementation,
            # you would convert the game state to the format expected by the model
            obs = torch.zeros(64)
            
            # Get prediction from the model
            logits, _ = model(obs)
            
            # Convert logits to probabilities
            probs = torch.softmax(logits, dim=0).detach().numpy()
            
            # Map actions to names
            action_names = ["FORWARD", "LEFT", "RIGHT", "LASER"]
            
            # Find the most probable action
            best_action_idx = np.argmax(probs)
            best_action = action_names[best_action_idx]
            
            # Create probabilities dictionary
            probabilities = {action: float(prob) for action, prob in zip(action_names, probs)}
            
            return jsonify({
                "action": best_action,
                "probabilities": probabilities
            })
            
        except Exception as e:
            print(f"Prediction failed: {e}")
            traceback.print_exc()
            
            # Fallback to random prediction
            action_names = ["FORWARD", "LEFT", "RIGHT", "LASER"]
            probs = np.random.rand(4)
            probs = probs / np.sum(probs)
            
            best_action_idx = np.argmax(probs)
            best_action = action_names[best_action_idx]
            
            probabilities = {action: float(prob) for action, prob in zip(action_names, probs)}
            
            return jsonify({
                "action": best_action,
                "probabilities": probabilities,
                "warning": "Used random fallback due to prediction error"
            })
            
    except Exception as e:
        return jsonify({"error": f"Prediction failed: {str(e)}"}), 500

# Get game history
@app.route('/api/games/history', methods=['GET'])
def get_game_history():
    try:
        # Get basic info about all game sessions
        history = []
        for game_id, session in game_sessions.items():
            try:
                env = session['env']
                turtle_pos = getattr(env, 'agent_pos', [0, 0])
                status = "COMPLETED" if getattr(env, 'done', False) else "ACTIVE"
                
                history.append({
                    "id": game_id,
                    "moves": session['moves'],
                    "status": status,
                    "turtlePos": turtle_pos
                })
            except:
                history.append({
                    "id": game_id,
                    "moves": session.get('moves', 0),
                    "status": "UNKNOWN",
                    "error": "Failed to get game details"
                })
        
        return jsonify({"games": history})
    except Exception as e:
        return jsonify({"error": f"Failed to get game history: {str(e)}"}), 500

@app.route('/api/games/<game_id>', methods=['GET'])
def get_game_state(game_id):
    """Get the current state of a game."""
    try:
        # Validate game exists
        if game_id not in game_sessions:
            return jsonify({'error': 'Game not found'}), 404
            
        # Get game session
        session = game_sessions[game_id]
        env = session['env']
        
        # Get current state
        state = env.get_state() if hasattr(env, 'get_state') else None
        if isinstance(state, dict):
            game_state = state
        else:
            game_state = {
                'board': [[0] * 8 for _ in range(8)],
                'agent_pos': env.agent_pos,
                'agent_dir': env.agent_dir,
                'target_pos': env.target_pos,
                'walls': env.walls
            }
            
        # Convert direction number to string
        direction_map = {0: 'NORTH', 1: 'EAST', 2: 'SOUTH', 3: 'WEST'}
        turtle_direction = direction_map[game_state['agent_dir']]
        
        # Prepare response
        response = {
            'id': game_id,
            'board': game_state,
            'turtlePos': game_state['agent_pos'],
            'turtleDirection': turtle_direction,
            'jewelPos': game_state['target_pos'],
            'walls': game_state['walls'],
            'status': session['status'],
            'moves': session['moves']
        }
        
        # Add multiplayer specific fields
        if session['players'] > 1:
            response['currentPlayer'] = session['current_player']
            response['players'] = session['players']
            response['turtlePos'] = [game_state['agent_pos']] * session['players']
            
        return jsonify(response)
        
    except Exception as e:
        print(f"Error getting game state: {e}")
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    # Create static directory for Swagger if needed
    os.makedirs('static', exist_ok=True)
    
    # Get port from environment variable or use default 5000
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=True) 