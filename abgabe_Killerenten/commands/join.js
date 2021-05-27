const { MessageEmbed } = require('discord.js')
var config = require('../config.json');

var ExceptRole = config.ExceptRolesJoin.split(" ")
module.exports = {
    name: 'join',
    async execute(message, args, client) {
        //when arg "team"
        if (args[0].toLowerCase() == 'team') {

            //split teamname from command
            let rName = message.content.split("!join team ").join("")
            let ExceptRoleBool = false

            //when teamname isnt there then error message
            //console.log(rName)
            if (rName.toLowerCase() == message.content.toLowerCase()) {
                return message.reply(`Bitte gib einen Namen für das Team an, dem du beitreten willst.`)
            }

            //When theres a Role named rName then it gives an error and goes to catch
            try {
                let addrolename = message.guild.roles.cache.find(role => role.name === rName);

                //returns message if user already in this team
                if (message.member.roles.cache.some(role => role.id === addrolename.id)) {
                    ExceptRole.forEach(function(ExpR){
                        if (ExpR === rName){
                            ExceptRoleBool = true
                            return message.reply(`Diesem Team konnte nicht beigetreten werden, du Schlingel.`)
                        }
                      });
                      if (ExceptRoleBool === false){
                        return message.reply(`Du bist schon Mitglied in diesem Team.`)
                      }
                }

                //sends embed for log
                else {
                    ExceptRole.forEach(function(ExpR){
                        if (ExpR === rName){
                            ExceptRoleBool = true
                            return message.reply(`Diesem Team konnte nicht beigetreten werden, du Schlingel.`)
                        }
                      })
                      if (ExceptRoleBool == false){
                        message.member.roles.add(addrolename.id)
                        const Embed = new MessageEmbed()
                            .setTitle(`Teambetritt`)
                            .setColor(addrolename.color)
                            .setDescription(`${message.author.username} ist dem Team "${rName}" beigetreten.`)
                        message.channel.send(Embed)

                        const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                        channellog.send(`${message.author} ist dem Team ${rName} beigetreten.`)
                      }
                }
            } catch(e) {
                console.log(e)
                return message.reply(`Dieses Team wurde nicht gefunden. Bitte überprüfe die Rechtschreibung.`)
            }   
        }
    }
}