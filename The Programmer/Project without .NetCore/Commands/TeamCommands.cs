using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus.CommandsNext;
using DSharpPlus.CommandsNext.Attributes;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{
    /// <summary>
    /// The Commands, wich are used to create teams and add or remove their tags from the members
    /// </summary>
    public class TeamCommands : BaseCommandModule
    {
        // --------------------------------------------- German ---------------------------------------------

        [Description("Trete einem Team bei [bzw. bekomme eine Rolle] (rezeption Kanal)" +
            "\n *Join a team [or get the role] (rezeption channel)*")]
        [Command("beitreten")]
        public async Task Beitreten(CommandContext ctx, string teamname)
        {
            await Beitreten(ctx, teamname, LanguageManager.Language.German);
        }

        [Description("Trete einem Team bei [bzw. bekomme eine Rolle] (rezeption Kanal)" +
            "\n *Join a team [or get the role] (rezeption channel)*")]
        [Command("beitreten")]
        public async Task Beitreten(CommandContext ctx, params string[] teamname)
        {
            await Beitreten(ctx, teamname, LanguageManager.Language.German);
        }

        [Description("Gründe ein neues Team. **Diese Funktion erfordert die Zustimmung eines Adminestratoren!** (rezeption Kanal)" +
            "\n *Establish a new team. **This function requires an agreement of an Adminestrator** (rezeption channel)*")]
        [Command("gründe")]
        public async Task Gründe(CommandContext ctx, string teamname)
        {
            await Gründe(ctx, teamname, LanguageManager.Language.German);
        }

        [Description("Gründe ein neues Team. **Diese Funktion erfordert die Zustimmung eines Adminestratoren!** (rezeption Kanal)" +
            "\n *Establish a new team. **This function requires an agreement of an Adminestrator** (rezeption channel)*")]
        [Command("gründe")]
        public async Task Gründe(CommandContext ctx, params string[] teamname)
        {
            await Gründe(ctx, teamname, LanguageManager.Language.German);
        }

        [Description("Verlasse ein Team [bzw. lass dier eine Rolle wegnehmen] (rezeption Kanal)" +
            "\n *Leve a team [or remove a role] (rezeption channel)*")]
        [Command("verlassen")]
        public async Task Verlassen(CommandContext ctx, string teamname)
        {
            await Verlassen(ctx, teamname, LanguageManager.Language.German);
        }

        [Description("Verlasse ein Team [bzw. lass dier eine Rolle wegnehmen] (rezeption Kanal)" +
            "\n *Leve a team [or remove a role] (rezeption channel)*")]
        [Command("verlassen")]
        public async Task Verlassen(CommandContext ctx, params string[] teamname)
        {
            await Verlassen(ctx, teamname, LanguageManager.Language.German);
        }




        // --------------------------------------------- English ---------------------------------------------

        [Description("Join a team [or get the role] (rezeption channel)" +
            "\n *Trete einem Team bei [bzw. bekomme eine Rolle] (rezeption Kanal)*")]
        [Command("join")]
        public async Task Join(CommandContext ctx, string teamname)
        {
            await Beitreten(ctx, teamname, LanguageManager.Language.English);
        }

        [Description("Join a team [or get the role] (rezeption channel)" +
            "\n *Trete einem Team bei [bzw. bekomme eine Rolle] (rezeption Kanal)*")]
        [Command("join")]
        public async Task Join(CommandContext ctx, params string[] teamname)
        {
            await Beitreten(ctx, teamname, LanguageManager.Language.English);
        }

        [Description("Establish a new team. **This function requires an agreement of an Adminestrator** (rezeption channel)" +
            "\n *Gründe ein neues Team. **Diese Funktion erfordert die Zustimmung eines Adminestratoren!** (rezeption Kanal)*")]
        [Command("establish")]
        public async Task Establish(CommandContext ctx, string teamname)
        {
            await Gründe(ctx, teamname, LanguageManager.Language.English);
        }

        [Description("Establish a new team. **This function requires an agreement of an Adminestrator** (rezeption channel)" +
            "\n *Gründe ein neues Team. **Diese Funktion erfordert die Zustimmung eines Adminestratoren!**(rezeption Kanal)*")]
        [Command("establish")]
        public async Task Establish(CommandContext ctx, params string[] teamname)
        {
            await Gründe(ctx, teamname, LanguageManager.Language.English);
        }

        [Description("Leave a team [or remove a role] (rezeption channel)" +
            "\n *Verlasse ein Team [bzw. lass dier eine Rolle wegnehmen] (rezeption Kanal)*")]
        [Command("leave")]
        public async Task Leave(CommandContext ctx, string teamname)
        {
            await Verlassen(ctx, teamname, LanguageManager.Language.English);
        }

        [Description("Leave a team [or remove a role] (rezeption channel)" +
            "\n *Verlasse ein Team [bzw. lass dier eine Rolle wegnehmen] (rezeption Kanal)*")]
        [Command("leave")]
        public async Task Leave(CommandContext ctx, params string[] teamname)
        {
            await Verlassen(ctx, teamname, LanguageManager.Language.English);
        }





        // --------------------------------------------- Command executions ---------------------------------------------

        public async Task Beitreten(CommandContext ctx, string teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            await InteractionCommands.TryAddRoleAsync(ctx, ctx.Member, teamname, startLanguage);
        }

        public async Task Beitreten(CommandContext ctx, string[] teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            await Beitreten(ctx, teamname.ToChainString(), startLanguage).ConfigureAwait(false);
        }

        public async Task Gründe(CommandContext ctx, string teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            await InteractionCommands.AskForNewTeamAsync(ctx, ctx.Member, teamname, startLanguage);
        }

        public async Task Gründe(CommandContext ctx, string[] teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            await Gründe(ctx, teamname.ToChainString(), startLanguage).ConfigureAwait(false);
        }

        [Command("verlassen")]
        public async Task Verlassen(CommandContext ctx, string teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }

            var role = ctx.Guild.Roles.FirstOrDefault(x => x.Value.Name == teamname);

            if (role.Value != null)
            {
                await ctx.Member.RevokeRoleAsync(role.Value).ConfigureAwait(false);

                await ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                    "Rolle '" + teamname + "', wurde dir erfolgreich weggenommen!",
                    "The role '" + teamname + "' have been removed sccessfully!", startLanguage).ConfigureAwait(false);
            }
            else
            {
                await ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                    "Rolle '" + teamname + "', wurde nicht gefunden!",
                    "Role '" + teamname + "' not found!", startLanguage).ConfigureAwait(false);
            }
        }

        public async Task Verlassen(CommandContext ctx, string[] teamname, LanguageManager.Language startLanguage)
        {
            if (!Requires.Channel(ctx, "rezeption")) { return; }
            await Verlassen(ctx, teamname.ToChainString(), startLanguage).ConfigureAwait(false);
        }
    }
}
