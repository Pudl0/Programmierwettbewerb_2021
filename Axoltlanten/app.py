from typing import Text
import discord
from discord import message
from discord import client
from discord import permissions
from discord.channel import CategoryChannel
from discord.enums import try_enum
from discord.ext import commands
import random
from discord.utils import get
from discord.ext.commands import has_permissions, MissingPermissions
import os
import re
import requests
import json
from jokeapi import Jokes

intents = discord.Intents.all()

bot = commands.Bot(command_prefix='!',intents = intents)


# class

#Startbestätigung
@bot.event
async def on_ready():
    print("erfolgreich gestartet")
    bot.get_channel(847452918600040478)



#Wilkommensnachricht
@bot.event
async def on_member_join(member):
    print("erfolgreich gemerkt")
    await bot.get_channel(847452918600040478).send(f"{member.name} ist zu uns gestoßen, setz dich hin und trink einen Tee")
    


#Abschiedsnachricht
@bot.event
async def on_member_remove(member):
    print("erfolgreich gemerkt")
    await bot.get_channel(847452918600040478).send(f"{member.name} ist gegangen :-(")


#Team zuweisen
@bot.command()
async def gib(ctx, arg, arg2):
    try:        
        if arg == "Team":
                print("Rolle zugewiesen")
                guild = ctx.guild
                authour = ctx.message.author
                role = discord.utils.get(authour.guild.roles, name= arg+arg2)
                await authour.add_roles(role)
                await ctx.send ("Wurde dir zugewiesen :dizzy:")
        else:
            await ctx.send ("Hat nicht geklappt. Versuche es nocheinmal!")
    except:
            await ctx.send ("Hat nicht geklappt. Versuche es nocheinmal!")




#Team erstellen
@bot.command()
async def erstellTeam(ctx, arg):

        try:
            print("Rolle Erstellt")
            guild = ctx.guild
            authour = ctx.message.author
            role = await guild.create_role(name='Team'+ arg, permissions=discord.Permissions(0), colour=discord.Colour(0xff550), hoist=True)
            await ctx.send ("wurde erstellt :sunglasses:")
            await authour.add_roles(role)
            roleid = role.id


            overwrites = {
                guild.default_role: discord.PermissionOverwrite(read_messages=False),
                guild.get_role(roleid): discord.PermissionOverwrite(read_messages=True)
            }
            await ctx.guild.create_text_channel('Team'+ arg, overwrites=overwrites)

        except:
                await ctx.send ("Da hat etwas nicht geklappt. Versuche es nochmal")

#Hilfe Embed
@bot.command()
async def Hilfe(ctx):
    embed = discord.Embed(
        title="**__Command_Hilfe__**", 
        description="Du kennst nicht alle Commands? Hier sind sie! :sunglasses:",
        colour = discord.Colour.gold()
   )

    embed.add_field(name="**!erstellTeam**", value="Erstelle Teams, indem du **'!erstellTeam *dein_Teamname*'** in das Textfeld eingibst.", inline=False)
    embed.add_field(name="**!gibTeam**", value="Weise dir ein Team zu, indem du **'!gib Team *dein_Teamname*'** in das Textfeld eingibst.", inline=False)
    embed.add_field(name="**!meldan**", value=" Melde dich an, indem du **'!meldan *deinName* *deinTeamname* *deineKlasse* *deineSchule*'** in das Textfeld eingibst.", inline=False)
    embed.add_field(name="**!anmeldungen**", value="Sieh alle bisherigen Anmeldungen, indem du ** *!anmeldungen* ** in das Textfeld eingibst. :warning: **Dieses Feature kann nur im Adminchannel genutzt werden!** :warning:", inline=False)
    embed.add_field(name="**!Witz**", value= "Der Bot schreibt einen random Programmier-Witz, indem du **'!Witz'** in das Textfeld eingibst.", inline=False)
    await ctx.send(embed=embed)


#Anmelden 
@bot.command()
async def meldan(ctx, Name, Teamname, Klasse, Schule):
    f = open("Anmeldungen.txt", "a")
    print("wird angemeldet")
    with open("Anmeldungen.txt", "a"):
        f.write("Name: "+Name)
        f.write(", Teamname: "+Teamname)
        f.write(", Klasse: "+ Klasse)
        f.write(", Schule: "+Schule)
        f.write( "\n")
        
        await ctx.send ("Fertig :thumbsup:")
        await ctx.send ("Du wurdes als: "+Name+", im Team: "+Teamname+", aus der Klasse: "+Klasse+", von der Schule: "+ Schule+" angemeldet!")

    f.close()


#Anmeldungen an Admin senden
@bot.command(pass_context = True)
async def anmeldungen(ctx):
        await bot.get_channel(847452960740999198).send("Hier sind alle aktuellen Anmeldungen")
        await bot.get_channel(847452960740999198).send(file=discord.File(r'C:\Users\Tino Brinker\Desktop\Programmierwettbewerb Axoltlanten\Anmeldungen.txt'))
        

#Witz senden
@bot.command(pass_context = True)
async def Witz(ctx):
        j = Jokes()
        joke = j.get_joke(category=['programming'], blacklist=['nsfw', 'racist'])
        if joke["type"] == "single": 
            await ctx.send (joke["joke"])
        else:
            await ctx.send (joke["setup"])
            await ctx.send (joke["delivery"])
            
 

bot.run("TOKEN")

# Es wurde der Sv443s-JokeApi-Python-Wrapper verwendet. -> https://github.com/thenamesweretakenalready/Sv443s-JokeAPI-Python-Wrapper#readme-