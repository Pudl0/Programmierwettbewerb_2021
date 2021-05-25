## Programmierwettbewerb 2021 Fortgeschrittenen-Aufgabe, Discord Bot

### Konzept

Der Bot besteht aus zwei Teilen: dem eigentlichen Bot und Server (``/backend``), umgesetzt in Java mithilfe der
Bibliothek
[Discord4J](https://github.com/Discord4J/Discord4J) und einer Web-basierten Benutzeroberfläche (``/frontend``),
umgesetzt in JavaScript mit [React](https://reactjs.org/).

### Build

Voraussetzungen für den Build sind [Node](https://nodejs.org/en/) (``npm``)
und [Maven](https://maven.apache.org/) (`mvn`). Im Hauptverzeichnis befindet sich ein Buildscript für
Windows, ``build.cmd``. Bei Ausführung baut es das Backend und Frontend und speichert eine fertige Serverdatei inklusive
Webinterface in ``/build``. Ein fertiger Build ist [hier](http://pauhull.de/spw21/latest-build.zip) zu finden.

### Erste Ausführung und Installation

#### Konfiguration

Nach einem erfolgreichem Build lässt sich der Server mit dem Befehl ``java -jar server.jar`` Starten. Es sind keine
Startargumente nötig. Er sollte folgende Ausgabe liefern:

```
Invalid token, make sure you're using the token from the developer portal Bot section and not the application client secret or public key
```

Der Server hat eine Konfigurationsdatei ``config.json`` generiert. Sie sollte wie folgt aussehen:

```json
{
  "discord": {
    "token": "Discord Token hier einfügen"
  },
  "webConsole": {
    "port": 8000
  },
  "channels": {
    ...
  },
  "messages": {
    ...
  },
  "commands": {
    ...
  },
  "twitch": {
    "channel": "hackathonleer",
    "clientId": "Client ID",
    "clientSecret": "Client secret"
  },
  "github": {
    "user": "Pudl0",
    "repo": "Programmierwettbewerb_2021"
  },
  "paste": {
    "url": "http://paste.pauhull.de/"
  }
}
```

Hier lässt sich der Bot genauer anpassen. Die wichtigste Einstellung ist ``discord.token``: Hier muss der Bot-Token
eingetragen werden, der auf dem [Discord Developer Portal](https://discord.com/developers) erstellt wurde.

``webConsole.port`` beinhaltet den Port (TCP), unter dem das Webinterface erreichbar sein wird. Für einige Funktionen
ist es wichtig, dass dieser Port öffentlich ist.

Unter ``channels``, ``messages`` und ``commands`` lassen sich Bot-Nachrichten o. Ä. einstellen. Diese Einstellungen
lassen sich auch im Webinterface treffen.

Unter ``twitch`` lässt sich der Stream-Alert einstellen. Geht der Kanal ``twitch.user`` live, wird dies im
Ankündigungskanal gepostet. Dafür ist eine gültige Client-ID und ein Client-Secret nötig. Diese erhält man
auf [Twitch Developers](https://dev.twitch.tv/console/).

``github`` beinhaltet das Repository, für das Pull Requests automatisch im Ankündigungskanal gepostet werden.

``paste.url`` beinhaltet die URL für den genutzten Paste-Service. Dieser muss
auf [haste-server](https://github.com/seejohnrun/haste-server)
basiert sein. Die URL muss auf ``/`` enden. Beispielsweise wäre auch ``https://hastebin.com/`` möglich (auch wenn dieser
Service oft nicht erreichbar ist.)

#### Webinterface

Ist der Bot richtig konfiguriert, sollte ein erster Start in der Konsole in etwa so aussehen:

```
[ INFO] (main) Discord4J 3.1.5 (https://discord4j.com)
Mai 25, 2021 2:26:29 PM com.fasterxml.jackson.databind.ext.Java7Handlers <clinit>
WARNUNG: Unable to load JDK7 types (java.nio.file.Path): no Java7 type support added
[ INFO] (reactor-http-nio-2) [G:c536448, S:0] Connected to Gateway
[ INFO] (reactor-http-nio-2) [G:c536448, S:0] Shard connected
[Terry Davis] Successfully connected to bot
Webserver started on port 8000

====================================

Passwort für Konsole: DTp8Y5iJOfTXhn6h
Bitte sofort nach Einloggen ändern!

====================================
```

Nun ist das Webinterface unter dem eingestellten Port erreichbar. Ist der Bot beispielsweise auf der lokalen Maschine
auf dem Port 8000 gehostet, lautet die URL ``http://localhost:8000/``. Dort wird man aufgefordert, das Passwort aus der
Konsole einzugeben (in unserem Fall ``DTp8Y5iJOfTXhn6h``. Geheimtipp: Löscht man die Datei ``.pwd``, wird beim nächsten
Start ein neues generiert.)
Nun ist man eingeloggt und kann in den Einstellungen ein neues **sicheres**
Passwort festlegen. Dort lässt sich auch ein dunkles Design aktivieren, da wie jeder weiß, jeder ernstzunehmende
Programmierer den Dark Mode benutzt.

### Features

Hier einige Features des Bots im Überblick:

- Umfangreiches Webinterface (Mobile-friendly)
- Zahlreiche Einstellungsmöglichkeiten
- Web-API
- Sammlung von Anmeldungen per Befehl und Export für Tabellenprogramme im Webinterface
- Team-System zum Erstellen von öffentlichen und privaten Teams inklusive Beitrittsanfragen
- Begrüßen von neuen Nutzern
- Github-Integration (Ankündigen von neuen Pull Requests)
- Twitch-Integration (Ankündigen von Livestreams)
- Stack Overflow-Integration (Suchen nach Fragen per Befehl)
- haste-server-Integration (Pastes erstellen per Befehl)
- Tic Tac Toe Minispiel
- Zufälligen Witz ausgeben (konfigurierbar)
- Dark Mode

### Screenshots

![](https://i.imgur.com/qjqI2AD.png)
![](https://i.imgur.com/yWt7qgd.png)
![](https://i.imgur.com/HNsmVUk.png) 
![](https://i.imgur.com/yal5ow7.png)
![](https://i.imgur.com/XKPB8j6.png)
![](https://i.imgur.com/35asZWP.png) 
![](https://i.imgur.com/gZyWBS7.png)
![](https://i.imgur.com/jJWRqWY.png)
![](https://i.imgur.com/qmyAOyb.png) 
![](https://i.imgur.com/Zmk9tz1.png)
![](https://i.imgur.com/gTzPtEx.png)
![](https://i.imgur.com/u3aczKT.png)