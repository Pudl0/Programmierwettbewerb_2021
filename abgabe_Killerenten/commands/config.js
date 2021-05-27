const { TIMEOUT } = require("dns");
const fs = require("fs")
var configWrite = JSON.parse(fs.readFileSync("./config.json"));
var config = require('../config.json');

module.exports = {
    name: 'config',
    aliases: 'c',
    async execute(message, args, client) {
        async function log() {
            try {
                const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                channellog.send(`${message.author} hat ${args[0].toLowerCase()} zu ${args[1]} geändert.`)
            } catch (e) {
                console.log(e)
            }

        }

        try {
            //checking for words in command and correcting letters to lower case can be done with difference ID's or data (always checking for ADMIN)
            if (args[0].toLowerCase() == 'welcomeid') {
                if (message.member.hasPermission('ADMINISTRATOR')) {
                    configWrite.ID[0].welcomeid = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`Welcomeid set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }

            //changing channel-id for announcements
            } else if (args[0].toLowerCase() == 'annid') {
                if (message.member.hasPermission('ADMINISTRATOR')) {
                    configWrite.ID[1].annid = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`Announcement-id set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            //changing email for application
            } else if (args[0].toLowerCase() == 'email') {
                if (message.member.hasPermission('ADMINISTRATOR')) {
                    configWrite["OrganizerEmail"] = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`Email for Application set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            // opening application for members
            } else if (args[0].toLowerCase() == 'application') {
                if (message.member.hasPermission('ADMINISTRATOR')) {

                    configWrite.BOOL[0].OpenApplication = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`OpenApplication now set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            //open team creation for members
            } else if (args[0].toLowerCase() == 'opencreate') {
                if (message.member.hasPermission('ADMINISTRATOR')) {

                    configWrite.BOOL[1].OpenCreate = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`OpenCreate now set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            // editing roles which are pinged at announcement
            } else if (args[0].toLowerCase() == 'ping') {
                if (message.member.hasPermission('ADMINISTRATOR')) {

                    configWrite.ID[3].ping = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`Role ping for announcements now set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            //changing logid for channel for bot log
            } else if (args[0].toLowerCase() == 'logid') {
                if (message.member.hasPermission('ADMINISTRATOR')) {

                    configWrite.ID[2].logid = args[1];
                    const json = JSON.stringify(configWrite);
                    message.channel.send(`Channel-id for bot log now set... Bot is back in 15 sek.`)
                    process.exit(1) = await fs.writeFile("./config.json", json, (err) => {
                    })
                                                           
                } else {
                    message.reply(`Du hast nicht die passenden Berechtigungen!`)
                }
            }
            //error catch
        } catch (e) {
            message.reply(`Du hast ein Argument für diesen command vergessen.`)
            console.log(e)
        }

    }
}



