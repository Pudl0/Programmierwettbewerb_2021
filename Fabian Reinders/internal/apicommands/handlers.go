package apicommands

import (
	"fmt"
	"log"
	"math/rand"
	"strconv"
	"strings"

	"github.com/bwmarrin/discordgo"
	"github.com/fabiancdng/Arrangoer/internal/models"
)

// Simpler switch-basierter Command-Handler, der Invokes von der API-
// Goroutine entgegennimmt und durch Zugriff auf den Bot-Nutzer
// z. B. Dinge machen kann wie Nachrichten senden oder Rollen erstellen
func HandleAPICommand(ctx *Context) {
	commandSlice := strings.Split(ctx.Command, "///")

	invoke := strings.ToLower(commandSlice[0])
	args := commandSlice[1:]

	switch invoke {
	// Eine Anmeldung ist eingegangen
	case "signup":
		message := fmt.Sprintf("**Eine Anmeldung von <@!%s> ist soeben eingegangen!** 🥳\n\nDu wirst benachrichtigt, sobald deine Anmeldung angenommen oder abgelehnt wurde.", args[0])

		if args[1] != "" {
			message += fmt.Sprintf("\n\nSobald dein Team **%s** vom Spielleiter bestätigt wurde, wirst du benachrichtigt und bekommst die Discord-Rolle automatisch zugewiesen.", args[1])
		}

		embed := &discordgo.MessageEmbed{
			Title:       "📨 Anmeldung eingegangen",
			Description: message,
			Color:       15204542,
		}
		ctx.Session.ChannelMessageSendEmbed(ctx.Config.Discord.NotificationsChannelID, embed)

	// Eine Anmeldung wurde akzeptiert
	case "signup-accepted":
		applicationID, err := strconv.Atoi(args[0])
		if err != nil {
			return
		}

		application := new(models.Application)
		application, err = ctx.Db.GetApplication(applicationID)
		if err != nil {
			return
		}

		// Team des Anmeldenden bekommen
		team := new(models.Team)
		team, err = ctx.Db.GetTeam(application.Team.ID)

		// Prüfen, ob das Team bereits eine Rolle auf dem Server hat
		memberGotRole := false
		roles, _ := ctx.Session.GuildRoles(ctx.Config.Discord.ServerID)
		for _, role := range roles {
			if role.Name == team.Name {
				// Dem neu angenommenen Nutzer die Rolle des Teams geben
				ctx.Session.GuildMemberRoleAdd(ctx.Config.Discord.ServerID, application.UserID, role.ID)
				memberGotRole = true
				break
			}
		}

		teamStatus := "Da dein Team noch nicht akzeptiert wurde, folgt eine Benachrichtigung sowie eine automatische Zuweisung der Rolle noch 😊"
		if memberGotRole {
			teamStatus = fmt.Sprintf("Dein Team wurde bereits akzeptiert. Du hast daher die Rolle %s bekommen und kannst die Team-Channel verwenden 🙃", team.Name)
		}

		message := fmt.Sprintf("**Die Anmeldung von <@!%s> wurde soeben akzeptiert!** 🥳\n\n%s", application.UserID, teamStatus)

		embed := &discordgo.MessageEmbed{
			Title:       "✅ Anmeldung akzeptiert",
			Description: message,
			Color:       62781,
		}

		ctx.Session.ChannelMessageSendEmbed(ctx.Config.Discord.NotificationsChannelID, embed)

	// Ein Team wurde akzeptiert
	case "team-approved":
		teamID, err := strconv.Atoi(args[0])
		if err != nil {
			return
		}

		team := new(models.Team)
		team, err = ctx.Db.GetTeam(teamID)
		if err != nil {
			return
		}

		// Ein paar Farben für die Rollen
		colors := [7]int{16711684, 13107414, 35798, 16754176, 65429, 33023, 16711820}

		// Neue Rolle erstellen
		var role *discordgo.Role
		role, err = ctx.Session.GuildRoleCreate(ctx.Config.Discord.ServerID)

		// Zufällige Farbe für die Rolle auswählen
		color := colors[rand.Intn(len(colors))]

		// Rolle einen Namen und eine Farbe geben
		_, err = ctx.Session.GuildRoleEdit(ctx.Config.Discord.ServerID, role.ID, team.Name, color, true, 0, true)
		if err != nil {
			return
		}

		// Alle Team-Member aus der Datenbank auslesen
		var teamMebers []*models.Application
		teamMebers, err = ctx.Db.GetTeamMembers(teamID)
		if err != nil {
			return
		}

		// Allen Team-Membern die Rolle geben
		members := "**Mitglieder**\n"
		for _, teamMember := range teamMebers {
			members += fmt.Sprintf("\n*%s* - <@!%s>", teamMember.Name, teamMember.UserID)
			err = ctx.Session.GuildMemberRoleAdd(ctx.Config.Discord.ServerID, teamMember.UserID, role.ID)
			if err != nil {
				log.Println(err)
				return
			}
		}

		message := fmt.Sprintf("**Das Team <@&%s> wurde soeben akzeptiert!** 🥳\n\n%s\n\nEuch wurde eine entsprechende Rolle im Discord zugewiesen und ein Sprach- sowie Textchannel für euer Team wurde eingerichtet.\nViel Spaß beim Wettbewerb! 😊", role.ID, members)

		embed := &discordgo.MessageEmbed{
			Title:       "✅ Team akzeptiert",
			Description: message,
			Color:       16761600,
		}

		ctx.Session.ChannelMessageSendEmbed(ctx.Config.Discord.NotificationsChannelID, embed)
	}
}
