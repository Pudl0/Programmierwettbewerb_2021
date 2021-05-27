using DSharpPlus;
using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021.Commands
{
    /// <summary>
    /// General Setup and Temporary Settings
    /// </summary>
    public class Setup
    {

        // References (not used now)
        public static ulong AdminChannelID { get; private set; }
        public static ulong AllgemeinChannelID { get; private set; }
        public static ulong WillkommenChannelID { get; private set; }
        public static ulong TeamsucheChannelID { get; private set; }
        public static ulong RezeptionChannelID { get; private set; }
        public static ulong AdminRoleID { get; private set; }

        // Temp Settings
        public static bool presentationMode = false;


        public static async Task StartSetup(DiscordClient client)
        {
            Console.WriteLine("[Setup] --- Start Setup (Guildcount: " + client.Guilds.Count + ")---");
            foreach (DiscordGuild guild in client.Guilds.Values)
            {
                if (guild.Members.Count > 0)
                {
                    Console.WriteLine("[Setup] --- Setup Guild : '" + guild.Name + "' ---");
                    await TryCreateBasics(guild);
                }
            }
        }

        /// <summary>
        /// To start a general setup
        /// </summary>
        public static async Task StartSetup(DiscordGuild guild)
        {
            if (guild.IsUnavailable || guild.Channels == null) { return; }
            Console.WriteLine("[Setup] --- Start Setup (With CommandContext)---");

            // ----- Check if there is anyone -----
            if (guild.Members.Count > 0)
            {
                Console.WriteLine("[Setup] --- Setup Guild : '" + guild.Name + "' ---");
                await TryCreateBasics(guild);
            }
            // await guild.Owner.SendMessageAsync("Setup Compledet! (Guild: " + guild.Name + ")"); // ----- [If someone wants this] -----
        }

        /// <summary>
        /// Next step of setup
        /// </summary>
        public static async Task TryCreateBasics(DiscordGuild guild)
        {
            AdminChannelID = await TryCreateChannel(guild, "admin", "Textkanäle");
            AllgemeinChannelID = await TryCreateChannel(guild, "allgemein", "Textkanäle");
            WillkommenChannelID = await TryCreateChannel(guild, "willkommen", "Textkanäle");
            TeamsucheChannelID = await TryCreateChannel(guild, "teamsuche", "Textkanäle");
            RezeptionChannelID = await TryCreateChannel(guild, "rezeption", "Textkanäle");

            AdminRoleID = await TryCreateRole(guild, "Admin", Permissions.Administrator, DiscordColor.Yellow);
        }

        /// <summary>
        /// Tries to create a channel of a parent category
        /// </summary>
        public static async Task<ulong> TryCreateChannel(DiscordGuild guild, string name, string parent)
        {
            var channel = guild.Channels.Values.FirstOrDefault(x => x.Name == name && !x.IsCategory && x.Parent.Name == parent);
            if (channel == null)
            {
                Console.WriteLine("[Setup] --- Setuping " + name + "! (Guild : '" + guild.Name + "') ---");
                var category = guild.Channels.Values.FirstOrDefault(x => x.Name == parent && x.IsCategory);
                if (category == null)
                {
                    Console.WriteLine("[Setup] --- Setuping Category " + parent + "! (Guild : '" + guild.Name + "') ---");
                    category = await guild.CreateChannelCategoryAsync(parent);
                }
                channel = await guild.CreateTextChannelAsync(name, category);
            }
            return channel.Id;
        }

        /// <summary>
        /// Tries to Create a role if it does not exist
        /// </summary>
        public static async Task<ulong> TryCreateRole(DiscordGuild guild, string name, Permissions permissions, DiscordColor color)
        {
            var role = guild.Roles.Values.FirstOrDefault(x => x.Name == name);
            if (role == null)
            {
                Console.WriteLine("[Setup] --- Setuping Role" + name + "! (Guild : '" + guild.Name + "') ---");
                role = await guild.CreateRoleAsync(name, permissions, color);
            }
            return role.Id;
        }
    }
}
