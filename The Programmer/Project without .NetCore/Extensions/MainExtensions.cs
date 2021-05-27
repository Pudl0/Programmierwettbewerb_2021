using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using System.Collections.Generic;
using System.Linq;

namespace Discord_bot_hackathon_schüler_2021.Extentions
{
    public static class MainExtensions
    {
        public static DiscordMember GetMember(this DiscordUser user, DiscordGuild guild)
        {
            return guild.Members.FirstOrDefault(x => x.Value.Id == user.Id).Value;
        }

        public static DiscordRole GetRoleByName(string name, CommandContext ctx)
        {
            return ctx.Guild.Roles.FirstOrDefault(x => x.Value.Name == name).Value;
        }

        public static string ToChainString(this string[] @params, string spacing = " ")
        {
            string ret = "";
            for (int i = 0; i < @params.Length; i++)
            {
                ret += @params[i];
                if (i < @params.Length - 1)
                {
                    ret += spacing;
                }
            }
            return ret;
        }

        public static bool HasRole(this DiscordMember member, string roleName)
        {
            return member.Roles.FirstOrDefault(x => x.Name == roleName) != null;
        }

        public static bool HasRole(this IReadOnlyDictionary<ulong, DiscordRole> roles, string roleName)
        {
            return roles.FirstOrDefault(x => x.Value.Name == roleName).Value != null;
        }
    }
}
