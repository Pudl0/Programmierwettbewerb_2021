const fetch = require('node-fetch');
const { htmlToText } = require('html-to-text');
const { MessageEmbed } = require('discord.js')
module.exports = {
    name: 'stackoverflow',
    aliases: [`stackoverflow`, 'code', 'codes'],
    async execute(message, args, client) {

        let searchTitle = args.join(" ");    

        let jsonBlocks;
        try {
            var response = await fetch(`https://api.stackexchange.com/2.2/search?order=desc&sort=relevance&intitle=${searchTitle}&site=stackoverflow&filter=!9sh0bVUzOu93HhEhjRijOJL.IgF8K)14Qooigf_)O7DEi1Fek9QZaMgc72oGUYoh5wm`);
            jsonBlocks = await response.json();
            //console.log(jsonBlocks)
        } catch (e) {
            // handle error
            console.error(e)
        }
        try {

            let answerfound = false
            let acceptedanswer = jsonBlocks.items[0].answers

            //console.log(acceptedanswer)
            acceptedanswer.forEach(obj => {
                Object.entries(obj).forEach(([key, value]) => {
                    if (answerfound == false) {
 
                        if (key = `is_accepted`) {

                            if (value = `true`) {
                                //console.log(jsonBlocks.items[0])
                                //console.log(obj)
                                //console.log(`${key} ${value}`);
                                answerfound = true

                                const EmbedAnswerFound = new MessageEmbed()
                                    .setTitle(htmlToText(jsonBlocks.items[0].title, {
                                        wordwrap: 130
                                      }))
                                    .setColor('RED')
                                    .setURL(obj.link)
                                    .setDescription(`${htmlToText(jsonBlocks.items[0].body, {
                                        wordwrap: 130
                                      })}\n\n** Akzeptierte Antwort:**\n\n${htmlToText(obj.body, {
                                        wordwrap: 130
                                      })}`)
                                      .setThumbnail("https://media-exp1.licdn.com/dms/image/C4E0BAQEooBvMO2kBVg/company-logo_200_200/0/1519880697944?e=2159024400&v=beta&t=cVe1_xseidAuya3zcvZMDT9LkbCjNcsm_R0wYqoJ7xo")
                                message.channel.send(EmbedAnswerFound)

                            }

                        }
                        

                    }
                    else {
                    }
                });
            })

        } catch(e) {
            message.channel.send(`Entweder wurde für "${searchTitle}" nichts gefunden, oder ich hatte einen Error.`)
            console.log(e)
        }


    }
}