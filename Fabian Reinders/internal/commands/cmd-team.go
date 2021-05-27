package commands

import (
	"fmt"

	"github.com/bwmarrin/discordgo"
)

type CommandTeam struct{}

func (commandTeam *CommandTeam) Invokes() []string {
	return []string{"team", "t"}
}

func (commandTeam *CommandTeam) AdminPermissionsNeeded() bool {
	return false
}

func (commandTeam *CommandTeam) Execute(ctx *Context) (err error) {
	embed := discordgo.MessageEmbed{
		Title:       "Zuweisung deines Teams",
		Description: fmt.Sprintf("Bist du schon angemeldet und hast ein Team gefunden? Oder möchtest du dein Team ändern? Dann klicke hier!\n\n[***➤ HIER DEIN TEAM ZUWEISEN***](%s)", ctx.Config.API.FrontendURL),
		Color:       14680128,
	}
	_, err = ctx.Session.ChannelMessageSendEmbed(ctx.Message.ChannelID, &embed)
	return
}
