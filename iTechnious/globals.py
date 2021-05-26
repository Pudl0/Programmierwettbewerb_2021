import discord
import pymysql

from statics import config

intents = discord.Intents.default()
intents.members = True
client = discord.Client(intents=intents)

db_mysql = pymysql.connect(
    host=config.DB.host,
    port=config.DB.port,
    user=config.DB.user,
    password=config.DB.password
)

with db_mysql.cursor() as cursor:
    cursor.execute("CREATE DATABASE IF NOT EXISTS %s CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" % config.DB.db)
db_mysql.commit()
db_mysql.close()

mysql = pymysql.connect(
    host=config.DB.host,
    port=config.DB.port,
    user=config.DB.user,
    password=config.DB.password,
    db=config.DB.db,
    charset='utf8mb4',
    cursorclass=pymysql.cursors.DictCursor
)