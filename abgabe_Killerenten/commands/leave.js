var config = require('../config.json');
const { MessageEmbed } = require('discord.js')

module.exports = {
    name: 'leave',
    async execute(message, args, client) {
        //when arg "team"
        if (args[0].toLowerCase() == 'team') {

            //split teamname from command
            let rName = message.content.split(`!leave team `).join("")

            //when teamname isnt there then error message
            if (rName.toLowerCase() == message.content.toLowerCase()) {
                return message.reply(`Bitte gib einen Namen für das Team an, das du verlassen willst.`)
            }

            //When theres a Role named rName then it gives an error and goes to catch
            try {
                let removerolename = message.guild.roles.cache.find(role => role.name === rName);

                //returns message if user isnt in this team
                if (!message.member.roles.cache.some(role => role.id === removerolename.id)) {
                    return message.reply(`Du bist kein Mitglied in diesem Team.`)
                }

                //sends embed for log
                else {
                    message.member.roles.remove(removerolename.id)
                    const Embed = new MessageEmbed()
                        .setTitle(`Teamaustritt`)
                        .setColor(removerolename.color)
                        .setDescription(`${message.author.username} hat das Team "${rName}" verlassen.`)
                    message.channel.send(Embed)
                    const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                    channellog.send(`${message.author} hat das Team ${rName} verlassen.`)
                }
            }
            catch(e) {
                console.log(e)
                return message.reply(`Dieses Team wurde nicht gefunden. Bitte überprüfe die Rechtschreibung`)
            }
        }
    }
}