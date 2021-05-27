using DBotHackathon2021DB;
using DBotHackathon2021DB.Models;
using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using DSharpPlus.Entities;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{
    public class RegisterCommands : BaseCommandModule
    {
        private readonly SavesContext context;

        public RegisterCommands(SavesContext _context)
        {
            context = _context;
        }

        // --------------------------------------------- German ---------------------------------------------

        [Description("Melde dich für den Wettbewerb an (rezeption Kanal)" +
            "\n *Register for the competition (rezeption channel)*")]
        [Command("anmelden")]
        public async Task Anmelden(CommandContext ctx,
            [Description("Dein Name \n *Your name*")] string name,
            [Description("Dein Nachname \n *Your surname*")] string nachname,
            [Description("Deine Klasse (nur Jahrgangsstufe) \n *Your class*")] int klasse,
            [Description("Der Name deiner Schule \n *The name of your school*")] string schule,
            [Description("Der Name deines Teams (falls du keines hast schreibe einfach '**[keines]**')" +
            "\n *The name of your team (when you haven´t one write '**[keines]**'*")] params string[] team)
        {
            await Anmelden(ctx, name, nachname, klasse, schule, team, LanguageManager.Language.German);
        }

        [Description("Zeige alle anmeldungen an (nur Admin; admin Kanal)" +
            "\n *See all registrations (Admin only; admin channel)*")]
        [Command("anmeldungen")]
        public async Task Anmeldungen(CommandContext ctx)
        {
            Anmeldungen(ctx, LanguageManager.Language.German);
        }


        [Description("Lösche eine Anmeldung (nur Admin; admin Kanal)" +
            "\n *Delete a registration (Admin only; admin channel)*")]
        [Command("abmelden")]
        public async Task Abmelden(CommandContext ctx, string name, string nachname)
        {
            Abmelden(ctx, name, nachname, LanguageManager.Language.German);
        }

        [Description("Lösche alle Anmeldungen (nur Admin; admin Kanal)" +
            "\n *Delete all registrations (Admin only; admin channel)*")]
        [Command("anmeldungen_löschen")]
        public async Task AnmeldungenLöschen(CommandContext ctx)
        {
            AnmeldungenClear(ctx, LanguageManager.Language.German);
        }



        // --------------------------------------------- English ---------------------------------------------

        [Description("Register for the competition (rezeption channel)" +
            "\n *Melde dich für den Wettbewerb an (rezeption Kanal)*")]
        [Command("register")]
        public async Task Register(CommandContext ctx,
            [Description("Your name \n *Dein Name*")] string name,
            [Description("Your surname \n *Dein Nachname*")] string surname,
            [Description("Your class \n *Deine Klasse (nur Jahrgangsstufe)*")] int @class,
            [Description("The name of your school \n *Der Name deiner Schule*")] string school,
            [Description("The name of your team (when you haven´t one write '**[keines]**')" +
            "\n *Der Name deines Teams (falls du keines hast schreibe einfach '**[keines]**'*")] params string[] team)
        {
            await Anmelden(ctx, name, surname, @class, school, team, LanguageManager.Language.English);
        }


        [Description("See all registrations (Admin only; admin channel)" +
            "\n *Zeige alle anmeldungen an (nur Admin; admin Kanal)*")]
        [Command("registrations")]
        public async Task Registrations(CommandContext ctx)
        {
            Anmeldungen(ctx, LanguageManager.Language.English);
        }


        [Description("Delete a registration (Admin only; admin channel)" +
            "\n *Lösche eine Anmeldung (nur Admin; admin Kanal)*")]
        [Command("sign_out")]
        public async Task SignOut(CommandContext ctx, string name, string surname)
        {
            Abmelden(ctx, name, surname, LanguageManager.Language.English);
        }

        [Description("Delete all registrations (Admin only; admin channel)" +
            "\n *Lösche alle Anmeldungen (nur Admin; admin Kanal)*")]
        [Command("clear_registrations")]
        public async Task ClearRegistrations(CommandContext ctx)
        {
            AnmeldungenClear(ctx, LanguageManager.Language.English);
        }



        // --------------------------------------------- Command executions ---------------------------------------------

        public async Task Anmelden(CommandContext ctx, string name, string nachname, int klasse, string schule, string[] team, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            var anmeldung = new Anmeldung
            {
                Name = name,
                Nachname = nachname,
                Klasse = klasse,
                SchuleName = schule,
                Teamname = team.ToChainString(),
            };

            await context.Anmeldungen.AddAsync(anmeldung).ConfigureAwait(false);
            await context.SaveChangesAsync().ConfigureAwait(false);

            await ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                "Du wurdest erfolgreich angemeldet!", "You have been registered successfully!", startLanguage);
        }

        public void Abmelden(CommandContext ctx, string name, string nachname, LanguageManager.Language startLanguage)
        {
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }
            if (!Requires.Channel(ctx, "admin")) { return; }

            var registration = context.Anmeldungen.FirstOrDefault(x => x.Name == name && x.Nachname == nachname);

            if (registration == null)
            {
                _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                    "Anmeldung von '" + name + " " + nachname + "' wurde nicht gefunden!",
                    "Registration of '" + name + " " + nachname + "' not found!", startLanguage);
                return;
            }

            context.Anmeldungen.Remove(registration);


            _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                   "Anmeldung von '" + name + " " + nachname + "' wurde erfolgreich gelöscht!",
                   "Registration of '" + name + " " + nachname + "' removed successfully!", startLanguage);

            context.SaveChanges();
        }


        public void AnmeldungenClear(CommandContext ctx, LanguageManager.Language startLanguage)
        {
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }
            if (!Requires.Channel(ctx, "admin")) { return; }

            var anmeldungen = context.Anmeldungen;
            foreach(Anmeldung anmeldung in anmeldungen)
            {
                context.Anmeldungen.Remove(anmeldung);
            }
            _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                "Alle Anmeldungen gelöscht",
                "Cleared all registrations", startLanguage);

            context.SaveChanges();
        }


        public void Anmeldungen(CommandContext ctx, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "admin")) { return; }
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }
            if (context.Anmeldungen.Count() == 0)
            {
                _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                "Keine Anmeldungen vorhanden!",
                "There are no registrations", startLanguage);
                return;
            }
            foreach (Anmeldung anmeldung in context.Anmeldungen)
            {
                var embed = new LanguageEmbed
                {
                    germanEmbed = new DiscordEmbedBuilder
                    {
                        Title = "Anmeldung ***" + anmeldung.Name + " " + anmeldung.Nachname + "***:",
                        Description =
                            "Klasse: **" + anmeldung.Klasse + "**\n" +
                            "Schule: **" + anmeldung.SchuleName + "**\n" +
                            "Team: **" + anmeldung.Teamname + "**",
                        Color = DiscordColor.Gold
                    },
                    englishEmbed = new DiscordEmbedBuilder()
                    {
                        Title = "Registration ***" + anmeldung.Name + " " + anmeldung.Nachname + "***:",
                        Description =
                            "Class: **" + anmeldung.Klasse + "**\n" +
                            "School: **" + anmeldung.SchuleName + "**\n" +
                            "Team: **" + anmeldung.Teamname + "**",
                        Color = DiscordColor.Gold
                    }
                };

                _ = ctx.Channel.SendTranslatedMessageAsync(ctx, embed, startLanguage);
            }
        }
    }
}
