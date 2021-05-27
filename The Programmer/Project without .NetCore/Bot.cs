using Discord_bot_hackathon_schüler_2021.Commands;
using Discord_bot_hackathon_schüler_2021.Extentions;
using DSharpPlus;
using DSharpPlus.CommandsNext;
using DSharpPlus.Entities;
using DSharpPlus.EventArgs;
using DSharpPlus.Interactivity;
using DSharpPlus.Interactivity.Extensions;
using System;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021
{
    /// <summary>
    /// Bot setup
    /// </summary>
    class Bot
    {
        const string BOT_TOKEN_PLATZHALTER = ""; // <----- Insert Token here

        public DiscordClient Client { get; private set; }
        public CommandsNextExtension Commands { get; private set; }

        /// <summary>
        /// Run the Bot
        /// </summary>
        /// <param name="services"></param>
        public Bot (IServiceProvider services)
        {
            if (services == null)
            {
                return;
            }

            // ----- Client -----
            var config = new DiscordConfiguration
            {
                Token = BOT_TOKEN_PLATZHALTER,
                TokenType = TokenType.Bot,
                AutoReconnect = true,
                MinimumLogLevel = Microsoft.Extensions.Logging.LogLevel.Debug,
                Intents = DiscordIntents.AllUnprivileged
            };

            Client = new DiscordClient(config);

            Client.Ready += OnClientReady;
            Client.GuildMemberAdded += OnGuildMemberAdded;
            Client.GuildAvailable += OnGuildGuildAvailable;

            // ----- Commands -----

            var commandConfig = new CommandsNextConfiguration
            {
                StringPrefixes = new string[] { "!" },
                EnableDms = false,
                EnableMentionPrefix = true,
                DmHelp = true,
                Services = services
            };

            Commands = Client.UseCommandsNext(commandConfig);

            Commands.RegisterCommands<MainCommands>();
            Commands.RegisterCommands<TeamCommands>();
            Commands.RegisterCommands<RegisterCommands>();
            Commands.RegisterCommands<GitCommands>();
            Commands.RegisterCommands<TeamsearchCommands>();
            Commands.RegisterCommands<AnnouncementCommands>();

            // ----- Interactivity -----

            var interactConfic = new InteractivityConfiguration
            {
                PollBehaviour = DSharpPlus.Interactivity.Enums.PollBehaviour.DeleteEmojis,
                Timeout = TimeSpan.FromMinutes(2)
            };

            Client.UseInteractivity(interactConfic);

            Client.ConnectAsync(); // --> Connect
        }

        /// <summary>
        /// If a guild is there
        /// </summary>
        private Task OnGuildGuildAvailable(DiscordClient client, GuildCreateEventArgs e)
        {
            Console.WriteLine("[Bot] --- Bot Guild Available ---");
            _ = Setup.StartSetup(e.Guild);
            return Task.CompletedTask;
        }

        /// <summary>
        /// The Bot started
        /// </summary>
        private Task OnClientReady(DiscordClient client, ReadyEventArgs e)
        {
            Console.WriteLine("[Bot] --- Bot started ---");
            return Task.CompletedTask;
        }

        /// <summary>
        /// Member added to the Server [say welcome]
        /// </summary>
        private async Task OnGuildMemberAdded(DiscordClient client, GuildMemberAddEventArgs e)
        {
            await e.Member.SendMessageAsync("Willkommen auf dem Server " + e.Guild + "!");
        }
    }
}
