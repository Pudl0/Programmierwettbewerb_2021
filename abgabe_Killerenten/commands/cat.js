const api = require("imageapi.js")
const Discord = require("discord.js");
const { MessageEmbed } = require('discord.js')

module.exports = {
    name: 'cat',
    async execute(message, args, client){
        let subreddits = [
            "cats",
            "blep"
        ]
        let subreddit = subreddits[Math.floor(Math.random()*(subreddits.length))]
        let img = await api(subreddit)
        const Embed = new MessageEmbed()
        .setTitle(`Cats yay`)
        .setURL(`https://reddit.rom/r/${subreddit}`)
        .setColor('RANDOM')
        .setImage(img)
        message.channel.send(Embed)
    }
}