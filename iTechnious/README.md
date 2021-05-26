# FlaskHACS (DE)
FlaskHACS ist eine Kombination aus Webapp und Discord Bot,
 die Veranstaltern von Programmierwettbewerben einiges an Arbeit abnimmt.
## Features
* Nutzerfreundliches Webinterface
* /commands für die Konfiguration auf dem Discord Server
* Einfach Anmdeldung und Zusammenstellung der Teams
* Mächtige Admin Features
* Einfache Einrichtung
* => Veranstalter behalten immer den Überblick

## Voraussetzungen
* Ein öffentlicher Server
* Ein MySql Datenbankserver
* Ein Discord Account

## Einrichtung
Zur Einrichtung werden lediglich ein paar Schritte benötigt. 
Zuerst muss dieses Programm auf einen aus dem Internet erreichbaren Server geladen werden.

Nun muss eine App über das [Developer Portal](https://discord.com/developers/applications) erstellt werden.
Dabei darf nicht vergessen werden, dass unter "Bot" der "SERVER MEMBERS INTENT" gesetzt werden muss.

Jetzt werden die Daten aus der App in die "example_config" und "secrets_example" eingetragen werden.
Aus dem Dateinamen ist noch das "example" zu entfernen.

Zuletzt muss die "INTERACTIONS ENDPOINT URL" gesetzt werden. Dabei muss das Programm bereits laufen,
da Discord die Endpunkt Daten sofort überprüft.

#
# FlaskHACS (EN)
FlaskHACS is a combination of webapp and Discord Bot,
 that takes a lot of work off the shoulders of programming contest organizers.
## Features
* User friendly web interface
* /commands for configuration on the Discord server
* Easy registration and team composition
* Powerful admin features
* Easy setup
* => Organizers always keep the overview

## Requirements
* A public server
* A MySql database server
* A Discord account

## Setup
Only a few steps are needed to set this up. 
First, this program needs to be uploaded to a server accessible from the internet.

Now an app must be created via the [Developer Portal](https://discord.com/developers/applications).
It must not be forgotten that the "SERVER MEMBERS INTENT" must be set under "Bot".

Now the data from the app must be entered into the "example_config" and "secrets_example".
The "example" has to be removed from the file name.

Finally the "INTERACTIONS ENDPOINT URL" must be set. The program must already be running,
because Discord checks the endpoint data immediately.
