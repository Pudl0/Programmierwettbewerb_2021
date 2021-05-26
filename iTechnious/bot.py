import asyncio
import concurrent.futures
import json

import discord

import init
from globals import client, mysql
from statics import config


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
            if team["status"] == "pending":
                continue
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

        embed = discord.embeds.Embed(
            title=f"Heißen wir **{member.name}** willkommen!!",
            description=f"Willkommen bei **{member.guild.name}**!\n"
                        f"Auf diesem Server wird der ganze Wettbewerb ausgetragen.\n"
                        f"Wenn du bereits einem Team angehörst, dann hast du gerade schon eine Nachricht in deinem Team Channel bekommen.\n\n"
                        f"Möchtest du noch einem Team betreten oder eins erstellen?\n"
                        f"Dann schaue auf der Wettbewerbsseite vorbei: {config.address}/guilds/{member.guild.id}/\n\n"
                        f"Sei doch auch so gut und hinterlege deine Daten für die Veranstalter: {config.address}/personal_infos/",
            colour=discord.Colour.random()
        )
        asyncio.run_coroutine_threadsafe(member.guild.system_channel.send(f"<@{member.id}>", embed=embed), client.loop)
