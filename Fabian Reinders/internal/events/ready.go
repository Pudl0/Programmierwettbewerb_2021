package events

import (
	"log"

	"github.com/bwmarrin/discordgo"
)

type ReadyHandler struct{}

func NewReadyHandler() *ReadyHandler {
	return &ReadyHandler{}
}

func (readyHandler *ReadyHandler) Handler(session *discordgo.Session, event *discordgo.Ready) {
	log.Println("Der Bot ist nun bei Discord eingeloggt und bereit!")
	log.Printf("Eingeloggt als %s.", event.User.String())
}
