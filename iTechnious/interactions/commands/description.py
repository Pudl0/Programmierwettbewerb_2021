import json
from iTechnious.interactions import response
from iTechnious.helpers import admin_checker
def run(req, client=None, options=None, mysql=None):
    content = [option["value"] for option in options if option["name"] == "value"][0]
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
        cursor.execute(f"UPDATE competitions SET `description`='{content}' WHERE `guild_id`='{req['guild_id']}'")
    mysql.commit()

    return {"type": 4,
            "data": {
                "tts": False,
                "content": response.get_ok(),
                "embeds": [],
                "allowed_mentions": []
                }
            }
