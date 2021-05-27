using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{

    /// <summary>
    /// To Check if the command can be used or not and respond to that
    /// </summary>
    public static class Requires
    {
        /// <summary>
        /// To check if a Member is an Admin or Owner
        /// </summary>
        /// <returns>Does the Member is an Admin or Owner</returns>
        public static bool AdminOrOwner(DiscordMember member)
        {
            bool ret = member.Roles.FirstOrDefault(x => x.Name == "Admin") != null;

            if (!Setup.presentationMode)
                ret = ret || member.IsOwner;


            if (!ret)
                member.SendMessageAsync("Du hast nicht die Rechte dafür!" +
                    ((Setup.presentationMode && member.IsOwner) ? " Schalte den Präsentationsmodus aus oder gebe dir Admin!" : "") +
                    "\n*You dont have the permissions for it!" +
                    ((Setup.presentationMode && member.IsOwner) ? " Turn off the presentationmode or get the admin role!*" : "*"));
            return ret;
        }

        /// <summary>
        /// To check if the Message [ctx] comes from channel
        /// </summary>
        /// <returns>Does it come from there?</returns>
        public static bool Channel(CommandContext ctx, string channel)
        {
            bool ret = ctx.Channel.Name == channel;
            if (!ret)
                ctx.Member.SendMessageAsync("Falscher Channel! Probiere es doch mal mit '" + channel + "'!" +
                    "\n*Wrong channel! Try out '" + channel + "'!*");
            return ret;
        }
    }
}
