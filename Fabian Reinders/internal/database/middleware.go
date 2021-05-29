package database

import (
	"github.com/fabiancdng/Arrangoer/internal/models"
)

// Definiert, welche Funktionen eine Datenbank-Middleware vorweisen muss
type Middleware interface {
	Open() error
	Close() error
	// Anmeldung für den Wettbewerb in der Datenbank speichern
	SaveApplication(application *models.ApplicationRequest) error
	// Das Team einer Anmeldung ändern
	UpdateTeam(userID int, teamID int) error
	// Gibt eine bestimmte Anmeldung zurück
	GetApplication(applicationID int) (*models.Application, error)
	// Gibt ID, Namen und Status des Teams zurück
	GetTeam(teamID int) (*models.Team, error)
	// Gibt alle Member eines Teams zurück
	GetTeamMembers(teamID int) ([]*models.Application, error)
	// Alle Anmeldungen zurückgeben
	GetApplications() ([]models.Application, error)
	// Alle Teams zurückgeben
	GetTeams() ([]models.Team, error)
	// Eine Anmeldung in der Datenbank als 'akzeptiert' markieren
	AcceptApplication(applicationID int, applicantName string) error
	// Ein Team in der Datenbank als 'akzeptiert' markieren
	ApproveTeam(teamID int, teamName string) error
	// Eine Anmeldung aus der Datenbank löschen, da sie abgelehnt wurde
	DeclineApplication(applicationID int) error
	// Ein Team aus der Datenbank löschen, da es abgelehnt wurde
	DeclineTeam(teamID int) error
}
