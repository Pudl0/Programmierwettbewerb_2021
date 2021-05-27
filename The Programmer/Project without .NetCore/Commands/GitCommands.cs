using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using DSharpPlus.Entities;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// To open the programming competitions Github-page (by a teamname [optional])
    /// </summary>
    public class GitCommands : BaseCommandModule
    {
        [Description("Öffne Github \n *Open Github*")]
        [Command("github")]
        public async Task Git(CommandContext ctx)
        {
            string path = "https://github.com/Pudl0/Programmierwettbewerb_2021";

            var embed = new LanguageEmbed("Link zu Github", path, "Link to Github", path, DiscordColor.Gold);

            // Process.Start(@"C:\Program Files (x86)\Mozilla Firefox\firefox.exe", path); // ----- A little shortcut -----

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx, embed);
        }

        [Description("Öffne Github mit Teamname \n *Open Github with a teamname*")]
        [Command("github")]
        public async Task Git(CommandContext ctx, string teamname)
        {
            string path = "https://github.com/Pudl0/Programmierwettbewerb_2021/tree/main/" + teamname.Replace(" ", "%20");

            var embed = new LanguageEmbed("Link zu Github", path, "Link to Github", path, DiscordColor.Gold);

            // Process.Start(@"C:\Program Files (x86)\Mozilla Firefox\firefox.exe", path); // ----- A little shortcut -----

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx, embed);
        }

        [Description("Öffne Github mit Teamname \n *Open Github with a teamname*")]
        [Command("github")]
        public async Task Git(CommandContext ctx, params string[] teamname)
        {
            await Git(ctx, teamname.ToChainString()).ConfigureAwait(false);
        }
    }
}
