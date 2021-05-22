import asyncio
import discord as discord_bot
import _thread
import os
import json
import logging
import smtplib
import ssl
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import formatdate

from flask import Flask, redirect, render_template, request, url_for, flash
from flask_discord import DiscordOAuth2Session, Unauthorized, requires_authorization

import init
import statics.config as config
import statics.secrets as secrets
from bot import client
from globals import mysql

# logging.basicConfig(level=logging.DEBUG)


app = Flask("FlaskHACS", static_folder='web/public', static_url_path='', template_folder='web/templates')
app.secret_key = secrets.flask_secret_key
app.config['TEMPLATES_AUTO_RELOAD'] = True

# os.environ["FLASK_ENV"] = "development"
os.environ["OAUTHLIB_INSECURE_TRANSPORT"] = "true"

app.config["DISCORD_CLIENT_ID"] = config.DISCORD_CLIENT_ID
app.config["DISCORD_CLIENT_SECRET"] = secrets.DISCORD_CLIENT_SECRET
app.config["DISCORD_REDIRECT_URI"] = f"{config.address}/callback/"
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
    try:
        member = asyncio.run_coroutine_threadsafe(guild.fetch_member(user_id), client.loop).result()
    except discord_bot.errors.NotFound:
        return False

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
        try:
            cursor.execute(f"SELECT manager_role FROM competitions WHERE `guild_id`='{str(guild_id)}'")
            role = cursor.fetchone()["manager_role"]
        except TypeError:
            flash("Es gab ein Problem beim Abrufen der Daten.\n"
                  "Sollte der Fehler weiterhin auftreten, versuche den Bot vom Server zu kicken und dann neu anzumelden.\n"
                  "Alternativ kannst du dich auch an den Support wenden.")
            return redirect(url_for("mainPage"))

    is_admin = False
    guild = client.get_guild(int(guild_id))
    member = guild.get_member(discord.fetch_user().id)
    if member is not None:
        member_roles = [str(role.id) for role in member.roles]
    else:
        member_roles = []
    user_id = discord.fetch_user().id

    if str(role) in member_roles:
        is_admin = True

    own_team = team_getter(int(guild_id), discord.fetch_user().id)
    joins = []
    if own_team is not None:
        with mysql.cursor() as cursor:
            cursor.execute(f"SELECT requests FROM teams WHERE `id`='{own_team['id']}'")
            i = 0
            for join in json.loads(cursor.fetchone()["requests"]):
                user = asyncio.run_coroutine_threadsafe(client.fetch_user(int(join)), client.loop).result()
                if user is None:
                    flash(f"Benutzer mit ID {join} konnte nicht geladen werden!")
                else:
                    name = user.name
                    joins.append({"name": name, "id": join})
                    i += 1

    for team in teams:
        if str(user_id) in str(json.loads(team["requests"])):
            own_team = team["id"]
            break

    return render_template("guild.html", guild=guild, teams=teams, admin=is_admin, own_team=own_team, joins=joins)

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

    joins = {}
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams")
        for team in cursor.fetchall():
            joins[team["id"]] = []
            joins_list = json.loads(team["requests"])
            for join in joins_list:
                user = asyncio.run_coroutine_threadsafe(client.fetch_user(int(join)), client.loop).result()
                if user is None:
                    flash(f"Benutzer mit ID {join} konnte nicht geladen werden!")
                else:
                    username = user.name
                    joins[team["id"]].append({"name": username, "id": join})

    print(joins)
    return render_template("guild_admin.html", guild=client.get_guild(int(guild_id)), teams=teams, joins=joins)

@app.route("/create_team/", methods=["POST"])
@requires_authorization
def post_create_team():
    print(request.form)
    form = request.form

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT name FROM teams WHERE `guild_id`='{form['guild_id']}'")
        if form["team_name"] in [team["name"] for team in cursor.fetchall()]:
            flash("Dieses Team existiert bereits!")
            return redirect(url_for("guild_overview", guild_id=form["guild_id"]))
        if request.form["admin"] == "false":
            members = json.dumps([discord.fetch_user().id])
        else:
            members = []
        cursor.execute(f"INSERT INTO teams (`name`, `description`, `guild_id`, `members`, `contact`, `status`, `requests`) VALUES ('{form['team_name']}', '{form['team_des']}', '{form['guild_id']}', '{members}', '{form['email']}', 'pending', '{json.dumps([])}')")
    mysql.commit()

    # return render_template("create_team_success.html", guild=client.get_guild(int(request.form["guild_id"])))
    flash("Team wurde ersellt! Bevor es weitergeht muss es von einem Admin freigeschaltet werden")

    if request.form["admin"] == "true":
        return redirect(url_for("guild_admin", guild_id=form["guild_id"]))
    else:
        return redirect(url_for("guild_overview", guild_id=form["guild_id"]))

@app.route("/confirm_team/", methods=["POST"])
@requires_authorization
def confirm_team():
    guild_id = request.form["guild_id"]
    team_id = request.form["team_id"]

    try:
        guild_id = int(guild_id)
    except ValueError:
        flash("Ungültige Server ID")
        return redirect(url_for("mainPage"))

    if guild_id not in [int(guild.id) for guild in client.guilds]:
        flash("Kein Event Server")
        return redirect(url_for("mainPage"))

    if not admin_checker(guild_id, discord.fetch_user().id):
        flash("Keine Berechtigung")
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"UPDATE teams SET `status` = 'valid' WHERE `guild_id`='{str(guild_id)}' AND `id`='{str(team_id)}'")

        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()
        guild = client.get_guild(guild_id)

        category = asyncio.run_coroutine_threadsafe(guild.create_category(team["name"]), client.loop).result()
        role = asyncio.run_coroutine_threadsafe(guild.create_role(name=team["name"], hoist=True, mentionable=True), client.loop).result()

        print(guild.default_role)

        asyncio.run_coroutine_threadsafe(category.set_permissions(guild.default_role, view_channel=False), client.loop)
        asyncio.run_coroutine_threadsafe(category.set_permissions(role, view_channel=True), client.loop)

        text = asyncio.run_coroutine_threadsafe(category.create_text_channel(team["name"]), client.loop).result()
        voice = asyncio.run_coroutine_threadsafe(category.create_voice_channel(team["name"]), client.loop).result()

        cursor.execute(f"UPDATE teams SET `team_text`='{text.id}', `team_voice`='{voice.id}', `team_role`='{role.id}' WHERE `id`='{team_id}'")

        mysql.commit()

    members = json.loads(team["members"])
    if members:
        user = asyncio.run_coroutine_threadsafe(client.fetch_user(members[0]), client.loop).result()

        embed = discord_bot.embeds.Embed(
            title="Team erstellt",
            description=f"Ein wunderschöner Tag. Dein Team '{team['name']}' wurde gerade freigeschaltet!"
        )
        asyncio.run_coroutine_threadsafe(user.send(embed=embed), client.loop)

    flash("Das Team wurde freigeschaltet")

    return redirect(url_for("guild_admin", guild_id=guild_id))


@app.route("/delete_team/", methods=["POST"])
@requires_authorization
def delete_team():
    team_id = request.form["team_id"]

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()

        if admin_checker(int(team["guild_id"]), discord.fetch_user().id):
            cursor.execute(f"DELETE FROM teams WHERE `id`='{team_id}'")
            mysql.commit()

            guild_id = int(team["guild_id"])
            if team["status"] == "valid":
                guild = client.get_guild(guild_id)
                text = guild.get_channel(int(team["team_text"]))
                voice = guild.get_channel(int(team["team_voice"]))
                role = guild.get_role(int(team["team_role"]))
                category = text.category

                asyncio.run_coroutine_threadsafe(text.edit(name="❌" + text.name), client.loop)
                asyncio.run_coroutine_threadsafe(voice.edit(name="❌" + voice.name), client.loop)
                asyncio.run_coroutine_threadsafe(role.delete(), client.loop)
                asyncio.run_coroutine_threadsafe(category.edit(name="❌" + category.name), client.loop)

            for user_id in json.loads(team["members"]):
                user_id = int(user_id)
                embed = discord_bot.embeds.Embed(
                    title="Team gelöscht!",
                    description=f"Ein Admin hat gerade das Team **{team['name']}** gelöscht!",
                    colour=discord_bot.colour.Colour.red()
                )
                user = asyncio.run_coroutine_threadsafe(client.fetch_user(user_id), client.loop).result()
                asyncio.run_coroutine_threadsafe(user.send(embed=embed), client.loop)

            flash("Das Team wurde gelöscht!")
            return redirect(url_for("guild_admin", guild_id=guild_id))
        else:
            flash("Keine Berechtigung!")
            return redirect(url_for("guild_overview", guild_id=team["guild_id"]))

@app.route("/accept_join/", methods=["POST"])
@requires_authorization
def join_team():
    guild_id = int(request.form["guild_id"])
    team_id = int(request.form["team_id"])
    user_id = int(request.form["user_id"])

    try:
        int(guild_id)
    except ValueError as e:
        print(e)
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        flash("Server wurde nicht gefunden")
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()

        if str(user_id) not in team["requests"]:
            flash("Dieser Benutzer hat den Beitritt nicht angefragt.")
            return redirect(url_for("guild_overview", guild_id=guild_id))

        cursor.execute(f"SELECT requests, members FROM teams WHERE `id`='{team_id}'")
        res = cursor.fetchone()
        requests = json.loads(res["requests"])
        members = json.loads(res["members"])

        request_user = discord.fetch_user()

        if not admin_checker(guild_id, request_user.id) and str(request_user.id) not in team["members"]:
            flash("Du hast keine Berechtigung dazu!")
            return redirect(url_for("guild_overview", guild_id=guild_id))

        requests.remove(user_id)
        members.append(user_id)

        requests = json.dumps(requests)
        members = json.dumps(members)

        cursor.execute(f"UPDATE teams SET `members`='{members}', `requests`='{requests}' WHERE `id`='{team_id}'")

        mysql.commit()

    embed = discord_bot.embeds.Embed(
        title="Beitrittsanfrage angenommen!",
        description=f"Ein Admin oder ein Mitglied hat gerade deine Anfrage zum Team **{team['name']}** angenommen!",
        colour=discord_bot.colour.Colour.green()
    )
    user = asyncio.run_coroutine_threadsafe(client.fetch_user(user_id), client.loop).result()
    asyncio.run_coroutine_threadsafe(user.send(embed=embed), client.loop)

    team_text = asyncio.run_coroutine_threadsafe(client.fetch_channel(int(team["team_text"])), client.loop).result()

    embed = discord_bot.embeds.Embed(
        title="Beitrittsanfrage angenommen!",
        description=f"{request_user.name} hat gerade die Anfrage von **{user.name}** angenommen!",
        colour=discord_bot.colour.Colour.green()
    )
    asyncio.run_coroutine_threadsafe(team_text.send(embed=embed), client.loop)

    guild = asyncio.run_coroutine_threadsafe(client.fetch_guild(guild_id), client.loop).result()
    member = asyncio.run_coroutine_threadsafe(guild.fetch_member(user.id), client.loop).result()
    if member is not None:
        role = guild.get_role(int(team["team_role"]))
        asyncio.run_coroutine_threadsafe(member.add_roles(role), client.loop)

    flash("Benutzer wurde ins Team aufgenommen!")

    if request.form["admin"] == "true":
        return redirect(url_for("guild_admin", guild_id=guild_id))
    else:
        return redirect(url_for("guild_overview", guild_id=guild_id))

@app.route("/reject_join/", methods=["POST"])
@requires_authorization
def reject_join():
    user_id = int(request.form["user_id"])
    team_id = request.form["team_id"]

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()
        guild_id = int(team["guild_id"])
        request_user = discord.fetch_user()
        if not admin_checker(guild_id, request_user.id) and str(request_user.id) not in team["members"]:
            flash("Keine Berechtigung")
            return redirect(url_for("guild_overview", guild_id=guild_id))
        requests = json.loads(team["requests"])
        requests.remove(user_id)

        cursor.execute(f"UPDATE teams SET `requests`='{requests}' WHERE `id`='{team_id}'")

        mysql.commit()

    embed = discord_bot.embeds.Embed(
        title="Beitrittsanfrage abgelehnt!",
        description=f"Ein Admin oder ein Mitglied hat gerade deine Anfrage zum Team **{team['name']}** abgelehnt :(",
        colour=discord_bot.colour.Colour.red()
    )
    user = asyncio.run_coroutine_threadsafe(client.fetch_user(user_id), client.loop).result()
    asyncio.run_coroutine_threadsafe(user.send(embed=embed), client.loop)

    team_text = asyncio.run_coroutine_threadsafe(client.fetch_channel(int(team["team_text"])), client.loop).result()

    embed = discord_bot.embeds.Embed(
        title="Beitrittsanfrage abgelehnt!",
        description=f"{request_user.name} hat gerade die Anfrage von **{user.name}** abgelehnt!",
        colour=discord_bot.colour.Colour.red()
    )
    asyncio.run_coroutine_threadsafe(team_text.send(embed=embed), client.loop)

    flash("Beitrittsanfrage abgelehnt!")
    if request.form["admin"] == "true":
        return redirect(url_for("guild_admin", guild_id=guild_id))
    else:
        return redirect(url_for("guild_overview", guild_id=guild_id))

@app.route("/request_join/", methods=["POST"])
@requires_authorization
def invite_member():
    form = request.form
    guild_id = form["guild_id"]
    team_id = form["team_id"]
    user = discord.fetch_user()

    try:
        int(guild_id)
    except ValueError:
        return redirect(url_for("mainPage"))

    if int(guild_id) not in [guild.id for guild in client.guilds]:
        flash("Dies ist kein Event Server")
        return redirect(url_for("mainPage"))

    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()
        if user.id in json.loads(team["requests"]) or user.id in json.loads(team["members"]):
            flash("Du hast schon einen Beitritt angefragt!")
            return redirect(url_for("guild_overview", guild_id=guild_id))

        requests = team["requests"]
        requests = json.loads(requests)
        requests.append(user.id)
        requests = json.dumps(requests)

        cursor.execute(f"UPDATE teams SET `requests`='{requests}' WHERE `id`='{team_id}'")
        mysql.commit()

    team_text = asyncio.run_coroutine_threadsafe(client.fetch_channel(int(team["team_text"])), client.loop).result()

    embed = discord_bot.embeds.Embed(
        title="Benutzer möchte dem Team beitreten!",
        description=f"**{discord.fetch_user().name}** möchte dem Team beitreten!",
        colour=discord_bot.colour.Colour.purple()
    )
    asyncio.run_coroutine_threadsafe(team_text.send(embed=embed), client.loop)

    return redirect(url_for("guild_overview", guild_id=guild_id))

@app.route("/leave/", methods=["POST"])
@requires_authorization
def leave():
    team_id = request.form["team_id"]
    with mysql.cursor() as cursor:
        cursor.execute(f"SELECT * FROM teams WHERE `id`='{team_id}'")
        team = cursor.fetchone()
        members = json.loads(team["members"])
        members.remove(discord.fetch_user().id)
        members = json.dumps(members)
        cursor.execute(f"UPDATE teams SET `members`='{members}' WHERE `id`='{team_id}'")
        mysql.commit()

    team_text = asyncio.run_coroutine_threadsafe(client.fetch_channel(int(team["team_text"])), client.loop).result()

    embed = discord_bot.embeds.Embed(
        title="Benutzer hat das Team verlassen!",
        description=f"{discord.fetch_user().name} hat gerade das Team verlassen!",
        colour=discord_bot.colour.Colour.red()
    )
    asyncio.run_coroutine_threadsafe(team_text.send(embed=embed), client.loop)

    guild = asyncio.run_coroutine_threadsafe(client.fetch_guild(int(team["guild_id"])), client.loop).result()
    member = asyncio.run_coroutine_threadsafe(guild.fetch_member(discord.fetch_user().id), client.loop).result()
    if member is not None:
        role = guild.get_role(int(team["team_role"]))
        asyncio.run_coroutine_threadsafe(member.remove_roles(role), client.loop)

    flash("Du hast das Team verlassen.")
    return redirect(url_for("guild_overview", guild_id=team["guild_id"]))

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

    flash("Wir haben deine Nachricht erhalten. Wir melden uns demnächst unter der angegebenen E-Mail Adresse.")
    return redirect(url_for("mainPage"))

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


token = secrets.DISCORD_BOT_TOKEN

loop = asyncio.get_event_loop()
loop.create_task(client.start(token))

_thread.start_new_thread(loop.run_forever, tuple(), {})

# _thread.start_new_thread(client.run, token, {})

init.init_db()

if __name__ == "__main__":
    app.run(host=config.bind, port=config.web_port)