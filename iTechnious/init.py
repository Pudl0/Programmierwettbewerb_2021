import asyncio
import time

import discord

from globals import mysql, client
from statics import config as conf


def init_db():
    print("Datenbank wird aktualisiert...")

    with mysql.cursor() as cursor:
        cursor.execute(f"CREATE TABLE IF NOT EXISTS `competitions` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "guild_id VARCHAR(100),"
                       "description TEXT,"
                       "custom_fields TEXT,"
                       "manager_role VARCHAR(100),"
                       "manager_chat VARCHAR(100),"
                       "custom_roles TEXT"
                       ")")

        while not client.is_ready():
            time.sleep(1)

        for guild in client.guilds:
            cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{guild.id}'")
            res = cursor.fetchone()
            if res is None:
                print("Neuer Server entdeckt:", guild.name)
                role = asyncio.run_coroutine_threadsafe(guild.create_role(name="Event-Manager", colour=discord.Colour.purple()), client.loop).result()
                overwrites = {
                    guild.default_role: discord.PermissionOverwrite(view_channel=False),
                    role: discord.PermissionOverwrite(view_channel=True)
                }
                text = asyncio.run_coroutine_threadsafe(guild.create_text_channel(name="event-manager", overwrites=overwrites), client.loop).result()
                embed = discord.embeds.Embed(
                    title="Willkommen bei FlaskHACS!",
                    description="Ich bin FlaskHACS und ich bin deine Möglichkeit, deinen Hackathon ganz einfach zu verwalten.\n"
                                f"Unter {conf.address}/guilds/{guild.id}/ findest du diesen Server im Webinterface.\n"
                                f"Schicke diesen Link auch deinen Teilnehmern. Über ihn können sie ihr Team erstellen und auf den Discord kommen.\n"
                                f"Alles weitere kannst du entweder selbst entdecken, oder unter \n"
                                f"{conf.address}/about und {conf.address}/help\n"
                                f"nachlesen.\n"
                                f"**Wichtig ist**: Jeder, der die 'Event-Manager' Rolle hat, hat Admin Zugang in der Web App! "
                                f"Weise die Rolle entsprechend zu!"
                )
                asyncio.run_coroutine_threadsafe(text.send(embed=embed), client.loop).result()

                cursor.execute(f"INSERT INTO competitions (`guild_id`, `description`, `manager_role`, `manager_chat`, `custom_fields`, `custom_roles`) VALUES ('{guild.id}', ' ', '{role.id}', '{text.id}', '{{}}', '[]')")

        # cursor.execute(f"CREATE TABLE IF NOT EXISTS `joins` ("
        #                "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
        #                "user_id VARCHAR(255),"
        #                "location TEXT,"
        #                "type VARCHAR(50),"
        #                "permissions TEXT"
        #                ")")

        cursor.execute(f"CREATE TABLE IF NOT EXISTS `teams` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "name VARCHAR(255),"
                       "description TEXT,"
                       "guild_id VARCHAR(100),"
                       "members TEXT,"
                       "contact VARCHAR(255),"
                       "status VARCHAR(50),"
                       "requests TEXT,"
                       "team_voice VARCHAR(100),"
                       "team_text VARCHAR(100),"
                       "team_role VARCHAR(100)"
                       ")")

        cursor.execute("CREATE TABLE IF NOT EXISTS `userdata` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "user_id VARCHAR(100),"
                       "first_name VARCHAR(255),"
                       "last_name VARCHAR(255),"
                       "email VARCHAR(255),"
                       "class VARCHAR(100),"
                       "extras TEXT"
                       ")")

        print()
        mysql.commit()

    return True
