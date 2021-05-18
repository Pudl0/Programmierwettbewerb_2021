import _thread
import os
import json
import smtplib
import ssl
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import formatdate

from flask import Flask, redirect, render_template, request, url_for
from flask_discord import DiscordOAuth2Session, Unauthorized, requires_authorization

import init
import statics.config as config
import statics.secrets as secrets
from bot import client
from globals import mysql

init.init_db()


app = Flask("FlaskHACS", static_folder='web/public', static_url_path='', template_folder='web/templates')
app.secret_key = secrets.flask_secret_key
app.config['TEMPLATES_AUTO_RELOAD'] = True

# os.environ["FLASK_ENV"] = "development"
os.environ["OAUTHLIB_INSECURE_TRANSPORT"] = "true"

app.config["DISCORD_CLIENT_ID"] = config.DISCORD_CLIENT_ID
app.config["DISCORD_CLIENT_SECRET"] = secrets.DISCORD_CLIENT_SECRET
app.config["DISCORD_REDIRECT_URI"] = "http://itechnious.ddns.net:7789/callback/"
app.config["DISCORD_BOT_TOKEN"] = secrets.DISCORD_BOT_TOKEN

discord = DiscordOAuth2Session(app)

print("################################")
print("#  FlaskHACS Hackathon Manager #")
print("#    -----------------------   #")
print("#           Soenke K.          #")
print("################################")


def admin_checker(guild_id, user_id):
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT manager_role FROM competitions WHERE `guild_id`='{str(guild_id)}'")
        role = cursor.fetchone()["manager_role"]

    guild = client.get_guild(int(guild_id))
    member = guild.get_member(user_id)
    member_roles = [str(role.id) for role in member.roles]

    if not str(role) in member_roles:
        return False
    return True

def team_getter(guild_id, user_id):
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `guild_id`={guild_id}")
        teams = cursor.fetchall()

    for team in teams:
        if user_id in json.loads(team["members"]):
            return team
    return None


# Front-End
@app.route("/")
def mainPage():
    return render_template("index.html", bot=client)

@app.route("/guilds/<guild_id>/")
@requires_authorization
def guild_overview(guild_id):
    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `guild_id`={str(guild_id)}")
        teams = cursor.fetchall()
        cursor.execute(f"SELECT manager_role FROM competitions WHERE `guild_id`='{str(guild_id)}'")
        role = cursor.fetchone()["manager_role"]

    is_admin = False
    guild = client.get_guild(int(guild_id))
    member = guild.get_member(discord.fetch_user().id)
    member_roles = [str(role.id) for role in member.roles]
    user_id = discord.fetch_user().id

    if str(role) in member_roles:
        is_admin = True

    own_team = team_getter(int(guild_id), discord.fetch_user().id)

    for team in teams:
        if str(user_id) in str(json.loads(team["joins"])):
            own_team = team["id"]
            break

    return render_template("guild.html", guild=guild, teams=teams, admin=is_admin, own_team=own_team)

@app.route("/guilds/<guild_id>/admin/")
@requires_authorization
def guild_admin(guild_id):
    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        return redirect(url_for("mainPage"))

    if not admin_checker(guild_id, discord.fetch_user().id):
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `guild_id`={str(guild_id)}")
        teams = cursor.fetchall()

    print(teams)
    return render_template("guild_admin.html", guild=client.get_guild(int(guild_id)), teams=teams)

@app.route("/guilds/<guild_id>/admin/confirm/<team_id>/")
@requires_authorization
def confirm_team(guild_id, team_id):
    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        return redirect(url_for("mainPage"))

    if not admin_checker(guild_id, discord.fetch_user().id):
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"UPDATE teams SET `status` = 'valid' WHERE `guild_id`='{str(guild_id)}' AND `id`='{str(team_id)}'")
        mysql.commit()

    return redirect(url_for("guild_admin", guild_id=guild_id))

@app.route("/guilds/<guild_id>/create_team/")
@requires_authorization
def create_team(guild_id):
    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        return redirect(url_for("mainPage"))

    return render_template("create_team.html", guild=client.get_guild(int(guild_id)))

@app.route("/post_create_team/", methods=["POST"])
@requires_authorization
def post_create_team():
    print(request.form)
    form = request.form

    with mysql.cursor() as cursor:
        members = json.dumps([discord.fetch_user().id])
        cursor.execute(f"INSERT INTO teams (`name`, `description`, `guild_id`, `members`, `contact`, `status`, `joins`) VALUES ('{form['team_name']}', '{form['team_des']}', '{form['guild_id']}', '{members}', '{form['email']}', 'pending', '{json.dumps([])}')")
    mysql.commit()

    return render_template("create_team_success.html", guild=client.get_guild(int(request.form["guild_id"])))

@app.route("/guilds/<guild_id>/join/<team_id>/")
@requires_authorization
def join_team(guild_id, team_id):
    try:
        int(guild_id)
    except ValueError as e:
        print(e)
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        print("guild not found")
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT joins FROM teams WHERE `id`='{team_id}'")
        joins = cursor.fetchone()["joins"]
        joins = json.loads(joins)
        joins.append(discord.fetch_user().id)

        joins = json.dumps(joins)

        cursor.execute(f"UPDATE teams SET `joins`='{joins}' WHERE `id`='{team_id}'")

        mysql.commit()

    return redirect(url_for("guild_overview", guild_id=guild_id))

@app.route("/invite/", methods=["POST"])
@requires_authorization
def invite_member():
    form = request.form
    guild_id = form["guild_id"]
    team_id = form["team_id"]

    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        joins = cursor.fetchone()["joins"]
        joins = json.loads(joins)
        joins.append(form["user_name"])

        joins = json.dumps(joins)

        cursor.execute(f"UPDATE teams SET `joins`='{joins}' WHERE `id`='{team_id}'")

        mysql.commit()

    return redirect(url_for("guild_overview", guild_id=guild_id))

@app.route("/login/")
def login():
    return discord.create_session()
@app.errorhandler(Unauthorized)
def redirect_unauthorized(e):
    return redirect(url_for("login"))

@app.route("/callback/")
def callback():
    discord.callback()
    return redirect(url_for("mainPage"))

@app.route("/contact/")
def contact():
    return render_template("contact.html")

@app.route("/post_contact/", methods=["POST"])
def contactPost():
    print(request.form)
    message = request.form["message"]
    name = request.form["name"]
    emailaddr = request.form["email"]

    msg = MIMEMultipart()
    msg.attach(MIMEText(
        name + " schrieb uns über das Kontaktformular:\n\n" + message + "\n\nEnde der Nachricht. Falls du antworten möchtest, hier die E-Mail Adresse: " + emailaddr))

    msg["Subject"] = "Nachricht von " + name
    msg["From"] = "FlaskHACS <%s>" % config.Mail.user + "@" + config.Mail.host
    msg["To"] = config.Mail.reciever
    msg["Date"] = formatdate(localtime=True)

    print(request.form)

    with smtplib.SMTP_SSL(config.Mail.host, config.Mail.port, context=ssl.create_default_context()) as server:
        server.login(config.Mail.user, config.Mail.password)
        server.sendmail(config.Mail.user, config.Mail.reciever, msg.as_string())
        server.close()

    return "Deine Nachricht wurde gesendet. Wir werden dich demnächst kontaktieren.<br>Du wirst automatisch weitergeleitet...<meta http-equiv='refresh' content='3; URL=/'>"


@app.after_request
def add_header(r):
    """
    Add headers to both force latest IE rendering engine or Chrome Frame,
    and also to cache the rendered page for 10 minutes.
    Credit: https://stackoverflow.com/a/34067710
    """
    r.headers["Cache-Control"] = "no-cache, no-store, must-revalidate"
    r.headers["Pragma"] = "no-cache"
    r.headers["Expires"] = "0"
    r.headers['Cache-Control'] = 'public, max-age=0'
    return r


token = tuple([secrets.DISCORD_BOT_TOKEN])
print(token)
_thread.start_new_thread(client.run, token, {})

if __name__ == "__main__":
    app.run(host=config.bind, port=config.web_port)
