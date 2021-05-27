from discord import colour

from iTechnious.statics import config

def run(req, client=None, options=None, mysql=None):
    guild_id = int(req["guild_id"])

    embed = {
        "title": "Übersichts-Link",
        "description": "Hier ist der Link zur Webapp. Viel Spaß!\n"
                       f"{config.address}/guilds/{guild_id}/",
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