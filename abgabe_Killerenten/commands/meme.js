const api = require("imageapi.js")
const Discord = require("discord.js");
const { MessageEmbed } = require('discord.js')

module.exports = {
    name: 'meme',
    async execute(message, args, client){
        let subreddit = 'memes'
        let img = await api(subreddit)
        const Embed = new MessageEmbed()
        .setTitle(`Ein freshes Meme`)
        .setURL(`https://reddit.rom/r/${subreddit}`)
        .setColor('RANDOM')
        .setImage(img)
        message.channel.send(Embed)
    }
}