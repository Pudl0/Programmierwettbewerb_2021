const { MessageEmbed } = require('discord.js')
var config = require('../config.json');

module.exports = {
    name: 'create',
    aliases: 'cr',
    async execute(message, args, client) {
        if (config.BOOL[1].OpenCreate == "true") {
            //when arg "team"
            if (args[0].toLowerCase() == 'team') {

                //split rolename from command
                let rName = message.content.split(`!create team `).join("")
                let Rolecreated = false

                //when Rolename isnt there then error message
                if (!rName) {
                    return message.reply(`Du hast keinen Namen für dein Team festgelegt.`)
                }

                //When theres a Role named rName then it gives an error and goes to catch
                try {
                    let findRole = message.guild.roles.cache.find(role => role.name === rName);
                    return message.reply(`Dieses Team "${findRole.name}" existiert schon. Versuch doch, diesem Team beizutreten`)
                }

                catch {
                    //genererates a random color
                    let randomNumber = Math.floor(Math.random() * 16777215);
                    let everyoneRole = message.guild.roles.cache.find(role => role.name === '@everyone');

                    //creates the category
                    message.guild.channels.create(rName, {
                        type: 'category',
                        permissionOverwrites: [
                            {
                                id: everyoneRole.id,
                                deny: ['VIEW_CHANNEL'],
                            },
                        ],
                    })
                        .catch(console.error);

                    //creates the new role with name and random color
                    let rNew = await message.guild.roles.create({
                        data: {
                            name: rName,
                            color: randomNumber,
                            hoist: true
                        }
                    })

                    //embed for log message
                    const Embed = new MessageEmbed()
                        .setTitle(`Neues Team`)
                        .setColor(randomNumber)
                        .setDescription(`${message.author.username} hat ein neues Team "${rName}" erstellt.\n${message.author.username} ist diesem Team beigetreten.`)
                    message.channel.send(Embed)

                    //gives user that issued command the role
                    let addrolename = message.guild.roles.cache.find(role => role.name === rName);
                    message.member.roles.add(addrolename.id)

                    const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                    channellog.send(`${message.author} hat Team ${rName} erstellt.`)

                    Rolecreated = true
                }

                //creates the remaining channels
                if (Rolecreated = true) {
                    let addrolename = message.guild.roles.cache.find(role => role.name === rName);
                    let everyoneRole = message.guild.roles.cache.find(role => role.name === '@everyone');
                    let categoryName = message.guild.channels.cache.find(cat => cat.name == rName && cat.type == 'category');


                    //console.log(categoryName)
                    message.guild.channels.create('team-chat', {
                        type: 'text',
                        parent: categoryName.id,
                        permissionOverwrites: [
                            {
                                id: addrolename.id,
                                allow: ['VIEW_CHANNEL'],
                            },
                            {
                                id: everyoneRole.id,
                                deny: ['VIEW_CHANNEL'],
                            },

                        ],
                    })
                        .catch(console.error);
                    message.guild.channels.create('team-voice', {
                        type: 'voice',
                        parent: categoryName.id,
                        permissionOverwrites: [
                            {
                                id: addrolename.id,
                                allow: ['VIEW_CHANNEL'],
                            },
                            {
                                id: everyoneRole.id,
                                deny: ['VIEW_CHANNEL'],
                            },

                        ],
                    })
                        .catch(console.error);
                }
            }
            if (args[0].toLowerCase() == 'role') {

                let rName = message.content.split(`!create role `).join("")

                if (!rName) {
                    return message.reply(`Du hast keinen Namen für diese Rolle festgelegt.`)
                }

                try {
                    let findRole = message.guild.roles.cache.find(role => role.name === rName);
                    return message.reply(`Dieses Team "${findRole.name}" existiert schon. Versuch doch, diesem Team beizutreten`)
                }

                catch {
                    //genererates a random color
                    let randomNumber = Math.floor(Math.random() * 16777215);

                    //creates the new role with name and random color
                    let rNew = await message.guild.roles.create({
                        data: {
                            name: rName,
                            color: randomNumber,
                            hoist: true
                        }
                    })

                    //embed for log message
                    const Embed = new MessageEmbed()
                        .setTitle(`Neues Team`)
                        .setColor(randomNumber)
                        .setDescription(`${message.author.username} hat eine neue Rolle "${rName}" erstellt.\n${message.author.username} wurde diese Rolle zugewiesen.`)
                    message.channel.send(Embed)

                    //gives user that issued command the role
                    let addrolename = message.guild.roles.cache.find(role => role.name === rName);
                    message.member.roles.add(addrolename.id)

                    const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                    channellog.send(`${message.author} hat Rolle ${rName} erstellt.`)
                }

            }
        } else {
            message.reply(`Momentan können keine Teams erstellt werden, bitte doch einen Admin die Teamerstellung freizuschalten.`)
        }
    }
}