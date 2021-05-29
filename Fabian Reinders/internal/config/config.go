package config

import (
	"fmt"
	"os"

	"gopkg.in/yaml.v2"
)

type Config struct {
	Discord struct {
		Token                  string `yaml:"token"`
		ClientID               string `yaml:"client_id"`
		ClientSecret           string `yaml:"client_secret"`
		ServerID               string `yaml:"server_id"`
		Prefix                 string `yaml:"prefix"`
		LobbyChannelID         string `yaml:"lobby_channel_id"`
		NotificationsChannelID string `yaml:"notifications_channel_id"`
		InviteLink             string `yaml:"invite_link"`
		WelcomeMessage         string `yaml:"welcome_message"`
	} `yaml:"discord"`

	API struct {
		AddressAndPort string `yaml:"address_and_port"`
		APIUrl         string `yaml:"api_url"`
		FrontendURL    string `yaml:"frontend_url"`
	} `yaml:"api"`
}

// Prüft, ob die Config-Datei existiert
func validateConfigPath(path string) error {
	s, err := os.Stat(path)
	if err != nil {
		return err
	}
	if s.IsDir() {
		return fmt.Errorf("Die Config-Datei existiert nicht oder konnte nicht gelesen werden. (Pfad: '%s')", path)
	}
	return nil
}

// Parst die config.yml
func ParseConfig(path string) (*Config, error) {
	if err := validateConfigPath(path); err != nil {
		return nil, err
	}

	config := new(Config)

	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}

	decoder := yaml.NewDecoder(file)

	if err = decoder.Decode(&config); err != nil {
		return nil, err
	}

	return config, nil
}
