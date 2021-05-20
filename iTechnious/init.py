import asyncio
import time
import discord

import pymysql
from globals import mysql, client

from statics import config as conf


def init_db():
    db_mysql = pymysql.connect(
        host=conf.DB.host,
        port=conf.DB.port,
        user=conf.DB.user,
        password=conf.DB.password
    )

    with db_mysql.cursor() as cursor:
        cursor.execute("CREATE DATABASE IF NOT EXISTS %s" % conf.DB.db)
    db_mysql.commit()
    db_mysql.close()

    with mysql.cursor() as cursor:
        cursor.execute(f"CREATE TABLE IF NOT EXISTS `competitions` ("
                       "id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                       "guild_id VARCHAR(100),"
                       "description TEXT,"
                       "custom_fields TEXT,"
                       "manager_role VARCHAR(100),"
                       "manager_chat VARCHAR(100)"
                       ")")

        while not client.is_ready():
            time.sleep(1)

        for guild in client.guilds:
            cursor.execute(f"SELECT * FROM competitions WHERE `guild_id`='{guild.id}'")
            res = cursor.fetchone()
            if res is None:
                print("Neuer Server entdeckt:", guild.name)
                role = asyncio.run_coroutine_threadsafe(guild.create_role(name="Event-Manager"), client.loop).result()
                overwrites = {
                    guild.default_role: discord.PermissionOverwrite(view_channel=False),
                    role: discord.PermissionOverwrite(view_channel=True)
                }
                text = asyncio.run_coroutine_threadsafe(guild.create_text_channel(name="event-manager", overwrites=overwrites), client.loop).result()
                cursor.execute(f"INSERT INTO competitions (guild_id, manager_role, manager_chat) VALUES ({guild.id}, {role.id}, {text.id})")

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

        print()
        mysql.commit()

    return True
