import json
from iTechnious.interactions import response
from iTechnious.helpers import admin_checker

def run(req, client=None, options=None, mysql=None):
    field = [option["value"] for option in options if option["name"] == "field_name"][0]
    content = [option["value"] for option in options if option["name"] == "content"][0]
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
        fields[field] = content
        cursor.execute(f"UPDATE competitions SET `custom_fields`='{json.dumps(fields)}' WHERE `guild_id`='{guild_id}'")
    mysql.commit()

    return {"type": 4,
            "data": {
                "tts": False,
                "content": response.get_ok(),
                "embeds": [],
                "allowed_mentions": []
                }
            }
