package api

import (
	"log"

	"github.com/fabiancdng/Arrangoer/internal/config"
	"github.com/fabiancdng/Arrangoer/internal/database"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/cors"
	"github.com/gofiber/fiber/v2/middleware/session"
	"github.com/ravener/discord-oauth2"
	"golang.org/x/oauth2"
)

// WebServer für die REST API Routes
type API struct {
	app         *fiber.App
	db          database.Middleware
	channel     chan string
	store       *session.Store
	config      *config.Config
	discordAuth *oauth2.Config
	state       string
}

func NewAPI(config *config.Config, db database.Middleware, channel chan string) (*API, error) {
	// Zufälliger String, der zwischen Login und Callback Seite weitergegeben wird
	var state string = "v6uhSq6eWsnyAp"

	discordAuth := &oauth2.Config{
		RedirectURL:  config.API.FrontendURL,
		ClientID:     config.Discord.ClientID,
		ClientSecret: config.Discord.ClientSecret,
		Scopes:       []string{discord.ScopeIdentify, discord.ScopeGuilds},
		Endpoint:     discord.Endpoint,
	}

	app := fiber.New()
	store := session.New()

	// Cross-origin Anfrangen erlauben
	app.Use(cors.New(cors.Config{
		AllowOrigins:     "*",
		AllowMethods:     "GET,POST,HEAD,PUT,DELETE,PATCH",
		AllowHeaders:     "*",
		AllowCredentials: false,
		ExposeHeaders:    "",
	}))

	api := &API{
		app:         app,
		db:          db,
		channel:     channel,
		store:       store,
		config:      config,
		discordAuth: discordAuth,
		state:       state,
	}

	api.registerHandlers()

	return api, nil
}

func (api *API) registerHandlers() {
	// Hauptgruppe für alle API Endpoints
	// Routes für /api/*
	apiGroup := api.app.Group("/api")

	// Untergruppe für Authentication-Endpoints
	// Routes für /api/auth/*
	apiAuthGroup := apiGroup.Group("/auth")
	apiAuthGroup.Get("/", api.auth)
	apiAuthGroup.Get("/callback", api.authCallback)
	apiAuthGroup.Get("/get/:endpoint", Protected(), api.authGetFromEndpoint)

	// Untergruppe für Anmeldungs-Endpoints
	// Routes für /api/application/*
	apiApplicationGroup := apiGroup.Group("/application")
	apiApplicationGroup.Post("/submit", Protected(), api.applicationSubmit)
	apiApplicationGroup.Get("/list", Protected(), api.applicationList)
	apiApplicationGroup.Put("/accept", Protected(), api.applicationAccept)
	apiApplicationGroup.Delete("/decline", Protected(), api.applicationDecline)

	// Untergruppe für Team-Endpoints
	// Routes für /api/team/*
	apiTeamGroup := apiGroup.Group("/team")
	apiTeamGroup.Get("/list", Protected(), api.teamList)
	apiTeamGroup.Put("/select", Protected(), api.teamSelect)
	apiTeamGroup.Put("/accept", Protected(), api.teamAccept)
	apiTeamGroup.Delete("/decline", Protected(), api.teamDecline)
}

func (api *API) RunAPI() {
	log.Println("API ist bereit!")
	api.app.Listen(api.config.API.AddressAndPort)
}
