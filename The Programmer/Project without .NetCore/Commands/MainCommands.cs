using DBotHackathon2021DB.Models;
using DBotHackathon2021DB;
using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using DSharpPlus.Entities;
using DSharpPlus.Interactivity.Extensions;
using System;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// All the other commands here
    /// </summary>
    class MainCommands : BaseCommandModule
    {
        [Description("Erstelle alles wichtige für den Server manuell (nur Admin; admin kanal) [Erfolgt automatisch bei Start des Bots]" +
            "*\n Create server generals manualy (admin only; admin channel) [Starts atomatically on start of the bot]*")]
        [Command("setup")]
        public async Task StartSetup(CommandContext ctx)
        {
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }
            if (!Requires.Channel(ctx, "admin")) { return; }
            await Setup.StartSetup(ctx.Guild);
            await ctx.Member.GrantRoleAsync(ctx.Guild.Roles.Values.FirstOrDefault(x => x.Name == "Admin"));
            await ctx.Channel.SendTranslatedMessageAsync(ctx.Client, "Setup war erfolgreich!", "Setup was succesful!");
        }

        [Description("Suche nach hilfe auf Stackoverflow" +
            "\n *Search for help on Stackoverflow*")]
        [Command("hilfe")]
        public async Task Hilfe(CommandContext ctx, [Description("Sucheingabe")] params string[] suche)
        {
            string path = "https://stackoverflow.com/search?q=" + suche.ToChainString("+");

            await ctx.Channel.SendMessageAsync(path);

            // Process.Start(@"C:\Program Files (x86)\Mozilla Firefox\firefox.exe", path); // ----- A little shortcut -----
        }


        [Description("Zeigt dir die Links zu den Streams" +
            "\n *Shows you the links to the streams*")]
        [Command("stream")]
        public async Task Stream(CommandContext ctx)
        {
            var links = "Twitch: https://www.twitch.tv/hackathonleer" +
                      "\nYouTube: https://www.youtube.com/channel/UCkeTyOUOKObW1pNlfptzC2A";
            var embed = new LanguageEmbed("Links zu den Streams:", links, "Links for the Streams:", links, DiscordColor.Blurple);

            await ctx.Channel.SendTranslatedMessageAsync(ctx, embed).ConfigureAwait(false);
        }

        [Description("Schaltet Präsentationsmodus an oder aus!" +
            "\n --> Der Präsentationsmodus ist zur Präsentation von einigen Funktionen gedacht! Darunter fällt:" +
            "\n - Befehle, die nur Admins oder Owner machen können, kann nur noch von einem Admin ausgeführt werden" +
            "\n - Anfragen oder ähnliches kann man auch selber annehmen" +
            "\n - Anfragen, die Admins bestätigen müssen, müssen auch bestätigt werden, wenn ein Admin die Anfrage erstellt hat" +
            "\n" +
            "\n *Turns the presentationmode on or off!" +
            "\n --> The presentationmode is used for showing funktions in a presentation! This includes:" +
            "\n - Commands, which are normaly only available for admins or owners are now only available for admins" +
            "\n - Requests or somithing similar can be assumed by yourself" +
            "\n - Requests, which have to be accepted by admins, now have to be accepted when an admin created the request*")]
        [Command("p")]
        public async Task PräsentationsModus(CommandContext ctx)
        {
            if (!Requires.Channel(ctx, "admin")) { return; }
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }

            Setup.presentationMode = !Setup.presentationMode;

            await ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                "Präsentationsmodus wurde " + (Setup.presentationMode ? "aktiviert" : "deaktiviert!"),
                "Presentationmode has been " + (Setup.presentationMode ? "activated" : "deactivated!")).ConfigureAwait(false);
        }
    }
}
