using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using DSharpPlus.Entities;
using DSharpPlus.Interactivity.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// Commands to search for a team or ask for a new teammember
    /// </summary>
    public class TeamsearchCommands : BaseCommandModule
    {

        // --------------------------------------------- German ---------------------------------------------

        [Description("Suche nach einem Teammitglied (teamsuche Kanal)" +
            "\n *Ask for a teammember (teamsuche channel)*")]
        [Command("teamsuche")]
        public async Task Teamsuche(CommandContext ctx)
        {
            await Teamsuche(ctx, LanguageManager.Language.German);
        }

        [Description("Suche nach einem Teammitglied in das Team mit einem Teamnamen (teamsuche Kanal)" +
            "\n *Ask for a teammember in the team of a specific teamname (teamsuche channel)*")]
        [Command("teamsuche")]
        public async Task Teamsuche(CommandContext ctx, params string[] teamnameVorschlag)
        {
            await Teamsuche(ctx, teamnameVorschlag.ToChainString(), LanguageManager.Language.German);
        }




        // --------------------------------------------- English ---------------------------------------------

        [Description("Ask for a teammember (teamsuche channel)" +
            "\n *Suche nach einem Teammitglied (teamsuche Kanal)*")]
        [Command("teamsearch")]
        public async Task Teamsearch(CommandContext ctx)
        {
            await Teamsuche(ctx, LanguageManager.Language.English);
        }

        [Description("Ask for a teammember in the team of a specific teamname (teamsuche channel)" +
            "\n *Suche nach einem Teammitglied in das Team mit einem Teamnamen (teamsuche Kanal)*")]
        [Command("teamsearch")]
        public async Task Teamsearch(CommandContext ctx, params string[] teamnameSuggestion)
        {
            await Teamsuche(ctx, teamnameSuggestion.ToChainString(), LanguageManager.Language.English);
        }




        // --------------------------------------------- Command executions ---------------------------------------------

        public async Task Teamsuche(CommandContext ctx, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "teamsuche")) { return; }
            var embed = new LanguageEmbed(
                "Teamsuche", ctx.Member.Username + " fragt nach einem Teammitglied",
                "Teamsearch", ctx.Member.Username + " asks for a teammember", DiscordColor.Blurple);

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx, embed, startLanguage, attachment: new LanguageAttachment(

                reCreateMessageAttachment: async delegate (DiscordMessage message)
                {
                    var thumbsUpEmoji = DiscordEmoji.FromName(ctx.Client, ":+1:");

                    await message.CreateReactionAsync(thumbsUpEmoji).ConfigureAwait(false);

                    var interact = ctx.Client.GetInteractivity();

                    var answer = await interact.WaitForReactionAsync(x => (x.Emoji == thumbsUpEmoji)
                    && message == x.Message && !x.User.IsBot && (x.User != ctx.User || Setup.presentationMode), TimeSpan.FromDays(1)).ConfigureAwait(false);

                    try { await message.DeleteAsync(); }
                    catch { return; }

                    if (answer.TimedOut)
                    {
                        _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client, "Die Anfrage wurde abgebrochen!", "Canceled request!", startLanguage);
                        await ctx.Member.SendMessageAsync("Deine Anfrage wurde automatisch abgebrochen! Probiere es doch nochmal!" +
                            "\n*Your request has been cancled automatically! Try again!*");
                        return;
                    }

                    if (answer.Result.Emoji == DiscordEmoji.FromName(ctx.Client, ":+1:"))
                    {
                        await ctx.Member.SendMessageAsync("Deine Anfrage wurde von " + answer.Result.User.Username + " angenommen!" +
                            "\n*Your request has been accepted by " + answer.Result.User.Username + "!*");
                    }

                })).ConfigureAwait(false);
        }

        public async Task Teamsuche(CommandContext ctx, string teamnameVorschlag, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "teamsuche")) { return; }

            await InteractionCommands.AskForNewTeamAsync(ctx, ctx.Member, teamnameVorschlag, startLanguage);
            if (!ctx.Guild.Roles.HasRole(teamnameVorschlag)) {
                await ctx.Member.SendMessageAsync("Bitte warte bis die Rolle erstellt wurde und frage erneut!" +
                    "\n*Please wait! Ask again when the role is created!*");
                return; }

            var embed = new LanguageEmbed(
                "Teamsuche", ctx.Member.Username + " fragt nach einem Teammitglied für sein Team " + teamnameVorschlag,
                "Teamsearch", ctx.Member.Username + " asks for a teammember for the team " + teamnameVorschlag, DiscordColor.Blurple);

            var message = ctx.Channel.SendTranslatedMessageAsync(ctx, embed, startLanguage, attachment: new LanguageAttachment(

                reCreateMessageAttachment: async delegate (DiscordMessage message)
                {
                    var thumbsUpEmoji = DiscordEmoji.FromName(ctx.Client, ":+1:");

                    await message.CreateReactionAsync(thumbsUpEmoji).ConfigureAwait(false);

                    var interact = ctx.Client.GetInteractivity();

                    var answer = await interact.WaitForReactionAsync(x => (x.Emoji == thumbsUpEmoji)
                    && message == x.Message && !x.User.IsBot && (x.User != ctx.User || Setup.presentationMode), TimeSpan.FromDays(1)).ConfigureAwait(false);

                    try { await message.DeleteAsync(); }
                    catch { return; }

                    if (answer.TimedOut)
                    {
                        _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client, "Die Anfrage wurde abgebrochen!", "Canceled request!", startLanguage);
                        await ctx.Member.SendMessageAsync("Deine Anfrage wurde automatisch abgebrochen! Probiere es doch nochmal!" +
                            "\n*Your request has been cancled automatically! Try again!*");
                        return;
                    }

                    if (answer.Result.Emoji == DiscordEmoji.FromName(ctx.Client, ":+1:"))
                    {
                        await ctx.Member.SendMessageAsync("Deine Anfrage wurde von " + answer.Result.User.Username + " angenommen!" +
                            "\n*Your request has been accepted by " + answer.Result.User.Username + "!*");

                        await InteractionCommands.TryAddRoleAsync(ctx, answer.Result.User.GetMember(ctx.Guild), teamnameVorschlag, startLanguage);
                        await InteractionCommands.TryAddRoleAsync(ctx, ctx.Member, teamnameVorschlag, startLanguage);
                    }

                })).ConfigureAwait(false);
        }
    }
}
