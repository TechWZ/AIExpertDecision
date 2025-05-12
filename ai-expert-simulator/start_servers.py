import subprocess
import os
import sys
import signal
import time # Import the time module

# --- Configuration ---
backend_dir = "backend"
frontend_dir = "frontend"
venv_name = "venv"
# --- End Configuration ---

# Determine platform specific paths and commands
python_executable = ""
npm_executable = "npm" # Assumes npm is in PATH, use 'npm.cmd' on Windows if needed explicitly

if sys.platform == "win32":
    python_executable = os.path.join(backend_dir, venv_name, "Scripts", "python.exe")
    # npm might need .cmd on Windows, but often works without
    # Explicitly use npm.cmd on Windows
    npm_executable = "npm.cmd" 
elif sys.platform == "linux" or sys.platform == "darwin":
    python_executable = os.path.join(backend_dir, venv_name, "bin", "python")
else:
    print(f"Unsupported platform: {sys.platform}")
    sys.exit(1)

# Commands to execute
backend_cmd = [python_executable, "run.py"]
frontend_cmd = [npm_executable, "run", "dev"] # Use list format for Popen

# Check if python executable exists
if not os.path.exists(python_executable):
    print(f"Error: Python executable not found at {python_executable}")
    print("Please ensure the backend virtual environment ('{venv_name}') exists and is correctly set up.")
    sys.exit(1)

print("Starting servers...")
backend_process = None
frontend_process = None

try:
    # Start Backend Server
    print(f"Starting backend in {backend_dir}...")
    backend_process = subprocess.Popen(
        backend_cmd, 
        cwd=backend_dir,
    )
    print(f"Backend PID: {backend_process.pid}")

    # Start Frontend Server
    print(f"Starting frontend in {frontend_dir}...")
    frontend_process = subprocess.Popen(
        frontend_cmd, 
        cwd=frontend_dir,
    )
    print(f"Frontend PID: {frontend_process.pid}")

    print("\n--- Server Output (will appear mixed below) ---")
    print("Press Ctrl+C to stop both servers.")

    # Keep the main script alive until Ctrl+C using time.sleep loop
    while True:
        time.sleep(1) # Sleep for 1 second, loop continues until KeyboardInterrupt

except KeyboardInterrupt:
    print("\nCtrl+C detected. Stopping servers...")

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    # Terminate processes on exit
    if frontend_process and frontend_process.poll() is None:
        print("Stopping frontend server...")
        frontend_process.terminate()
        frontend_process.wait() # Wait for process to terminate
    if backend_process and backend_process.poll() is None:
        print("Stopping backend server...")
        backend_process.terminate()
        backend_process.wait() # Wait for process to terminate
    print("Servers stopped.") 