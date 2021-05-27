var config = require('../config.json');

module.exports = {
    name: 'kick',
    execute(message, args, client) {

        if (message.member.hasPermission('KICK_MEMBERS')) {
            let member = message.mentions.members.first();
            let reason = args.slice(1).join(" ");
            if (!member) return message.reply("Bitte pinge einen User!");
            if (!member.kickable) return message.reply("Du kannst den User nicht kicken!");

            member.kick();
            message.reply("User wurde gekickt!" + " **Grund: " + reason + "**");
            const channellog = message.guild.channels.cache.get(config.ID[2].logid);
            channellog.send(`User ${member} wurde von ${message.author} vom Server gekickt.`)
        } else {
            message.reply('Du hast nicht die passenden Berechtigungen!');
        }


    }
}