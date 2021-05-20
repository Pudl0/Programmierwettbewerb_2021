import discord.ext.commands

import init

from globals import client

@client.event
async def on_ready():
    print("Bot gestartet!")
    # init.init_db()
