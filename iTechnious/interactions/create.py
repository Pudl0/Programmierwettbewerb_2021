import time

import requests

from iTechnious.statics import config
from iTechnious.statics import secrets

url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/commands"
# url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/guilds/844218034666209280/commands"

commands = [
    {
        "name": "ping",
        "description": "Ein bisschen Server Last erzeugen.",
    },
    {
        "name": "detail_felder",
        "description": "Eine Beschreibung für deinen Server erstellen.",
        "options": [
            {
                "name": "feld_name",
                "description": "Was möchtest du beschreiben?",
                "type": 3,
                "required": True
            },
            {
                "name": "inhalt",
                "description": "Beschreibe drauf los!",
                "type": 3,
                "required": True
            }
        ]
    },
    {
        "name": "beschreibung",
        "description": "Lege eine Beschreibung für deinen Server fest!",
        "options": [
            {
                "name": "text",
                "description": "Schreibe drauf los!",
                "type": 3,
                "required": True
            }
        ]
    },
    {
        "name": "extra_rollen",
        "description": "Umschalter für Rollen, die sich Teilnehmer zuteilen können.",
        "options": [
            {
                "name": "rolle",
                "description": "Die Rolle, die umgeschaltet werden soll.",
                "type": 8,
                "required": True
            }
        ]
    },
    {
        "name": "alles_ist_kaputt",
        "description": "Wieder mal alles kaputt gegangen? Lass dich ein wenig aufheitern!"
    },
    {
        "name": "link",
        "description": "Hier der Link zur Wettbewerbs Übesicht!"
    }
]

# For authorization, you can use either your bot token
headers = {
    "Authorization": f"Bot {secrets.DISCORD_BOT_TOKEN}"
}

r = requests.get(url, headers=headers)

for command in r.json():
    continue
    time.sleep(3)
    r = requests.delete(url + "/" + command["id"], headers=headers)
    print("deleted command " + command["name"])
    print(r)
    print()

for json in commands:
    time.sleep(10)
    r = requests.post(url, headers=headers, json=json)
    print("created command " + json["name"])
    print(r)

exit()

r = requests.get(f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/commands", headers=headers)
print(r)
print(r.json())
