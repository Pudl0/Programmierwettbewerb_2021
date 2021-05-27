
module.exports = (client) => {
        //when function activated by member join sending message and getting channelid from config.json
        client.on('guildMemberAdd', (member) => {
                const config = require('../../config.json');
                
                //console.log (`${channelid.welcomeid}`);
                const message = `Hallo <@${member.id}> schön, dass du da bist. Viel Spaß!`;
                const channel = member.guild.channels.cache.get(config.ID[0].welcomeid);
                channel.send(message);
        });

}