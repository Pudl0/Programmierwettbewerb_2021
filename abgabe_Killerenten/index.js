const Discord = require("discord.js");
var config = require('./config.json');
require('dotenv').config();
const fs = require('fs');
const client = new Discord.Client();
//Setting client requirements for welcome.js, so client is not isolated in a function
require("./events/client/welcome.js")(client)
require("./events/client/ready.js")(client)


client.commands = new Discord.Collection();
client.events = new Discord.Collection();

['command_handler', 'event_handler'].forEach(handler =>{
    require(`./handlers/${handler}`)(client, Discord);
});


client.login(process.env.KillerentenBOT_TOKEN);