using System;
using System.ComponentModel.DataAnnotations;

namespace DBotHackathon2021DB
{

    /// <summary>
    /// The child of all models
    /// </summary>
    public abstract class Entity
    {
        [Key]
        public int Id { get; set; }
    }
}
