# Hackathon_KillerentenBOT

### THis is the Project of the Team Killerenten for the Hackathon 2021.
---

## Installation guide

-1. Clone or Download the Repository

-1.2 "npm install package.json" in cmd

-1.3 create a .env file and put 
```KillerentenBOT_TOKEN = <TOKEN>```
```Gmail_pass = <gmail smtp password>```
```Gmail_adress = <gmail smtp adress>```
in it. You need a Gmail account with low security to make the email system work.

-1.4 install ffmpeg

-2 !!!FFMPEG needs to be installed for the BOT to work.(For !play)!!!

-3 change OrganizerEmail and ExceptRoles and if wanted Token.

-4 open start.bat if on windows

-5 Use the config Command to change all of the ChannelID`s.


---

## HELP

> Hier bist du richtig, wenn du Hilfe brauchst.**!application oder!a** startet einen Abfrage zur Anmeldung woraufhin eine E-mail zum Organisator gesendet wird.**!ban** bannt einen Nutzer, allerdings nur wenn du die Berechtigung hast.\n\n**!cat** sendet ein Katzenbild.**!create** team erstellst du ein neues Team mit Sprach- und Textchanneln, den du dann joinen kannst, dafür einfach den Teamnamen hinter !create team festlegen, auch lassen sich nachträglich Rollen mit **!create role erstellen** (nur für Admins!).Mit **!join team** kannst du einem bereits bestehenden Team joinen, dafür einfach den Teamnamen hinter !join team eingeben.Mit **!kick** kannst du einen Member kicken, wenn du die Berechtigung hast.Mit **!leave** team kannst du dein Team wieder verlassen, hierfür einfach den Teamnamen hinter !leave team eingeben**!minigame** startet ein Zahlenratespiel.Mit **!play** kannst du nach einem Youtube Titel suchen, indem du den Titel hinter !play eingibst, dieser wird dann im Bot im Channel abgespielt.**!meme** sendet ein beliebiges Meme in den channel. Mit **!code** kannst du in stackoverflow nach unterschiedlichen code Fragen suchen, hierfür einfach deine Sucheingabe hinter !code eingeben. Mit **!announcement** kannst du ein announcement Embed in ein vorher festgelegten channel senden, hierfür die Werte in folgender Reihenfolge tippen (COLOR TITLE(zwingen benötigt) DESCRIPTION URL THUMBNAIL IMAGE), jeder nicht genannte Wert ist mit ... zu ersetzen (nur für Admins!).

> !config/!c', '!config setzt mit annid welcomeid ping die Channel id bzw Rolle für die welcome message bzw announcements bzw für gepingte user fest, mit application und opencreate lassen sich dann mit true und false die Anmeldung und die Teamerstellung freischalten. Außerdem lässt sich mit **!config logid** die channeld-id für den bot log ändern.

#### PING

-  ```ping```: You can test the BOT by sending !ping in a channel.

#### BOT Permissions

> The Bot needs to have the Admin permission.

> If you want to use the config command the user needs Admin rights too and same goes for the kick/ban commands.

