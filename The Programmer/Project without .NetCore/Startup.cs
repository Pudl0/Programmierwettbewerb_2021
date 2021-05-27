using DBotHackathon2021DB;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Text;

namespace Discord_bot_hackathon_schüler_2021
{
    /// <summary>
    /// Startup only used for creating and configuring Host and Services
    /// </summary>
    class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddDbContext<SavesContext>(options =>
            {
                options.UseSqlServer("Server=(localdb)\\mssqllocaldb;Database=SavesContext;Trusted_Connection=True;MultipleActiveResultSets=true",
                    x => x.MigrationsAssembly("DBotHackathon2021DB.Migrations"));
            });

#pragma warning disable ASP0000
            var serviceProvider = services.BuildServiceProvider();
#pragma warning restore ASP0000

            var bot = new Bot(serviceProvider);
            services.AddSingleton(bot);
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {

        }
    }
}
