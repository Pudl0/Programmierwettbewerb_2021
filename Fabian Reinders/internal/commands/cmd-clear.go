package commands

import (
	"strconv"

	"github.com/bwmarrin/discordgo"
)

type CommandClear struct{}

func (commandClear *CommandClear) Invokes() []string {
	return []string{"clear", "cls"}
}

func (commandClear *CommandClear) AdminPermissionsNeeded() bool {
	return true
}

func (CommandClear *CommandClear) Execute(ctx *Context) (err error) {
	if len(ctx.Args) < 1 {
		embed := discordgo.MessageEmbed{
			Description: "❌ Syntax: clear <Anzahl an Nachrichten>",
			Color:       15728644,
		}
		_, err = ctx.Session.ChannelMessageSendEmbed(ctx.Message.ChannelID, &embed)
		return
	}

	limit, _ := strconv.Atoi(ctx.Args[0])
	if limit > 100 {
		embed := discordgo.MessageEmbed{
			Description: "❌ Es dürfen maximal 100 Nachrichten auf einmal gelöscht werden.",
			Color:       15728644,
		}
		_, err = ctx.Session.ChannelMessageSendEmbed(ctx.Message.ChannelID, &embed)
		return
	}

	var messageIDs []string
	messages, err := ctx.Session.ChannelMessages(ctx.Message.ChannelID, limit, ctx.Message.ID, "", "")

	for _, message := range messages {
		messageIDs = append(messageIDs, message.ID)
	}

	err = ctx.Session.ChannelMessagesBulkDelete(ctx.Message.ChannelID, messageIDs)
	err = ctx.Session.MessageReactionAdd(ctx.Message.ChannelID, ctx.Message.ID, "✅")
	return
}
