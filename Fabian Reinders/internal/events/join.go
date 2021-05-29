package events

import (
	"log"
	"strings"

	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/config"
)

type JoinHandler struct{}

func NewJoinHanlder() *JoinHandler {
	return &JoinHandler{}
}

func (joinHandler *JoinHandler) Handler(session *discordgo.Session, event *discordgo.GuildMemberAdd) {
	log.Printf("%s ist dem Server beigetreten.", event.User.Username)

	config, err := config.ParseConfig("./config/config.yml")
	if err != nil {
		log.Panic(err)
	}

	channel, err := session.Channel(config.Discord.LobbyChannelID)
	if err != nil {
		log.Println("Der Lobby-Channel aus der config.yml kann nicht gefunden werden! Prüfe, ob der Bot ausreichende Berechtigungen hat und die Channel ID korrekt ist.")
		return
	}

	welcomeMessageParsed := strings.ReplaceAll(config.Discord.WelcomeMessage, "//USER//", event.User.Mention())
	welcomeMessageParsed = strings.ReplaceAll(welcomeMessageParsed, "///", "\n")

	embed := &discordgo.MessageEmbed{
		Title:       "Willkommen!",
		Description: welcomeMessageParsed,
		Color:       16757504,
	}

	_, err = session.ChannelMessageSendEmbed(channel.ID, embed)
	if err != nil {
		log.Println("Die Willkommens-Nachricht konnte nicht gesendet werden! Prüfe, ob der Bot ausreichende Berechtigungen hat und die Channel ID korrekt ist.")
		return
	}
}
