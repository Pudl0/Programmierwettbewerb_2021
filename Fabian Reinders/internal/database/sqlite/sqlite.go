package sqlite

import (
	"database/sql"
	"io/ioutil"
	"log"
	"os"

	"github.com/fabiancdng/Arrangoer/internal/models"
	"github.com/gofiber/fiber/v2"
	_ "github.com/mattn/go-sqlite3"
)

// Datenbank Middleware für SQLite
type SQLite struct {
	db *sql.DB
}

// Öffnet die Datenbank und erstellt (falls noch nicht vorhanden) alle nötigen Tabellen
func (sqlite *SQLite) Open() error {
	var err error

	if _, err := os.Stat("./db.db"); os.IsNotExist(err) {
		err := ioutil.WriteFile("./db.db", []byte(""), 0755)
		if err != nil {
			return err
		}
		log.Println("Datenbank-Datei wurde erstellt.")
	}

	sqlite.db, err = sql.Open("sqlite3", "./db.db")
	if err != nil {
		return err
	}

	_, err = sqlite.db.Exec("CREATE TABLE IF NOT EXISTS `teams` (`id` INTEGER PRIMARY KEY, `name` VARCHAR(50), `approved` INT)")
	if err != nil {
		return err
	}

	_, err = sqlite.db.Exec("CREATE TABLE IF NOT EXISTS `applications` (`id` INTEGER PRIMARY KEY, `name` VARCHAR(100), `email` VARCHAR(200), `team` INT, `user_id` VARCHAR(100), `accepted` INT, FOREIGN KEY (`team`) REFERENCES `teams`(`id`))")
	if err != nil {
		return err
	}

	return nil
}

// Schließt die Verbindung zur Datenbank
func (sqlite *SQLite) Close() error {
	if err := sqlite.db.Close(); err != nil {
		return err
	}
	return nil
}

// Speichert eine Anmeldung für den Wettbewerb in der Datenbank
// Ebenso wie das Team (bzw. erstellt es ggf.)
func (sqlite *SQLite) SaveApplication(application *models.ApplicationRequest) error {
	// Prüfen, ob der Nutzer sich bereits angemeldet hat
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `applications` WHERE `name`=?", application.Name).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}
		// Nutzer ist noch nicht angemeldet
	} else {
		// Nutzer ist bereits angemeldet
		return fiber.NewError(302)
	}

	var teamID int
	// Prüfen, ob das eingegebene Team bereits existiert
	err = sqlite.db.QueryRow("SELECT `id` FROM `teams` WHERE `name`=?", application.Team).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Das Team existiert (in der DB) noch nicht und muss daher erstellt werden
		var res sql.Result
		res, err = sqlite.db.Exec("INSERT INTO `teams` VALUES (NULL, ?, 0)", application.Team)
		if err != nil {
			return fiber.NewError(500)
		}

		var lastInsertId int64
		lastInsertId, err = res.LastInsertId()
		if err != nil {
			return fiber.NewError(500)
		}

		teamID = int(lastInsertId)

	} else {
		// Das Team existiert (in der DB) bereits
		teamID = id
	}

	// Die Anmeldung in der Datenbank speichern
	// Das Team wird über eine Team-ID (die auf eine andere Tabelle weist) referenziert
	_, err = sqlite.db.Exec("INSERT INTO `applications` VALUES (NULL, ?, ?, ?, ?, 0)", application.Name, application.Email, teamID, application.UserID)
	if err != nil {
		return fiber.NewError(500)
	}

	log.Printf("%s hat sich für den Wettbewerb angemeldet.", application.Name)

	return nil
}

// Gibt eine bestimmte Anmeldung zurück
func (sqlite *SQLite) GetApplication(applicationID int) (*models.Application, error) {
	application := new(models.Application)

	rows, err := sqlite.db.Query("SELECT * FROM `applications` WHERE `id`=?", applicationID)
	if err != nil {
		return application, fiber.NewError(500)
	}

	for rows.Next() {
		rows.Scan(&application.ID, &application.Name, &application.Email, &application.Team.ID, &application.UserID, &application.Accepted)
	}

	return application, nil
}

// Gibt alle Member sowie den Namen eines Teams zurück
func (sqlite *SQLite) GetTeam(teamID int) (*models.Team, error) {
	team := new(models.Team)

	rows, err := sqlite.db.Query("SELECT * FROM `teams` WHERE `id`=?", teamID)
	if err != nil {
		return team, fiber.NewError(500)
	}

	for rows.Next() {
		rows.Scan(&team.ID, &team.Name, &team.Approved)
	}

	return team, nil
}

// Gibt eine Liste mit allen Anmeldungen (aus der Datenbank) zurück
func (sqlite *SQLite) GetApplications() ([]models.Application, error) {
	// Alle Teams auslesen
	rows, err := sqlite.db.Query("SELECT * FROM `teams`")
	if err != nil {
		return nil, fiber.NewError(500)
	}

	// Team-ID auf Team struct mappen
	teams := make(map[int]models.Team)

	currentTeam := new(models.Team)
	for rows.Next() {
		rows.Scan(&currentTeam.ID, &currentTeam.Name, &currentTeam.Approved)
		teams[currentTeam.ID] = *currentTeam
	}

	rows, err = sqlite.db.Query("SELECT * FROM `applications`")
	if err != nil {
		return nil, fiber.NewError(500)
	}

	currentApplication := new(models.Application)
	applications := []models.Application{}

	var currentTeamID int
	for rows.Next() {
		rows.Scan(&currentApplication.ID, &currentApplication.Name, &currentApplication.Email, &currentTeamID, &currentApplication.UserID, &currentApplication.Accepted)
		currentApplication.Team = teams[currentTeamID]
		applications = append(applications, *currentApplication)
	}

	return applications, nil
}

// Gibt eine Liste mit allen Anmeldungen (aus der Datenbank) zurück
func (sqlite *SQLite) GetTeams() ([]models.Team, error) {
	// Alle Teams auslesen
	rows, err := sqlite.db.Query("SELECT * FROM `teams`")
	if err != nil {
		return nil, fiber.NewError(500)
	}

	// Team-ID auf Team struct mappen
	teams := []models.Team{}

	currentTeam := new(models.Team)
	for rows.Next() {
		rows.Scan(&currentTeam.ID, &currentTeam.Name, &currentTeam.Approved)
		teams = append(teams, *currentTeam)
	}

	return teams, nil
}

// Eine Anmeldung in der Datenbank als 'akzeptiert' markieren
func (sqlite *SQLite) AcceptApplication(applicationID int, applicantName string) error {
	// Prüfen, ob die Anmeldung in der Datenbank existiert
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `applications` WHERE `id`=?", applicationID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Die Anmeldung existiert (in der DB) nicht
		return fiber.NewError(404)
	}

	// Die Anmeldung existiert (in der DB) und der Status wird entsprechend geupdatet
	_, err = sqlite.db.Exec("UPDATE `applications` SET `accepted`=1, `name`=?  WHERE `id`=?", applicantName, applicationID)
	if err != nil {
		return fiber.NewError(500)
	}

	return nil
}

// Ein Team in der Datenbank als 'bestätigt' markieren
func (sqlite *SQLite) ApproveTeam(teamID int, teamName string) error {
	// Prüfen, ob das Team in der Datenbank existiert
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `teams` WHERE `id`=?", teamID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Das Team existiert (in der DB) nicht
		return fiber.NewError(404)
	}

	// Das Team existiert (in der DB) und der Status wird entsprechend geupdatet
	_, err = sqlite.db.Exec("UPDATE `teams` SET `approved`=1, `name`=? WHERE `id`=?", teamName, teamID)
	if err != nil {
		return fiber.NewError(500)
	}

	return nil
}

// Eine Anmeldung aus der Datenbank löschen, da sie abgelehnt wurde
func (sqlite *SQLite) DeclineApplication(applicationID int) error {
	// Prüfen, ob die Anmeldung in der Datenbank existiert
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `applications` WHERE `id`=?", applicationID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Die Anmeldung existiert (in der DB) nicht
		return fiber.NewError(404)
	}

	// Die Anmeldung existiert (in der DB) und wird entsprechend gelöscht
	_, err = sqlite.db.Exec("DELETE FROM `applications` WHERE `id`=?", applicationID)
	if err != nil {
		return fiber.NewError(500)
	}

	return nil
}

// Ein Team aus der Datenbank löschen, da es abgelehnt wurde
func (sqlite *SQLite) DeclineTeam(teamID int) error {
	// Prüfen, ob das Team in der Datenbank existiert
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `teams` WHERE `id`=?", teamID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Das Team existiert (in der DB) nicht
		return fiber.NewError(404)
	}

	// Das Team existiert (in der DB) und wird entsprechend gelöscht
	_, err = sqlite.db.Exec("DELETE FROM `teams` WHERE `id`=?", teamID)
	if err != nil {
		return fiber.NewError(500)
	}

	return nil
}

// Gibt alle Member eines Teams zurück
func (sqlite *SQLite) GetTeamMembers(teamID int) ([]*models.Application, error) {
	var teamMembers []*models.Application
	rows, err := sqlite.db.Query("SELECT * FROM `applications` WHERE `team`=?", teamID)
	if err != nil {
		return teamMembers, err
	}

	currentApplication := new(models.Application)
	for rows.Next() {
		rows.Scan(&currentApplication.ID, &currentApplication.Name, &currentApplication.Email, &currentApplication.Team.ID, &currentApplication.UserID, &currentApplication.Accepted)
		log.Println(currentApplication)
		teamMembers = append(teamMembers, currentApplication)
	}

	return teamMembers, err
}

// Das Team einer Anmeldung ändern
func (sqlite *SQLite) UpdateTeam(userID int, teamID int) error {
	// Prüfen, ob die Anmeldung in der Datenbank existiert
	id := 0
	err := sqlite.db.QueryRow("SELECT `id` FROM `applications` WHERE `user_id`=?", userID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Die Anmeldung existiert (in der DB) nicht
		return fiber.NewError(404)
	}

	// Prüfen, ob das Team in der Datenbank existiert
	err = sqlite.db.QueryRow("SELECT `id` FROM `teams` WHERE `id`=?", teamID).Scan(&id)
	if err != nil {
		if err != sql.ErrNoRows {
			return fiber.NewError(500)
		}

		// Das Team existiert (in der DB) nicht
		log.Println("team")
		return fiber.NewError(404)
	}

	// Das Team existiert (in der DB) und die Anmeldung wird entsprechend aktualisiert
	_, err = sqlite.db.Exec("UPDATE `applications` SET `team`=? WHERE `user_id`=?", teamID, userID)
	if err != nil {
		return fiber.NewError(500)
	}

	return nil
}
