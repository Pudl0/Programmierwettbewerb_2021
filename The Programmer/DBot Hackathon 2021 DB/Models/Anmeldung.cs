namespace DBotHackathon2021DB.Models
{

    /// <summary>
    /// Storage type of registrations
    /// </summary>
    public class Anmeldung : Entity
    {
        public string Name { get; set; }
        public string Nachname { get; set; }
        public string Teamname { get; set; }
        public int Klasse { get; set; }
        public string SchuleName { get; set; }
    }
}
