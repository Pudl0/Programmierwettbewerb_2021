import discord
import json
import asyncio
import init
import concurrent.futures

from globals import client, mysql

@client.event
async def on_ready():
    print("Bot gestartet!")

    executor = concurrent.futures.ThreadPoolExecutor(max_workers=3)
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(executor, init.init_db)

@client.event
async def on_guild_join(guild):
    executor = concurrent.futures.ThreadPoolExecutor(max_workers=3)
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(executor, init.init_db)

@client.event
async def on_member_join(member):
    with mysql.cursor() as cursor:
        cursor.execute("SELECT * FROM teams")
        teams = cursor.fetchall()
        for team in teams:
            if member.id in json.loads(team["members"]):
                role = member.guild.get_role(int(team["team_role"]))
                asyncio.run_coroutine_threadsafe(member.add_roles(role), client.loop)

                text = member.guild.get_channel(int(team["team_text"]))
                embed = discord.embeds.Embed(
                    title=f"Willkommen {member.name}!",
                    description="Du hast gerade automatisch deine Teamrolle bekommen, "
                                "da du dich zuvor über die App angemeldet hast.\n"
                                "Hier ist dein Team bereich, den könnt nur ihr sehen!",
                    colour=discord.Colour.green()
                )
                asyncio.run_coroutine_threadsafe(text.send(f"<@{member.id}>", embed=embed), client.loop)
