package commands

import (
	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/config"
)

type Context struct {
	Session *discordgo.Session
	Message *discordgo.Message
	Config  *config.Config
	Args    []string
	Handler *CommandHandler
}
