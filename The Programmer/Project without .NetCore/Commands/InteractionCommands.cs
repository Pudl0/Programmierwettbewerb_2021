using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus;
using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using DSharpPlus.EventArgs;
using DSharpPlus.Interactivity;
using DSharpPlus.Interactivity.Extensions;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{
    public static class InteractionCommands
    {
        public delegate void VerifyAttachment(bool isVerified);

        /// <summary>
        /// Ask for verifikation to get a role, your not normally permittet to get (Admin)
        /// </summary>
        /// <param name="member">The member, which get the role atomatically</param>
        public static void AskForRoleVerifikation(CommandContext ctx, DiscordMember member, string rolle, LanguageManager.Language startLanguage)
        {
            var askForAdminEmbed = new LanguageEmbed
            {
                germanEmbed = new DiscordEmbedBuilder
                {
                    Title = "@Admin? Darf " + ctx.Member.DisplayName + " die Rolle '" + rolle + "' bekommen?",
                    Color = DiscordColor.Orange
                },
                englishEmbed = new DiscordEmbedBuilder
                {
                    Title = "@Admin? Can " + ctx.Member.DisplayName + " get the role '" + rolle + "'?",
                    Color = DiscordColor.Orange
                }
            };

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx, askForAdminEmbed, startLanguage, attachment: new LanguageAttachment(

                reCreateMessageAttachment: async delegate (DiscordMessage message)
                {
                    if (await CreateVote(ctx, message, "Admin", startLanguage))
                    {
                        await AddRoleAsync(ctx, member, rolle, startLanguage);
                    }

                })).ConfigureAwait(false);
        }

        /// <summary>
        /// Create a vote, where you can vote for or against a thing (adds reactionbuttons)
        /// </summary>
        /// <returns>Is the vote for the thing?</returns>
        public static async Task<bool> CreateVote(CommandContext ctx, DiscordMessage message, string roleName, LanguageManager.Language startLanguage)
        {
            var thumbsUpEmoji = DiscordEmoji.FromName(ctx.Client, ":+1:");
            var thumbsDownEmoji = DiscordEmoji.FromName(ctx.Client, ":-1:");

            await message.CreateReactionAsync(thumbsUpEmoji).ConfigureAwait(false);
            await message.CreateReactionAsync(thumbsDownEmoji).ConfigureAwait(false);

            var interact = ctx.Client.GetInteractivity();

            try
            {
                var answer = await interact.WaitForReactionAsync(x => (x.Emoji == thumbsUpEmoji || x.Emoji == thumbsDownEmoji)
                && x.User.GetMember(ctx.Guild).HasRole(roleName) && message == x.Message, TimeSpan.FromDays(1)).ConfigureAwait(false);

                try { await message.DeleteAsync(); } catch { return false; }
                if (answer.TimedOut)
                {
                    _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                        "Die Anfrage wurde abgebrochen!", "Canceled request!", startLanguage);
                    return false;
                }
                return answer.Result.Emoji == thumbsUpEmoji;
            }
            catch { return false; }
        }

        /// <summary>
        /// Grand a role. When it´s admin ask for permission
        /// </summary>
        /// <param name="member">The member, which get the role atomatically</param>
        public static async Task TryAddRoleAsync(CommandContext ctx, DiscordMember member, string roleName, LanguageManager.Language startLanguage)
        {
            if (Setup.presentationMode ? roleName == "Admin" :
                roleName == "Admin" && !ctx.Member.IsOwner && !ctx.Member.HasRole("Admin"))
            {
                AskForRoleVerifikation(ctx, member, roleName, startLanguage);
            }
            else
            {
                await AddRoleAsync(ctx, member, roleName, startLanguage);
            }
        }

        /// <summary>
        /// Grand a role
        /// </summary>
        /// <param name="member">The member, which get the role atomatically</param>
        public static async Task AddRoleAsync(CommandContext ctx, DiscordMember member, string roleName, LanguageManager.Language startLanguage)
        {
            var role = MainExtensions.GetRoleByName(roleName, ctx);

            if (role != null)
            {
                await member.GrantRoleAsync(role).ConfigureAwait(false);

                _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client, 
                    "Die Rolle '" + roleName + "', wurde **" + member.DisplayName + "** erfolgreich zugewiesen!",
                    "The role '" + roleName + "' has been succesfully assigned to **" + member.DisplayName + "**!", startLanguage).ConfigureAwait(false);
            }
            else
            {
                _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                    "Rolle '" + roleName + "', wurde nicht gefunden!",
                    "Role '" + roleName + "' not found!", startLanguage).ConfigureAwait(false);
                await AskForNewTeamAsync(ctx, member, roleName, startLanguage).ConfigureAwait(false);
            }
        }

        /// <summary>
        /// Create a team and ask for it, when there is no permission for it
        /// </summary>
        /// <param name="member">The member, which get the role atomatically</param>
        public static async Task AskForNewTeamAsync(CommandContext ctx, DiscordMember member, string roleName, LanguageManager.Language startLanguage)
        {
            if (ctx.Guild.Roles.FirstOrDefault(x => x.Value.Name == roleName).Value != null) 
            {
                _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                    "Das Team gibt es bereits!",
                    "The team already exists!", startLanguage);
                return; 
            }

            if (ctx.Member.HasRole("Admin") && !Setup.presentationMode)
            {
                await NewTeamAsync(ctx, member, roleName, startLanguage);
                return;
            }


            var askForAdminEmbed = new LanguageEmbed
            {
                germanEmbed = new DiscordEmbedBuilder
                {
                    Title = "Admin? Darf das Team '" + roleName + "' erstellt werden?",
                    Color = DiscordColor.Orange
                },
                englishEmbed = new DiscordEmbedBuilder
                {
                    Title = "Admin? May the team '" + roleName + "' be created?",
                    Color = DiscordColor.Orange
                }
            };

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx, askForAdminEmbed, attachment: new LanguageAttachment(
                
                reCreateMessageAttachment: async delegate (DiscordMessage message)
                {
                    try
                    {
                        if (await CreateVote(ctx, message, "Admin", startLanguage))
                        {
                            await NewTeamAsync(ctx, member, roleName, startLanguage);
                        }
                    }
                    catch
                    {

                    }

                })).ConfigureAwait(false);
        }


        /// <summary>
        /// Create a team
        /// </summary>
        /// <param name="member">The member, which get the role atomatically</param>
        public static async Task NewTeamAsync(CommandContext ctx, DiscordMember member, string roleName, LanguageManager.Language startLanguage)
        {
            if (ctx.Guild.Roles.HasRole(roleName)) { return; }
            var role = await ctx.Guild.CreateRoleAsync(roleName, color: RandomManager.GetRandomColor()).ConfigureAwait(false);
            var category = await ctx.Guild.CreateChannelCategoryAsync(roleName).ConfigureAwait(false);

            var channel = await ctx.Guild.CreateTextChannelAsync("team-chat", category).ConfigureAwait(false);
            await ctx.Guild.CreateVoiceChannelAsync("team-voice", category).ConfigureAwait(false);

            _ = channel.SendTranslatedMessageAsync(ctx.Client,
                "@" + role.Name + "! Das ist euer neuer Teamchat!",
                "@" + role.Name + "! This is your new teamchat!").ConfigureAwait(false);

            await member.GrantRoleAsync(role).ConfigureAwait(false);

            _ = ctx.Channel.SendTranslatedMessageAsync(ctx.Client,
                "Team wurde erfolgreich erstellt!",
                "The team has been created successfully!", startLanguage).ConfigureAwait(false);
        }
    }
}
