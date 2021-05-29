/*
                                Arrangør

   Discord Bot, der bei der Organisation des Programmier-Wettbewerbs hilft.

   Einsendung für den 'Programmier-Wettbewerb' der 'Digitalen Woche 2021 Leer'

                   Copyright (c) 2021 Fabian Reinders

*/

package main

import (
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/api"
	"github.com/fabiancdng/Arrangoer/internal/apicommands"
	"github.com/fabiancdng/Arrangoer/internal/commands"
	"github.com/fabiancdng/Arrangoer/internal/config"
	"github.com/fabiancdng/Arrangoer/internal/database/sqlite"
	"github.com/fabiancdng/Arrangoer/internal/events"
)

func main() {
	//////////////////////
	//      CONFIG      //
	//////////////////////

	config, err := config.ParseConfig("./config/config.yml")
	if err != nil {
		panic(err)
	}

	///////////////////////////
	//       DATABASE        //
	///////////////////////////

	db := new(sqlite.SQLite)
	if err = db.Open(); err != nil {
		panic(err)
	}

	///////////////////////////
	//    DISCORD SESSION 	 //
	///////////////////////////

	session, err := discordgo.New("Bot " + config.Discord.Token)
	if err != nil {
		panic(err)
	}

	session.Identify.Intents = discordgo.IntentsAll

	registerEvents(session)
	registerCommands(session, config)

	err = session.Open()
	if err != nil {
		log.Panic(err)
	}

	////////////////////////////
	//    API / WEB SERVER    //
	////////////////////////////

	// API (& ihren Webserver) in Goroutine starten
	// und einen Channel zur Kommunikation zwischen Bot und API (/Website) aufbauen
	apiChannel := make(chan string)
	api, err := api.NewAPI(config, db, apiChannel)
	if err != nil {
		log.Panic(err)
	}

	// Auf Befehle aus dem apiChannel warten und diese ggf. ausführen
	go (func() {
		for {
			// Invoke aus dem Channel lesen
			apiCommand := <-apiChannel

			// Command-Context erstellen
			ctx := &apicommands.Context{
				Session: session,
				Config:  config,
				Db:      db,
				Command: apiCommand,
			}

			// An API-Command-Handler weitergeben
			go apicommands.HandleAPICommand(ctx)
		}
	})()

	go api.RunAPI()

	log.Println("Der Bot läuft jetzt! // Er kann mit STRG+C beendet werden.")

	// Channel zwischen Main und Discord Session Routine, der Platz für max. eine Fehlermeldung hat
	sessionChannel := make(chan os.Signal, 1)

	// Auf bestimmte Syscalls hören und diese ggf. in den Channel schicken
	// um ihn zu schließen und das Programm zu beenden
	signal.Notify(sessionChannel, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)

	// Blocken durch Lesen des Channels, bis ein Fehler auftritt
	<-sessionChannel

	log.Println("Der Bot wurde gestoppt!\n Ausloggen...")

	// Session sauber schließen
	session.Close()
}

// Registrierung aller Event Handler, auf die der Bot hören soll
func registerEvents(session *discordgo.Session) {
	session.AddHandler(events.NewReadyHandler().Handler)
	session.AddHandler(events.NewJoinHanlder().Handler)
}

// Registrierung aller Commands, auf die der Bot hören soll und deren Handler
func registerCommands(session *discordgo.Session, config *config.Config) {
	commandHandler := commands.NewCommandHandler(config)

	commandHandler.RegisterCommand(&commands.CommandTest{})
	commandHandler.RegisterCommand(&commands.CommandSignup{})
	commandHandler.RegisterCommand(&commands.CommandTeam{})
	commandHandler.RegisterCommand(&commands.CommandClear{})
	// Registrierung der Command-Permissions-Middleware, die die Permissions überprüft
	commandHandler.RegisterMiddleware(&commands.MiddlewarePermissions{})

	session.AddHandler(commandHandler.HandleMessage)
}
