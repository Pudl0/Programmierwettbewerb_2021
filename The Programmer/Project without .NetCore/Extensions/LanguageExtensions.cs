using Discord_bot_hackathon_schüler_2021.Commands;
using DSharpPlus;
using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using DSharpPlus.Interactivity.Extensions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Extentions
{
    /// <summary>
    /// Send translated messages
    /// </summary>
    public static class LanguageExtensions
    {
        // --- message ---
        /// <summary>
        /// Send a translated message with a text
        ///
        /// Note: It´s not possible in most cases to access the finished state when you wait for it.
        ///       When you want to add reactions using the message, which will be created
        ///       use the attachment. 
        /// </summary>
        /// <param name="channel">The channel in which the message should send to</param>
        /// <param name="german">German text</param>
        /// <param name="english">English text</param>
        /// <param name="startLanguage">The language, which showed by default</param>
        public static async Task SendTranslatedMessageAsync
            (this DiscordChannel channel, DiscordClient client, string german, string english, LanguageManager.Language? startLanguage = LanguageManager.Language.German)
        {
            string activeMessage;

            LanguageManager.Language nextLanguage;

            if (startLanguage == LanguageManager.Language.German)
            {
                activeMessage = german;
                nextLanguage = LanguageManager.Language.English;
            }
            else
            {
                activeMessage = english;
                nextLanguage = LanguageManager.Language.German;
            }

            var message = await channel.SendMessageAsync(activeMessage);

            await message.AddLanguageReaktionMessageAsync(client, german, english, nextLanguage);
        }

        // --- embed ---
        /// <summary>
        /// Send a translated message with an embed.
        /// 
        /// Note: It´s not possible in most cases to access the finished state when you wait for it.
        ///       When you want to add reactions using the message, which will be created
        ///       use the attachment. 
        /// </summary>
        /// <param name="channel">The channel in which the message should send to</param>
        /// <param name="languageEmbed">The language embed for german and english translation</param>
        /// <param name="startLanguage">The language, which showed by default</param>
        /// <param name="attachment">An attachment is used for messages with reaction-options</param>
        /// <returns></returns>
        public static async Task SendTranslatedMessageAsync
            (this DiscordChannel channel, CommandContext ctx, LanguageEmbed languageEmbed,
            LanguageManager.Language? startLanguage = LanguageManager.Language.German,
            LanguageAttachment attachment = null)
        {
            DiscordEmbed activeMessage;

            LanguageManager.Language nextLanguage;

            if (startLanguage == LanguageManager.Language.German)
            {
                activeMessage = languageEmbed.germanEmbed;
                nextLanguage = LanguageManager.Language.English;
            }
            else
            {
                activeMessage = languageEmbed.englishEmbed;
                nextLanguage = LanguageManager.Language.German;
            }


            var message = await channel.SendMessageAsync(activeMessage);

            if (attachment != null)
            {
                attachment.createMessageAttachment?.Invoke(message);
                attachment.reCreateMessageAttachment?.Invoke(message);
            }

            await message.AddLanguageReaktionMessageAsync(ctx, languageEmbed, nextLanguage, attachment);
        }

        // --- message ---
        private static async Task AddLanguageReaktionMessageAsync
            (this DiscordMessage message, DiscordClient client, string german, string english, LanguageManager.Language nextLanguage)
        {
            string translatedMessage;

            LanguageManager.Language nextnextLanguage;

            if (nextLanguage == LanguageManager.Language.German)
            {
                translatedMessage = german;
                nextnextLanguage = LanguageManager.Language.English;
            }
            else
            {
                translatedMessage = english;
                nextnextLanguage = LanguageManager.Language.German;
            }


            var ger_flag = DiscordEmoji.FromName(client, ":flag_de:");
            var eng_flag = DiscordEmoji.FromName(client, ":flag_gb:");

            await message.CreateReactionAsync(nextLanguage == LanguageManager.Language.English ? eng_flag : ger_flag).ConfigureAwait(false);

            var interact = client.GetInteractivity();

            var answer = await interact.WaitForReactionAsync(x =>
            ((nextLanguage == LanguageManager.Language.English) ? x.Emoji == eng_flag : x.Emoji == ger_flag)
            && message == x.Message && !x.User.IsBot, TimeSpan.FromHours(1)).ConfigureAwait(false);

            await message.DeleteReactionsEmojiAsync(nextLanguage == LanguageManager.Language.English ? eng_flag : ger_flag);

            if (answer.TimedOut)
            {
                return;
            }

            await message.ModifyAsync(translatedMessage);
            await message.AddLanguageReaktionMessageAsync(client, german, english, nextnextLanguage);
        }

        // --- embed ---
        private static async Task AddLanguageReaktionMessageAsync
            (this DiscordMessage message, CommandContext ctx, LanguageEmbed languageEmbed, LanguageManager.Language nextLanguage,
            LanguageAttachment attachmant = null)
        {
            DiscordEmbed translatedEmbed;

            LanguageManager.Language nextnextLanguage;

            if (nextLanguage == LanguageManager.Language.German)
            {
                translatedEmbed = languageEmbed.germanEmbed;
                nextnextLanguage = LanguageManager.Language.English;
            }
            else
            {
                translatedEmbed = languageEmbed.englishEmbed;
                nextnextLanguage = LanguageManager.Language.German;
            }


            var ger_flag = DiscordEmoji.FromName(ctx.Client, ":flag_de:");
            var eng_flag = DiscordEmoji.FromName(ctx.Client, ":flag_gb:");

            if (attachmant != null)
            {
                attachmant.reCreateMessageAttachment?.Invoke(message);
            }

            await message.CreateReactionAsync(nextLanguage == LanguageManager.Language.English ? eng_flag : ger_flag).ConfigureAwait(false);

            var interact = ctx.Client.GetInteractivity();

            var answer = await interact.WaitForReactionAsync(x => 
            ((nextLanguage == LanguageManager.Language.English) ? x.Emoji == eng_flag : x.Emoji == ger_flag)
            && message == x.Message && !x.User.IsBot, TimeSpan.FromMinutes(1)).ConfigureAwait(false);

            await message.DeleteReactionsEmojiAsync(nextLanguage == LanguageManager.Language.English ? eng_flag : ger_flag);

            if (attachmant != null)
            {
                attachmant.translateMessageAttachment?.Invoke(message);
            }

            if (answer.TimedOut)
            {
                return;
            }

            await message.ModifyAsync(translatedEmbed);
            await message.AddLanguageReaktionMessageAsync(ctx, languageEmbed, nextnextLanguage);
        }
    }
}
