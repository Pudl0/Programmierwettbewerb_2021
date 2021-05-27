package commands

import "github.com/bwmarrin/discordgo"

type MiddlewarePermissions struct{}

func (middlewarePermissions *MiddlewarePermissions) Execute(ctx *Context, command Command) (next bool, err error) {
	if !command.AdminPermissionsNeeded() {
		next = true
		return
	}

	guild, err := ctx.Session.Guild(ctx.Message.GuildID)
	if err != nil {
		return
	}

	// Prüfen, ob der Nachricht-Autor Owner ist
	if guild.OwnerID == ctx.Message.Author.ID {
		next = true
		return
	}

	roles := make(map[string]*discordgo.Role)

	// Map für jede Rollen ID auf ein Rollen Objekt
	for _, role := range guild.Roles {
		roles[role.ID] = role
	}

	for _, roleID := range ctx.Message.Member.Roles {
		// Überprüfen, ob die Rolle Admin-Rechte hat
		if role, valid := roles[roleID]; valid && role.Permissions&discordgo.PermissionAdministrator > 0 {
			next = true
			break
		}
	}

	return
}
