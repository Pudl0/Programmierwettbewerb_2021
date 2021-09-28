package webserver

import (
	"log"
	"net/http"

	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/config"
	"github.com/fabiancdng/Arrangoer/internal/database"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/filesystem"
	"github.com/gofiber/fiber/v2/middleware/session"
	"github.com/ravener/discord-oauth2"
	"golang.org/x/oauth2"
)

// WebServer für die REST API Routes
type WebServer struct {
	app              *fiber.App
	db               database.Middleware
	store            *session.Store
	config           *config.Config
	discordClient    *discordgo.Session
	discordAuth      *oauth2.Config
	discordAuthState string
}

func NewWebServer(config *config.Config, db database.Middleware, discordClient *discordgo.Session) (*WebServer, error) {
	// Zufälliger String, der zwischen Login und Callback Seite weitergegeben wird
	var state string = "v6uhSq6eWsnyAp"

	discordAuth := &oauth2.Config{
		RedirectURL:  config.WebServer.URL,
		ClientID:     config.Discord.ClientID,
		ClientSecret: config.Discord.ClientSecret,
		Scopes:       []string{discord.ScopeIdentify, discord.ScopeGuilds},
		Endpoint:     discord.Endpoint,
	}

	app := fiber.New(fiber.Config{
		DisableStartupMessage: true,
	})
	store := session.New()

	ws := &WebServer{
		app:              app,
		db:               db,
		store:            store,
		config:           config,
		discordClient:    discordClient,
		discordAuth:      discordAuth,
		discordAuthState: state,
	}

	ws.registerHandlers()

	return ws, nil
}

func (ws *WebServer) registerHandlers() {
	// Alle Routes für die API registrieren
	// Routes für /api/*
	apiRouter := ws.app.Group("/api")

	// Routes für /api/auth/*
	apiAuthRouter := apiRouter.Group("/auth")
	apiAuthRouter.Get("/", ws.auth)
	apiAuthRouter.Get("/callback", ws.authCallback)
	apiAuthRouter.Get("/get/:endpoint", Protected(), ws.authGetFromEndpoint)

	// Routes für /api/application/*
	apiApplicationRouter := apiRouter.Group("/application")
	apiApplicationRouter.Post("/submit", Protected(), ws.applicationSubmit)
	apiApplicationRouter.Get("/list", Protected(), ws.applicationList)
	apiApplicationRouter.Put("/accept", Protected(), ws.applicationAccept)
	apiApplicationRouter.Delete("/decline", Protected(), ws.applicationDecline)

	// Routes für /api/team/*
	apiTeamRouter := apiRouter.Group("/team")
	apiTeamRouter.Get("/list", Protected(), ws.teamList)
	apiTeamRouter.Put("/select", Protected(), ws.teamSelect)
	apiTeamRouter.Put("/accept", Protected(), ws.teamAccept)
	apiTeamRouter.Delete("/decline", Protected(), ws.teamDecline)

	// React Frontend bereitstellen und alle Anfragen, die nicht an die API gehen,
	// darauf leiten
	ws.app.Use(filesystem.New(filesystem.Config{
		Root:         http.Dir("web/build"),
		Browse:       true,
		Index:        "index.html",
		MaxAge:       3600,
		NotFoundFile: "index.html",
	}))
}

func (ws *WebServer) RunWebServer() {
	log.Println("Webserver ist bereit!")
	ws.app.Listen(ws.config.WebServer.AddressAndPort)
}
