using DSharpPlus.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Discord_bot_hackathon_schüler_2021
{
    public class RandomManager
    {
        public static DiscordColor GetRandomColor()
        {
            Random rand = new Random();
            var r = rand.NextDouble();
            var g = rand.NextDouble();
            var b = rand.NextDouble();

            return new DiscordColor((float)r, (float)g, (float)b);
        }
    }
}
