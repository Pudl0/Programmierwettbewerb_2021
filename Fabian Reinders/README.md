<div align="center">
    <img src="assets/Arrangør-transparent.png" width="230px" />
    <h1>Arrangør</h1>
    <h3>
        Ein Discord-Bot mit Web-Dashboard, der bei der Organisation des Hackathons helfen kann.
    </h3><br>
</div>

---

## Funktionen des Bots
### Web-Oberfläche
Die Steuerung des Bots erfolgt hauptäschlich über die Weboberfläche. Es gibt ein Dashboard für Admins, wo Anmeldungen und Teams angenommen, abegelehnt oder verändert werden können und eines, wo sich die Nutzer für den Wettbewerb anmelden oder ihr Team wechseln können.

#### Login mit Discord
<img width="100%" src="assets/screenshots/discord-login.png" />

#### Dashboard für Nutzer
Wenn man sich anmeldet, erkennt der Bot automatisch, ob man auf dem Hackathon-Server Admin oder normaler Benutzer ist und leitet einen entsprechend entweder an eine Nutzer- oder Adminübersicht weiter.

Auf dem Nutzerdashboard ist es möglich, sich für den Wettbewerb anzumelden oder sein Team zu wechseln.

<img width="100%" src="assets/screenshots/user-dashboard.png" />

#### Dashboard für Admins

Wenn eine Anmeldung abgeschickt ist, muss ein Admin sie erst annehmen. Wenn das Team, was in der Anmeldung angegeben wurde noch nicht existiert, wird es automatisch erstellt, aber muss zusätzlich auch nochmal von einem Admin bestätigt werden

<img width="100%" src="assets/screenshots/admin-dashboard.png" />

#### Discord Benachrichtigungen
Um die von "Quack" eingeführte Tradition der "Rezeption" fortzuführen, hat mein Bot die Möglichkeit, einen Channel für Benachrichtigungen über Anmeldungen, Annahmen oder Teams einzurichten.
##### Anmeldung eingegangen
<img width="600px" src="assets/screenshots/notification-new-application.png" />

##### Anmeldung angenommen
<img width="600px" src="assets/screenshots/notification-application-accepted.png" />

##### Team angenommen
<img width="600px" src="assets/screenshots/notification-team-accepted.png" />

#### Begrüßung neuer Nutzer
Der Bot schickt automatisch eine Begrüßung in den Lobby-Channel, wenn ein neuer Nutzer dem Server beitritt.

<img width="600px" src="assets/screenshots/welcome-message.png" />

## Einrichtung / Konfiguration des Bots
### Config-Datei
Der Bot verfügt über eine 'Config-Datei', in die alle Daten eingetragen werden können, die er braucht, um zu funktionieren. Eine ```example.config.yml``` befindet sich im Ordner 'config'. Diese kann einfach kopiert und/oder umbenannt werden zu ```config.yml```. Nachdem dies erledigt ist, muss die Config ausgefüllt werden (siehe Kommentare in der Datei).

### Kompilieren des Bots
Der Bot, inklusive seiner API, sind in Go geschrieben und müssen dementsprechend mit dem [Go Compiler](https://golang.org/) kompiliert werden. Den Go Compiler kann man hier herunterladen: https://golang.org/

Mit folgendem Befehl kann man den Bot kompilieren:

```go build cmd/arrangoer/main.go``` 

### Builden der Web-Oberfläche

Die Weboberfläche wurde mit React.js entwickelt und muss entsprechend auch kompiliert bzw. gebuildet werden. 

1. In den Ordner für die Web-Oberfläche gehen

```cd web/```

2. Alle Abhängigkeiten installieren. Dazu müssen [Node.js und npm](https://nodejs.org/en/) installiert sein

```npm install```

3. Die React App "kompilieren" bzw. builden

```npm run build```

Man bekommt einen ```build/``` Ordner, der aus plain HTML und JavaScript Dateien besteht, die dann automatisch vom integrierten WebServer bereitgestellt werden.

### Woher bekomme ich Client Secret und Client ID?
Den Token, das Client Secret und die Client ID gibt es auf der [Discord Developers Seite](https://discord.com/developers) im OAuth-Menü. Zudem muss dort eine 'redirect uri' eingetragen werden, also eine URL, an die der Nutzer nach dem Login mit Discord weitergeleitet wird. Dort muss die URL der Web-Oberfläche eingetragen werden (wie im Bild zu sehen).

<img width="100%" src="assets/screenshots/discord-developers-oauth.png" />

<br>

**Einsendung für den Programmier-Wettbewerb der "Digitalen Woche 2021 Leer" von Fabian Reinders.**
