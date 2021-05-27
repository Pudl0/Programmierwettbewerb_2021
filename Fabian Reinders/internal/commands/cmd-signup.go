package commands

import (
	"fmt"

	"github.com/bwmarrin/discordgo"
)

type CommandSignup struct{}

func (commandSignup *CommandSignup) Invokes() []string {
	return []string{"anmelden", "a"}
}

func (commandSignup *CommandSignup) AdminPermissionsNeeded() bool {
	return false
}

func (commandSignup *CommandSignup) Execute(ctx *Context) (err error) {
	embed := discordgo.MessageEmbed{
		Title:       "Beim Programmier-Wettbewerb anmelden",
		Description: fmt.Sprintf("Du kannst dich ganz einfach anmelden! Gestatte mir, dich auf meine Web-Oberfläche weiterzuleiten.\n\n[***➤ HIER ANMELDEN***](%s)", ctx.Config.API.FrontendURL),
		Color:       46074,
	}
	_, err = ctx.Session.ChannelMessageSendEmbed(ctx.Message.ChannelID, &embed)
	return
}
