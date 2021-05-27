using DSharpPlus.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// An Attachment for translation-dialouges [To grant more reaction-options]
    /// </summary>
    public class LanguageAttachment
    {
        public delegate void Attachment(DiscordMessage message);

        /// <summary>
        /// After Create and after Translate
        /// </summary>
        public Attachment reCreateMessageAttachment;

        /// <summary>
        /// After Translate
        /// </summary>
        public Attachment translateMessageAttachment;

        /// <summary>
        /// After creating message
        /// </summary>
        public Attachment createMessageAttachment;

        public LanguageAttachment(Attachment reCreateMessageAttachment = null, Attachment translateMessageAttachment = null,
            Attachment createMessageAttachment = null)
        {
            this.reCreateMessageAttachment = reCreateMessageAttachment;
            this.translateMessageAttachment = translateMessageAttachment;
            this.createMessageAttachment = createMessageAttachment;
        }
    }
}
