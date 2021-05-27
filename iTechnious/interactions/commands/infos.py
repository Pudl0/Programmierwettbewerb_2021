import json


def run(req, client=None, options=None, mysql=None):
    topic = [option["value"] for option in options if option["name"] == "thema"][0]
    guild_id = int(req["guild_id"])

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{guild_id}'")
        info = json.loads(cursor.fetchone()["custom_fields"])[topic]

    return {"type": 4,
            "data": {
                "tts": False,
                "content": "Das habe ich gefunden:\n%s" % info,
                "embeds": [],
                "allowed_mentions": []
                }
            }
