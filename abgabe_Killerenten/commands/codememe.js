const api = require("imageapi.js")
const Discord = require("discord.js");
const { MessageEmbed } = require('discord.js')

module.exports = {
    name: 'codememe',
    aliases: ["cmeme", "pmeme"],
    async execute(message, args, client){
        let subreddits = [
            "ProgrammerHumor"
        ]
        let subreddit = subreddits[Math.floor(Math.random()*(subreddits.length))]
        let img = await api(subreddit)
        const Embed = new MessageEmbed()
        .setTitle(`Some Programming meme`)
        .setURL(`https://reddit.rom/r/${subreddit}`)
        .setColor('RANDOM')
        .setImage(img)
        message.channel.send(Embed)
    }
}