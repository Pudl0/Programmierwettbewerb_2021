var config = require('../config.json');

module.exports = {
    name: 'ban',
    execute(message, args, client) {
    
        if(message.member.hasPermission('BAN_MEMBERS')) {
            let member = message.mentions.members.first();
            let reason = args.slice(1).join(" ");
                if(!member) return message.reply("Bitte ping einen User!");
                    if(!member.kickable) return message.reply("Du kannst den User nicht bannen!");
                
                member.ban();
                message.reply("User wurde gebannt!" + " **Grund: " + reason + "**");
                const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                channellog.send(`User ${member} wurde von ${message.author} vom Server gebannt.`)
        }else {
            message.reply('Du hast nicht die passenden Berechtigungen!');
        }


    }
}