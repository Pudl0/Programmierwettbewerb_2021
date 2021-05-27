using DBotHackathon2021DB.Models;
using Microsoft.EntityFrameworkCore;

namespace DBotHackathon2021DB
{

    /// <summary>
    /// Databese context [settings not used anymore]
    /// </summary>
    public class SavesContext : DbContext
    {
        public SavesContext(DbContextOptions<SavesContext> options) : base(options)
        {

        }

        public DbSet<Settings> Settings { get; set; }

        public DbSet<Anmeldung> Anmeldungen { get; set; }
    }
}
