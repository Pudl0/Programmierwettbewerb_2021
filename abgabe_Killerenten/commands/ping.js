module.exports = {
    // only for testing bots 
    name: 'ping',
    execute(message, args, client){
        message.channel.send('pong');
    }
}