package api

import (
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"strconv"

	"github.com/fabiancdng/Arrangoer/internal/models"
	jwt "github.com/form3tech-oss/jwt-go"
	"github.com/gofiber/fiber/v2"
	"golang.org/x/oauth2"
)

// +++++++ AUTHENTICATION HANDLERS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

// Leite an die Oauth2 Authorization Seite weiter
func (api *API) auth(ctx *fiber.Ctx) error {
	return ctx.Redirect(api.discordAuth.AuthCodeURL(api.state), 307)
}

// Route, die den State und Code aus einem Discord Callback request als GET Parameter akzeptiert
// und sie gegen einen JWT austauscht.
func (api *API) authCallback(ctx *fiber.Ctx) error {
	callbackRequest := new(CallbackRequest)

	err := ctx.QueryParser(callbackRequest)
	if err != nil {
		return fiber.NewError(400, "invalid request body")
	}

	if callbackRequest.Sate != api.state {
		return fiber.NewError(400, "state doesn't match")
	}

	// Den Code für einen Access-Token eintauschen
	token, err := api.discordAuth.Exchange(context.Background(), callbackRequest.Code)

	if err != nil {
		return fiber.NewError(500, "an error occured at code/token exchange")
	}

	// Neuer JSON Web Token
	jwtoken := jwt.New(jwt.SigningMethodHS256)

	// Discord access und refresh token in JWT speichern
	claims := jwtoken.Claims.(jwt.MapClaims)
	claims["dc_access_token"] = token.AccessToken
	claims["dc_refresh_token"] = token.RefreshToken

	signedjwt, err := jwtoken.SignedString([]byte("gbt3FPVq5#MwU8SM^hpvUJwxEw"))
	if err != nil {
		return fiber.NewError(500)
	}

	return ctx.JSON(fiber.Map{
		"jwt": signedjwt,
	})
}

// Daten von der Discord OAuth2 API abrufen (wie Nutzerinfos oder Guildinfos)
func (api *API) authGetFromEndpoint(ctx *fiber.Ctx) error {
	jwtoken := ctx.Locals("jwtoken").(*jwt.Token)
	claims := jwtoken.Claims.(jwt.MapClaims)
	accessToken := claims["dc_access_token"].(string)
	refreshToken := claims["dc_refresh_token"].(string)

	if accessToken == "" || refreshToken == "" {
		return fiber.NewError(401)
	}

	token := &oauth2.Token{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	var endpoint string

	switch ctx.Params("endpoint") {
	case "guild":
		endpoint = "https://discordapp.com/api/users/@me/guilds"
	default:
		endpoint = "https://discordapp.com/api/users/@me"
	}

	// Den Access-Token benutzen, um Daten des Benutzers abzurufen
	res, err := api.discordAuth.Client(context.Background(), token).Get(endpoint)

	if err != nil || res.StatusCode != 200 {
		return fiber.NewError(500, "couldn't use the access token")
	}

	defer res.Body.Close()

	body, err := ioutil.ReadAll(res.Body)

	if err != nil {
		return fiber.NewError(500, "an error occured while attempting to parse request body")
	}

	switch ctx.Params("endpoint") {
	case "guild":
		var discordGuilds []*DiscordGuild
		json.Unmarshal(body, &discordGuilds)

		var isUserMemberOfGuild bool = false
		var isUserAdminOfGuild bool = false

		for _, guild := range discordGuilds {
			// Prüfen, ob der Nutzer bereits auf dem Server ist
			if guild.ID == api.config.Discord.ServerID {
				isUserMemberOfGuild = true
				// Prüfen, ob der Nutzer Admin-Rechte auf dem Server hat
				permissions, _ := strconv.Atoi(guild.Permissions)
				if permissions&8 > 0 {
					isUserAdminOfGuild = true
				}

				break
			}
		}

		return ctx.JSON(fiber.Map{
			"user_is_member": isUserMemberOfGuild,
			"user_is_admin":  isUserAdminOfGuild,
			"invite_link":    api.config.Discord.InviteLink,
		})

	default:
		var discordUser DiscordUser
		json.Unmarshal(body, &discordUser)

		return ctx.JSON(discordUser)
	}

}

// +++++++ APPLICATION HANDLERS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

// Nimmt Daten des Anmeldeformulars entgegen
func (api *API) applicationSubmit(ctx *fiber.Ctx) error {
	jwtoken := ctx.Locals("jwtoken").(*jwt.Token)
	claims := jwtoken.Claims.(jwt.MapClaims)
	accessToken := claims["dc_access_token"].(string)
	refreshToken := claims["dc_refresh_token"].(string)

	if accessToken == "" || refreshToken == "" {
		return fiber.NewError(401)
	}

	application := new(models.ApplicationRequest)

	err := ctx.BodyParser(application)
	if err != nil {
		return fiber.NewError(400)
	}

	token := &oauth2.Token{
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	}

	// Den Access-Token benutzen, um Daten des Benutzers abzurufen
	res, err := api.discordAuth.Client(context.Background(), token).Get("https://discordapp.com/api/users/@me")

	defer res.Body.Close()

	body, err := ioutil.ReadAll(res.Body)

	discordUser := new(DiscordUser)
	json.Unmarshal(body, &discordUser)

	application.UserID = discordUser.ID

	err = api.db.SaveApplication(application)
	if err != nil {
		return err
	}

	// Event auslösen, das u. A. eine Benachrichtigung vom Bot triggert
	api.channel <- fmt.Sprintf("signup///%s///%s", application.UserID, application.Team)

	return ctx.SendStatus(200)
}

// Gibt alle Anmeldungen zurück
func (api *API) applicationList(ctx *fiber.Ctx) error {
	applications, err := api.db.GetApplications()
	if err != nil {
		return err
	}

	return ctx.JSON(applications)
}

// Gibt alle Teams zurück
func (api *API) teamList(ctx *fiber.Ctx) error {
	teams, err := api.db.GetTeams()
	if err != nil {
		return err
	}

	return ctx.JSON(teams)
}

// Akzeptiert und editiert ggf. Anmeldungen (oder lehnt sie ab)
func (api *API) applicationAccept(ctx *fiber.Ctx) error {
	sentenceRequest := new(SentenceRequest)

	err := ctx.BodyParser(sentenceRequest)
	if err != nil {
		return fiber.NewError(400)
	}

	if err = api.db.AcceptApplication(sentenceRequest.Id, sentenceRequest.Name); err != nil {
		return err
	}

	// Event auslösen, das u. A. eine Benachrichtigung vom Bot triggert
	api.channel <- fmt.Sprintf("signup-accepted///%s", strconv.Itoa(sentenceRequest.Id))

	return ctx.SendStatus(200)
}

// Akzeptiert und editiert ggf. Teams (oder lehnt sie ab)
func (api *API) teamAccept(ctx *fiber.Ctx) error {
	sentenceRequest := new(SentenceRequest)

	err := ctx.BodyParser(sentenceRequest)
	if err != nil {
		return fiber.NewError(400)
	}

	if err = api.db.ApproveTeam(sentenceRequest.Id, sentenceRequest.Name); err != nil {
		return err
	}

	// Event auslösen, das u. A. eine Benachrichtigung vom Bot triggert
	api.channel <- fmt.Sprintf("team-approved///%s", strconv.Itoa(sentenceRequest.Id))

	return ctx.SendStatus(200)
}

// Akzeptiert und editiert ggf. Anmeldungen (oder lehnt sie ab)
func (api *API) applicationDecline(ctx *fiber.Ctx) error {
	sentenceRequest := new(SentenceRequest)

	err := ctx.BodyParser(sentenceRequest)
	if err != nil {
		return fiber.NewError(400)
	}

	if err = api.db.DeclineApplication(sentenceRequest.Id); err != nil {
		return err
	}

	return ctx.SendStatus(200)
}

// Akzeptiert und editiert ggf. Teams (oder lehnt sie ab)
func (api *API) teamDecline(ctx *fiber.Ctx) error {
	sentenceRequest := new(SentenceRequest)

	err := ctx.BodyParser(sentenceRequest)
	if err != nil {
		return fiber.NewError(400)
	}

	if err = api.db.DeclineTeam(sentenceRequest.Id); err != nil {
		return err
	}

	return ctx.SendStatus(200)
}

// Akzeptiert und editiert ggf. Teams (oder lehnt sie ab)
func (api *API) teamSelect(ctx *fiber.Ctx) error {
	teamSelectRequest := new(TeamSelectRequest)

	if err := api.db.UpdateTeam(teamSelectRequest.UserID, teamSelectRequest.TeamID); err != nil {
		return err
	}

	return ctx.SendStatus(200)
}
