#start
import discord
from discord import channel
from discord.utils import get

intents = discord.Intents.all()
bot = discord.Client(intents = intents)


#melden, Rollen
@bot.event
async def on_message(message):
    if message.author == bot.user:
        return
    prefix = "!"
    if not message.content.startswith(prefix):
        return
    message_text = message.content[len(prefix):]
    Befehl = message_text.split(" ") [0]
    if Befehl == "melde":
        rolle = rolle = get(message.guild.roles, name= "gemeldet")
        if rolle in message.author.roles:
            await message.channel.send("Du bist schon gemeldet.")
            return
        Vorname = message_text.split(" ") [1]
        Nachname = message_text.split(" ") [2]
        Teamname = message_text.split(" ") [3] 
        kanal2 = bot.get_channel(846084259454386236)
        await kanal2.send(str(Vorname) + " " + str(Nachname) + " hat sich im Team " + str(Teamname) + " angemeldet")
        await message.author.add_roles(rolle)
        perms = discord.Permissions(send_messages=True, read_messages=True)
        await message.guild.create_role(name = Teamname, permissions = perms )
        rolle = get(message.guild.roles, name= Teamname)
        await message.author.add_roles(rolle)
    if Befehl == "gibmir":
        namederrolle = message_text.split(" ") [1]
        rolle = get(message.guild.roles, name= namederrolle)
        if namederrolle == "neue Rolle":
            return
        if get(message.guild.roles, name=namederrolle):
            if rolle in message.author.roles:
                await message.channel.send(str(namederrolle)+" hast du schon")
                return
            await message.channel.send(str(namederrolle)+" wurde dir zugewiesen.")
            await message.author.add_roles(rolle)
            return
        await message.channel.send(str(namederrolle)+" gibt es nicht.")           
    if Befehl == "machweg":
        namederrolle = message_text.split(" ") [1]
        if namederrolle == "gemeldet":
            await message.channel.send("ahahaha ne")
            return
        rolle = get(message.guild.roles, name= namederrolle)
        if rolle in message.author.roles:
            await message.author.remove_roles(rolle)
            await message.channel.send(str(namederrolle)+" wurde dir weggenommen.")
            return
        await message.channel.send(str(namederrolle)+" hast du nicht.")
    if Befehl == "erstelle":
        if message.author.id == (753970172779298890):
            Rollenname = message_text.split(" ") [1]
            perms = discord.Permissions(send_messages=True, read_messages=True)
            await message.guild.create_role(name = Rollenname, permissions = perms )
            await message.channel.send(str(Rollenname)+" wurde erstellt.")
            return
        await message.channel.send("Dafür hast du nicht die nötige Berechtigung.")
            

#Begrüßung & und Erklärung
@bot.event
async def on_member_join(member):
    if member.id == bot.user.id:
        return
    kanal = bot.get_channel(845968435828752415)
    await kanal.send("Schön, dass du da bist, " + str(member.name)+ " ! Um dich anzumelden, gib einfach [!melde + dein Vorname + dein Nachname + dein Teamname] ein. Zwischen die Namen solltest du Leerzeichen setzen.")

bot.run("ODQzODk3MzYwMjk5MDY1NDA0.YKKiqQ.1eqEOm04iPQxfbGrn4e4ZODep14")