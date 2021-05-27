module.exports = {
    // only for testing bots 
    name: 'restart',
    async execute(message, args, client) {
        if (message.member.hasPermission('ADMINISTRATOR')) {
            let sent = await message.channel.send("Restarting: Bin in so 15 Sekunden zurück ;)")
            process.exit(1);
        } else {
            message.reply(`Du hast nicht die passenden Berechtigungen!`)
        }
        
    }
}