import discord
import pymysql
from statics import config

intents = discord.Intents.default()
intents.members = True
client = discord.Client(intents=intents)


mysql = pymysql.connect(
    host=config.DB.host,
    port=config.DB.port,
    user=config.DB.user,
    password=config.DB.password,
    db=config.DB.db,
    charset='utf8',
    cursorclass=pymysql.cursors.DictCursor
)