const api = require("imageapi.js")
const Discord = require("discord.js");
const { MessageEmbed } = require('discord.js')

module.exports = {
    name: 'codememe',
    aliases: ["cmeme", "pmeme"],
    async execute(message, args, client){
        let subreddit = "ProgrammerHumor"
        let img = await api(subreddit)
        const Embed = new MessageEmbed()
        .setTitle(`Some Programming meme`)
        .setURL(`https://reddit.com/r/${subreddit}`)
        .setColor('RANDOM')
        .setImage(img)
        message.channel.send(Embed)
    }
}