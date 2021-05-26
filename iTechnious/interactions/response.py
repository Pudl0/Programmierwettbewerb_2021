import random
from discord import colour

ok = ["10 4",
      "Passt!",
      "Ay Ay",
      "Copy that",
      "Affirmative",
      "all right",
      "okey-dokey",
      "alles paletti",
      "jawoll",
      "Alles in Butter"]

def get_ok():
    embed = {
          "title": random.choice(ok),
          "color": colour.Colour.green().value
    }
    return {"type": 4,
            "data": {
                  "tts": False,
                  "content": "",
                  "embeds": [embed],
                  "allowed_mentions": []
            }
            }

def get_unauthorized():
    embed = {
          "title": "Fehlende Berechtigung!",
          "description": "Diesen Befehl kannst du nur verwenden, wenn du die Event-Manager Rolle hast!",
          "color": colour.Colour.red().value
    }
    return {"type": 4,
            "data": {
                  "tts": False,
                  "content": "",
                  "embeds": [embed],
                  "allowed_mentions": []
                  }
            }
