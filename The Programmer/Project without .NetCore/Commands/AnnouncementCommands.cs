using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using DSharpPlus.Entities;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// Commands to create Announcements and send them to specific channels
    /// </summary>
    public class AnnouncementCommands : BaseCommandModule
    {

        // --------------------------------------------- German ---------------------------------------------

        [Description("Sende eine Ankündigung [auch in mehrere Kanäle] (nur Admin; admin kanal)" +
            "\n* Send a announcement [also in more channels] (Admin only; admin channel)*")]
        [Command("ankündigung")]
        public async Task Ankündigung(CommandContext ctx, string text, params string[] channels)
        {
            await Ankündigung(ctx, text, channels, LanguageManager.Language.German);
        }



        // --------------------------------------------- English ---------------------------------------------

        [Description("Send a announcement [also in more channels] (Admin only; admin channel)" +
            "\n  *Sende eine Ankündigung [auch in mehrere Kanäle] (nur Admin; admin kanal)*")]
        [Command("announcement")]
        public async Task Announcement(CommandContext ctx, string text, params string[] channels)
        {
            await Ankündigung(ctx, text, channels, LanguageManager.Language.English);
        }


        // --------------------------------------------- Command executions ---------------------------------------------

        public async Task Ankündigung(CommandContext ctx, string text, string[] channels, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "admin")) { return; }
            if (!Requires.AdminOrOwner(ctx.Member)) { return; }
            foreach (string channelName in channels)
            {
                DiscordChannel channel = ctx.Guild.Channels.FirstOrDefault(x => x.Value.Name == channelName && x.Value.Type == DSharpPlus.ChannelType.Text).Value;
                if (channel == null)
                {
                    channel = await ctx.Guild.CreateTextChannelAsync(channelName).ConfigureAwait(false);
                }

                var embed = new LanguageEmbed("Ankündigung: ", text, "Announcement:", text + " *(cannot translate)*", DiscordColor.Gold);

                _ = channel.SendTranslatedMessageAsync(ctx, embed, startLanguage, attachment: new LanguageAttachment(
                    
                    createMessageAttachment: async delegate (DiscordMessage message)
                    {
                        await message.PinAsync();
                    }
                    
                    )).ConfigureAwait(false);
            }
        }
    }
}
