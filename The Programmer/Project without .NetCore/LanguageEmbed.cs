using DSharpPlus.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021
{
    /// <summary>
    /// This is used to make a translated embed-message
    /// </summary>
    public class LanguageEmbed
    {
        public DiscordEmbed germanEmbed;
        public DiscordEmbed englishEmbed;

        public LanguageEmbed(string ger_title, string ger_description, string eng_title, string eng_description, DiscordColor color)
        {
            germanEmbed = new DiscordEmbedBuilder
            {
                Title = ger_title,
                Description = ger_description,
                Color = color
            };

            englishEmbed = new DiscordEmbedBuilder
            {
                Title = eng_title,
                Description = eng_description,
                Color = color
            };
        }

        public LanguageEmbed()
        {

        }
    }
}
