import asyncio
import json

import requests
from discord import colour

from iTechnious.helpers import admin_checker
from iTechnious.interactions import response
from iTechnious.statics import config, secrets


def run(req, client=None, options=None, mysql=None):
    rolle = int([option["value"] for option in options if option["name"] == "rolle"][0])
    guild_id = int(req["guild_id"])
    user_id = int(req["member"]["user"]["id"])

    if not admin_checker(guild_id, user_id):
        return response.get_unauthorized()

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{req['guild_id']}'")
        competition = cursor.fetchone()

        custom_roles = json.loads(competition["custom_roles"])

        try:
            custom_roles.remove(rolle)
            embed = {
                "title": "Rolle kann nicht mehr angefordert werden.",
                "description": "Die Rolle kann nun nicht mehr frei vergeben werden.",
                "color": colour.Colour.blurple().value
            }

        except ValueError:
            custom_roles.append(rolle)
            embed = {
                "title": "Rolle kann jetzt angefordert werden.",
                "description": "Jedes Mitglied kann nun die Rolle anfordern.",
                "colour": colour.Colour.green().value
            }

        cursor.execute(f"UPDATE competitions SET `custom_roles`='{custom_roles}' WHERE `guild_id`='{guild_id}'")
        mysql.commit()

    commands = [
        {
            "name": "rollen",
            "description": "Eine freigeschaltete Rolle anfordern oder entfernen.",
            "options": [
                {
                    "name": "rolle",
                    "description": "Welche Rolle darfs denn sein?",
                    "type": 8,
                    "required": True
                }
            ]
        }
    ]

    url = f"https://discord.com/api/v8/applications/{config.DISCORD_CLIENT_ID}/guilds/{guild_id}/commands"

    headers = {
        "Authorization": f"Bot {secrets.DISCORD_BOT_TOKEN}"
    }

    for command in commands:
        r = requests.post(url, headers=headers, json=command)

    return {"type": 4,
            "data": {
                "tts": False,
                "content": "",
                "embeds": [embed],
                "allowed_mentions": []
            }
            }
