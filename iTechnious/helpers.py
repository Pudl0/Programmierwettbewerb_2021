import asyncio
import discord as discord_bot
import json
from globals import mysql, client

def admin_checker(guild_id, user_id):
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT manager_role FROM competitions WHERE `guild_id`='{str(guild_id)}'")
        role = cursor.fetchone()["manager_role"]

    guild = client.get_guild(int(guild_id))
    try:
        member = asyncio.run_coroutine_threadsafe(guild.fetch_member(user_id), client.loop).result()
    except discord_bot.errors.NotFound:
        return False

    member_roles = [str(role.id) for role in member.roles]

    if not str(role) in member_roles:
        return False
    return True

def team_getter(guild_id, user_id):
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `guild_id`={guild_id}")
        teams = cursor.fetchall()

    for team in teams:
        if user_id in json.loads(team["members"]):
            return team
    return None