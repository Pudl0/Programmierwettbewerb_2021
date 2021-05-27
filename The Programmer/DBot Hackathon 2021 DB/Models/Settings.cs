namespace DBotHackathon2021DB.Models
{
    /// <summary>
    /// Storage-type of settings [not used anymore]
    /// </summary>
    public class Settings : Entity
    {
        public string Language { get; set; }
        public string MessageMode { get; set; }
    }
}
