from iTechnious.helpers import admin_checker
from iTechnious.interactions import response


def run(req, client=None, options=None, mysql=None):
    content = [option["value"] for option in options if option["name"] == "text"][0]
    guild_id = int(req["guild_id"])
    user_id = int(req["member"]["user"]["id"])

    if not admin_checker(guild_id, user_id):
        return response.get_unauthorized()

    with mysql.cursor() as cursor:
        cursor.execute(f"UPDATE competitions SET `description`='{content}' WHERE `guild_id`='{req['guild_id']}'")
    mysql.commit()

    return response.get_ok()
