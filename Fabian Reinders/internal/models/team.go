package models

// Daten für ein Team
type Team struct {
	ID       int    `json:"id"`
	Name     string `json:"name"`
	Approved int    `json:"approved"`
}
