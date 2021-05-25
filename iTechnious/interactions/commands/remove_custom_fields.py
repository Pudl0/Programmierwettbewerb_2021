import json

import requests

from iTechnious.interactions import response
from iTechnious.helpers import admin_checker
from iTechnious.statics import config, secrets

def run(req, client=None, options=None, mysql=None):
    field = [option["value"] for option in options if option["name"] == "field_name"][0]
    guild_id = int(req["guild_id"])
    user_id = int(req["member"]["user"]["id"])

    if not admin_checker(guild_id, user_id):
        return {"type": 4,
                "data": {
                    "tts": False,
                    "content": "Ähm, nein...du musst Verwalter sein!",
                    "embeds": [],
                    "allowed_mentions": []
                    }
                }

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{guild_id}'")
        guild = cursor.fetchone()
        fields = json.loads(guild["custom_fields"])
        try:
            del fields[field]
        except KeyError:
            pass
        cursor.execute(f"UPDATE competitions SET `custom_fields`='{json.dumps(fields)}' WHERE `guild_id`='{guild_id}'")
    mysql.commit()

    url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/guilds/{guild_id}/commands"

    choices = []
    for key in fields.keys():
        choices.append(
            {
                "name": key,
                "value": key
            }
        )

    commands = [
        {
            "name": "infos",
            "description": "Bekomme die Infos, die wir haben!",
            "options": [
                {
                    "name": "thema",
                    "description": "Worüber möchtest du Infos?",
                    "type": 3,
                    "required": True,
                    "choices": choices
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
                    "required": True,
                    "choices": choices
                }
            ]
        }
    ]

    headers = {
        "Authorization": f"Bot {secrets.DISCORD_BOT_TOKEN}"
    }

    for command in commands:
        r = requests.post(url, headers=headers, json=command)
        print(r, r.json())

    return {"type": 4,
            "data": {
                "tts": False,
                "content": response.get_ok(),
                "embeds": [],
                "allowed_mentions": []
                }
            }
