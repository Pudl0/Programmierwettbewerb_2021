import requests
from iTechnious.statics import config
from iTechnious.statics import secrets

url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/commands/"
url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/guilds/844218034666209280/commands"

commands = [
    {
        "name": "ping",
        "description": "Ein bisschen Server Last erzeugen.",
    },
    {
        "name": "custom_fields",
        "description": "Eine Beschreibung für deinen Server erstellen.",
        "options": [
            {
                "name": "field_name",
                "description": "Was möchtest du beschreiben?",
                "type": 3,
                "required": True
            },
            {
                "name": "content",
                "description": "Beschreibe drauf los!",
                "type": 3,
                "required": True
            }
        ]
    },
    {
        "name": "remove_custom_fields",
        "description": "Ein Beschreibungsfeld entfernen.",
        "options": [
            {
                "name": "field_name",
                "description": "Das Feld, dass du entfernen möchtest.",
                "type": 3,
                "required": True
            }
        ]
    },
    {
        "name": "description",
        "description": "Lege eine Beschreibung für deinen Server fest!",
        "options": [
            {
                "name": "value",
                "description": "Schreibe drauf los!",
                "type": 3,
                "required": True
            }
        ]
    }
]

# For authorization, you can use either your bot token
headers = {
    "Authorization": f"Bot {secrets.DISCORD_BOT_TOKEN}"
}
"""
# or a client credentials token for your app with the applications.commands.update scope
headers = {
    "Authorization": "Bearer abcdefg"
}
"""

for json in commands:
    r = requests.post(url, headers=headers, json=json)
    print(r)
    print(r.json())

exit()

r = requests.get(f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/commands", headers=headers)
print(r)
print(r.json())
