const { MessageEmbed } = require('discord.js')
const config = require('../config.json')

module.exports = {
    name: 'announcement',
    aliases: ['ann'],
    async execute(message, args, client) {

        if (message.member.hasPermission('ADMINISTRATOR')) {

            let Rfilter = (reaction, user) => user.id == message.author.id && (reaction.emoji.name == '✅' || reaction.emoji.name == '❌')

            // Collects the message/args
            let Color = args[0]
            let Title = args[1]
            let Description = args[2]
            let URL = args[3]
            let Thumbnail = args[4]
            let Image = args[5]

            let Descriptionbool = true
            let URLbool = true
            let Thumbnailbool = true
            let Imagebool = true


            if (!Color) {
                Color = "BLUE"
            }
            if (!Title) {
                message.reply("Es muss ein Titel angegeben sein.")
            }
            if (Description == ".") {
                Descriptionbool = false
            }
            if (URL == ".") {
                URLbool = false
            }
            if (Thumbnail == ".") {
                Thumbnailbool = false
            }
            if (Image == ".") {
                Imagebool = false
            }

            const channel = message.guild.channels.cache.get(config.ID[1].annid);

            const messageembedann = new MessageEmbed()
                .setColor(Color)
                .setTitle(Title)

            if (Descriptionbool == true) {
                messageembedann.setDescription(Description)
            }
            if (URLbool == true) {
                messageembedann.setURL(URL)
            }
            if (Thumbnailbool == true) {
                messageembedann.setThumbnail(Thumbnail)
            }
            if (Imagebool == true) {
                messageembedann.setImage(Image)
            }

            message.channel.send("Ankündigung senden?")
            message.react('✅').then(r => {
                message.react('❌');

                //wait for answer
                message.awaitReactions(Rfilter, {
                    max: 1,
                    time: 30000,
                    errors: ['time']
                })
                    .then(collected => {
                        if (collected.first().emoji.name == '✅') {
                            channel.send(`<@&${config.ID[3].ping}>`)
                            channel.send(messageembedann)
                            const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                            channellog.send(`Ankündigung versandt von ${message.author}.`)
                        } else if (collected.first().emoji.name == '❌') {
                            message.channel.send("Ankündigung nicht versandt.")
                        }
                    })
            })

        } else {
            message.reply("Du hast leider zu wenig Rechte, um diesen Command zu nutzen.")
        }
    }
}