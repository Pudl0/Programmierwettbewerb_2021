using Microsoft.EntityFrameworkCore.Migrations;

namespace DBotHackathon2021DB.Migrations.Migrations
{
    public partial class InitialCreate : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Anmeldungen",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Nachname = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Teamname = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Klasse = table.Column<int>(type: "int", nullable: false),
                    SchuleName = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Anmeldungen", x => x.Id);
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Anmeldungen");
        }
    }
}
