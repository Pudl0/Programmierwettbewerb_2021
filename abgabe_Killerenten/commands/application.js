const { MessageEmbed } = require('discord.js')
const fs = require("fs");
const nodemailer = require("nodemailer");
require('dotenv').config();
var config = require('../config.json');

module.exports = {
    name: 'application',
    aliases: ['anmeldung', 'a'],
    async execute(message, args, client) {
        if (config.BOOL[0].OpenApplication == "true") {

        let filter = m => m.author.id === message.author.id
        let Rfilter = (reaction, user) => user.id == message.author.id && (reaction.emoji.name == '✅' || reaction.emoji.name == '❌')
        let Ffilter = (reaction, user) => user.id == message.author.id && (reaction.emoji.name == '🇪' || reaction.emoji.name == '🇳' || reaction.emoji.name == '🇹' || reaction.emoji.name == '🇸' || reaction.emoji.name == '🇰')

        let anmeldeNameEXPORT = "undefined"

        let successful = false

        let anmeldeEmail = 'undefined@email.net'
        let anmeldeName = 'Max Mustermann'
        let anmeldeTeam = 'Teamless'
        let anmeldeSchule = 'undefinedSchool'
        let anmeldeKlasse = 'undefinedClass'



        const EmbedAnmeldungHelp = new MessageEmbed()
            .setTitle('Anmeldung')
            .setColor('RED')
            .setDescription('Dies wird nun eine Abfrage zur Anmeldung.\nWenn du dies nicht wolltest, tipp einfach "stop".\nFalls doch, tipp "weiter".\nIch frage jetzt erst nach:\nEmail(max.mustermann@gmail.com) Name(Max Mustermann) Team(Killerenten) Schule(UEG Leer) Klasse(10 PLF2)')
        message.channel.send(EmbedAnmeldungHelp)
            .then((embedMessage) => {
                embedMessage.react('✅').then(r => {
                    embedMessage.react('❌');

                    //wait for answer
                    embedMessage.awaitReactions(Rfilter, {
                        max: 1,
                        time: 30000,
                        errors: ['time']
                    })
                        .then(collected => {
                            if (collected.first().emoji.name == '✅') {

                                message.reply(`Was ist deine Email adresse? (max.mustermann@gmail.com)`)

                                //wait for answer
                                message.channel.awaitMessages(filter, {
                                    max: 1,
                                    time: 30000,
                                    errors: ['time']
                                })
                                    .then(messageA => {

                                        messageA = messageA.first()

                                        //answer to var
                                        anmeldeEmail = messageA
                                        //console.log(anmeldeEmail.content)

                                        //next question
                                        message.reply(`Was ist dein Name? (Max Mustermann)`)

                                        //wait for answer
                                        message.channel.awaitMessages(filter, {
                                            max: 1,
                                            time: 30000,
                                            errors: ['time']
                                        })
                                            .then(messageA => {

                                                messageA = messageA.first()

                                                //answer to var
                                                anmeldeName = messageA
                                                //console.log(anmeldeName.content)

                                                //next question
                                                message.reply(`Was ist dein Teamname? (Killerenten) \no. "Kein Team" falls du kein Team hast`)

                                                //wait for answer
                                                message.channel.awaitMessages(filter, {
                                                    max: 1,
                                                    time: 30000,
                                                    errors: ['time']
                                                })
                                                    .then(messageA => {

                                                        messageA = messageA.first()

                                                        //answer to var
                                                        anmeldeTeam = messageA
                                                        //console.log(anmeldeTeam.content)

                                                        //next question
                                                        message.reply(`Was ist dein Schule? (UEG Leer)`)

                                                        //wait for answer
                                                        message.channel.awaitMessages(filter, {
                                                            max: 1,
                                                            time: 30000,
                                                            errors: ['time']
                                                        })
                                                            .then(messageA => {

                                                                messageA = messageA.first()

                                                                //answer to var
                                                                anmeldeSchule = messageA
                                                                //console.log(anmeldeSchule.content)

                                                                //next question
                                                                message.reply(`Was ist deine Klasse? (10 PLF2)`)

                                                                //wait for answer
                                                                message.channel.awaitMessages(filter, {
                                                                    max: 1,
                                                                    time: 30000,
                                                                    errors: ['time']
                                                                })
                                                                    .then(messageA => {

                                                                        messageA = messageA.first()

                                                                        //answer to var
                                                                        anmeldeKlasse = messageA
                                                                        //console.log(anmeldeKlasse.content)

                                                                        async function saveJSON() {
                                                                            //saves data in .txt
                                                                            fs.writeFile(`./anmeldungen/${anmeldeName}.json`, `{\n "Email" : "${anmeldeEmail.content}",\n "Name" : "${anmeldeName.content}",\n "Team" : "${anmeldeTeam.content}",\n "Schule" : "${anmeldeSchule.content}",\n "Klasse" : "${anmeldeKlasse.content}"\n}`, err => {
                                                                                if (err) {
                                                                                    console.error(err)
                                                                                    return
                                                                                }
                                                                            })
                                                                            const channellog = message.guild.channels.cache.get(config.ID[2].logid);
                                                                            channellog.send(`${message.author} hat sich angemeldet. Die Email wurde an ${anmeldeEmail.content} gesendet.`)
                                                                        }

                                                                        async function sendEmail() {

                                                                            if (successful = true) {

                                                                                // create reusable transporter object using the default SMTP transport
                                                                                let transporter = nodemailer.createTransport({
                                                                                    host: "smtp.gmail.com",
                                                                                    port: 465,
                                                                                    secure: true, // true for 465, false for other ports
                                                                                    auth: {
                                                                                        user: process.env.Gmail_adress, //hackathonschuelerdiscordbot@gmail.com
                                                                                        pass: process.env.Gmail_pass, //7+jQ[:&2[*
                                                                                    },
                                                                                });

                                                                                //transporter.verify(function(error, success) {
                                                                                //if (error) {
                                                                                //console.log(error);
                                                                                //} else {
                                                                                //console.log('Server is ready to take our messages');
                                                                                //}
                                                                                //});

                                                                                let Email = await transporter.sendMail({
                                                                                    from: `"${anmeldeName}" <${anmeldeEmail}>`, // sender address
                                                                                    to: config.OrganizerEmail, // list of receivers
                                                                                    subject: "Neue Anmeldung", // Subject line
                                                                                    text: `Sehr geehrte Damen und Herren,\nhiermit möchte ich mich mich gerne zum Schüler-Hackathon anmelden. Ich gehe in die Klasse ${anmeldeKlasse.content} des ${anmeldeSchule.content} und würde gerne im Team ${anmeldeTeam.content} antreten. Erreichen können Sie mich unter ${anmeldeEmail.content}.\nSchöne Grüße\n${anmeldeName.content}`, // plain text body
                                                                                });
                                                                            }
                                                                        }

                                                                        async function falseData() {

                                                                            const EmbedAnmeldungKorrektur = new MessageEmbed()
                                                                                .setTitle('Korrektur Anmeldung')
                                                                                .setColor('RED')
                                                                                .setDescription(`Welche Werte willst du ändern?`)
                                                                                .setFooter(`Reagier einfach mit dem entsprechenden Emoji.`)
                                                                            message.channel.send(EmbedAnmeldungKorrektur)
                                                                                .then((embedMessage) => {
                                                                                    embedMessage.react('🇪').then(r => {
                                                                                        embedMessage.react('🇳');
                                                                                        embedMessage.react('🇹');
                                                                                        embedMessage.react('🇸');
                                                                                        embedMessage.react('🇰');

                                                                                        embedMessage.awaitReactions(Ffilter, {
                                                                                            max: 1,
                                                                                            time: 30000,
                                                                                            errors: ['time']
                                                                                        })
                                                                                            .then(collected => {

                                                                                                //if weiter
                                                                                                if (collected.first().emoji.name == '🇪') {

                                                                                                    message.reply(`Was ist deine Email adresse? (max.mustermann@gmail.com)`)

                                                                                                    //wait for answer
                                                                                                    message.channel.awaitMessages(filter, {
                                                                                                        max: 1,
                                                                                                        time: 30000,
                                                                                                        errors: ['time']
                                                                                                    })
                                                                                                        .then(messageA => {

                                                                                                            messageA = messageA.first()

                                                                                                            //answer to var
                                                                                                            anmeldeEmail = messageA
                                                                                                            //console.log(anmeldeTeam.content)

                                                                                                            CheckA();

                                                                                                        })

                                                                                                } else if (collected.first().emoji.name == '🇳') {

                                                                                                    message.reply(`Was ist dein Name? (Max Mustermann)`)

                                                                                                    //wait for answer
                                                                                                    message.channel.awaitMessages(filter, {
                                                                                                        max: 1,
                                                                                                        time: 30000,
                                                                                                        errors: ['time']
                                                                                                    })
                                                                                                        .then(messageA => {

                                                                                                            messageA = messageA.first()

                                                                                                            //answer to var
                                                                                                            anmeldeName = messageA
                                                                                                            //console.log(anmeldeTeam.content)
                                                                                                            
                                                                                                            CheckA();

                                                                                                        })

                                                                                                } else if (collected.first().emoji.name == '🇹') {

                                                                                                    message.reply(`Was ist dein Teamname? (Killerenten) \no. "Kein Team" falls du kein Team hast`)

                                                                                                    //wait for answer
                                                                                                    message.channel.awaitMessages(filter, {
                                                                                                        max: 1,
                                                                                                        time: 30000,
                                                                                                        errors: ['time']
                                                                                                    })
                                                                                                        .then(messageA => {

                                                                                                            messageA = messageA.first()

                                                                                                            //answer to var
                                                                                                            anmeldeTeam = messageA
                                                                                                            //console.log(anmeldeTeam.content)
                                                                                                            
                                                                                                            CheckA();

                                                                                                        })

                                                                                                } else if (collected.first().emoji.name == '🇸') {

                                                                                                    message.reply(`Was ist dein Schule? (UEG Leer)`)

                                                                                                    //wait for answer
                                                                                                    message.channel.awaitMessages(filter, {
                                                                                                        max: 1,
                                                                                                        time: 30000,
                                                                                                        errors: ['time']
                                                                                                    })
                                                                                                        .then(messageA => {

                                                                                                            messageA = messageA.first()

                                                                                                            //answer to var
                                                                                                            anmeldeSchule = messageA
                                                                                                            //console.log(anmeldeTeam.content)

                                                                                                            CheckA();

                                                                                                        })

                                                                                                } else if (collected.first().emoji.name == '🇰') {

                                                                                                    message.reply(`Was ist deine Klasse? (10 PLF2)`)

                                                                                                    //wait for answer
                                                                                                    message.channel.awaitMessages(filter, {
                                                                                                        max: 1,
                                                                                                        time: 30000,
                                                                                                        errors: ['time']
                                                                                                    })
                                                                                                        .then(messageA => {

                                                                                                            messageA = messageA.first()

                                                                                                            //answer to var
                                                                                                            anmeldeKlasse = messageA
                                                                                                            //console.log(anmeldeTeam.content)

                                                                                                            CheckA();

                                                                                                        })

                                                                                                } else {
                                                                                                    message.channel.send(`Anmeldung abgebrochen. (Error)`)
                                                                                                }
                                                                                            })
                                                                                    })
                                                                                })
                                                                        }

                                                                        async function CheckA() {

                                                                            const EmbedAnmeldungFertig = new MessageEmbed()
                                                                                .setTitle('Anmeldung')
                                                                                .setColor('RED')
                                                                                .setDescription(`Ist dies richtig?\nEmail: ${anmeldeEmail.content}\nName: ${anmeldeName.content}\nTeam: ${anmeldeTeam.content}\nSchule: ${anmeldeSchule.content}\nKlasse: ${anmeldeKlasse.content}`)
                                                                                .setFooter(`Falls dies falsch ist tipp "falsch" und falls richtig, "weiter".`)
                                                                            message.channel.send(EmbedAnmeldungFertig)
                                                                                .then((embedMessage) => {
                                                                                    embedMessage.react('✅').then(r => {
                                                                                        embedMessage.react('❌');

                                                                                        //wait for answer
                                                                                        embedMessage.awaitReactions(Rfilter, {
                                                                                            max: 1,
                                                                                            time: 30000,
                                                                                            errors: ['time']
                                                                                        })
                                                                                            .then(collected => {

                                                                                                //if weiter
                                                                                                if (collected.first().emoji.name == '✅') {

                                                                                                    saveJSON();

                                                                                                    message.reply(`Anmeldung abgeschlossen.`)

                                                                                                    successful = true

                                                                                                    sendEmail();

                                                                                                } else if (collected.first().emoji.name == '❌') {

                                                                                                    falseData();

                                                                                                } else {
                                                                                                    message.channel.send(`Anmeldung abgebrochen. (Error)`)
                                                                                                }
                                                                                            })

                                                                                    })
                                                                                })
                                                                        }
                                                                        CheckA();
                                                                    })
                                                                    .catch(collected => {
                                                                        message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                                                                    })
                                                            })
                                                            .catch(collected => {
                                                                message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                                                            })
                                                    })
                                                    .catch(collected => {
                                                        message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                                                    })
                                            })
                                            .catch(collected => {
                                                message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                                            })
                                    })
                                    .catch(collected => {
                                        message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                                    })

                                //if stop or false input
                            } else if (collected.first().emoji.name == '❌') {
                                message.channel.send(`Anmeldung abgebrochen.`)
                            } else {
                                message.channel.send(`Anmeldung abgebrochen. (Error)`)
                            }
                        })
                        .catch(collected => {
                            message.channel.send('Zeitüberschreitung. Anmeldung abgebrochen.');
                        })

                })
            })
        } else if (config.BOOL[0].OpenApplication == "false") {
            message.reply("es werden im Moment keine Anmeldungen angenommen.\nFordere doch bei einem Admin an, die Anmeldungen zu öffnen.")
        } else {
            message.channel.send("Es gab leider einene Fehler in der Config.json.")
        }
    }
}