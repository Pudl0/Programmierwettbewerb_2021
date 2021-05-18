import discord.ext.commands

from globals import client

@client.event
async def on_ready():
    print("Bot gestartet!")
