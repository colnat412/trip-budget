import os, requests
from dotenv import load_dotenv
load_dotenv()
key = os.getenv("GEMINI_API_KEY")
r = requests.get(f"https://generativelanguage.googleapis.com/v1beta/models?key={key}")
for m in r.json().get('models', []):
    print(m['name'])
