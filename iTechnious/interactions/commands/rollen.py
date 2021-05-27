import asyncio
import json

import discord.errors
import requests
from discord import colour

from iTechnious.helpers import admin_checker
from iTechnious.interactions import response
from iTechnious.statics import config, secrets


def run(req, client=None, options=None, mysql=None):
    rolle = int([option["value"] for option in options if option["name"] == "rolle"][0])
    guild_id = int(req["guild_id"])
    user_id = int(req["member"]["user"]["id"])

    guild = asyncio.run_coroutine_threadsafe(client.fetch_guild(guild_id), client.loop).result()
    role = guild.get_role(rolle)
    member = asyncio.run_coroutine_threadsafe(guild.fetch_member(user_id), client.loop).result()

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{guild_id}'")
        competition = cursor.fetchone()
        custom_roles = json.loads(competition["custom_roles"])

    if rolle not in custom_roles:
        embed = {
            "title": "Rolle nicht vergebbar!",
            "description": "Damit ich diese Rolle vergeben kann, muss sie erst über /extra_rollen freigeschaltet werden!",
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

    try:
        if role not in member.roles:
            asyncio.run_coroutine_threadsafe(member.add_roles(role), client.loop).result()
            embed = {
                "title": "Rolle zugewiesen!",
                "description": f"Die Rolle **{role.name}** wurde dir zugewiesen.",
                "color": colour.Colour.blurple().value
            }
        else:
            asyncio.run_coroutine_threadsafe(member.remove_roles(role), client.loop).result()
            embed = {
                "title": "Rolle entfernt!",
                "description": f"Dir wurde die Rolle **{role.name}** entfernt.",
                "color": colour.Colour.blurple().value
            }
    except discord.errors.Forbidden:
        embed = {
            "title": "Huch!",
            "description": f"Mir wurde zwar was anderes gesagt, aber du bist anscheinend über mir im Rechtesystem.\n"
                           f"Daher kann ich dir keine Rollen geben oder entfernen!",
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
