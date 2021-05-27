package api

// Discord User in OAuth2 API
type DiscordUser struct {
	ID            string `json:"id"`
	Username      string `json:"username"`
	Avatar        string `json:"avatar"`
	Discriminator string `json:"discriminator"`
}

// Discord Guild in OAuth2 API
type DiscordGuild struct {
	ID          string `json:"id"`
	Name        string `json:"name"`
	Icon        string `json:"icon"`
	Permissions string `json:"permissions_new"`
}

// Eine Anfrage auf die Seite, an die Discord einen nach der Autorisierung weiterleitet
type CallbackRequest struct {
	Sate string `query:"state"`
	Code string `query:"code"`
}

// Eine Anfrage, einen Nutzer oder ein Team zu akzeptieren oder abzulehnen
type SentenceRequest struct {
	// Entweder die Team-ID oder die Anmeldungs-ID
	Id int `json:"id"`
	// Name, damit dieser ggf. geändert werden kann
	Name string `json:"name"`
}

// Ein Request, um sein Team auszuwählen oder zu wechseln
type TeamSelectRequest struct {
	// Discord UserID
	UserID int `json:"user_id"`
	// ID von dem Team, in das der Nutzer möchte
	TeamID int `json:"team_id"`
}
