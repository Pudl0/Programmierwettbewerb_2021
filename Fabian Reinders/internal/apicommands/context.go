package apicommands

import (
	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/config"
	"github.com/fabiancdng/Arrangoer/internal/database"
)

type Context struct {
	Session *discordgo.Session
	Config  *config.Config
	Db      database.Middleware
	Command string
}
