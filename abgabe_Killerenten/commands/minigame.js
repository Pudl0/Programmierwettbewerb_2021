module.exports = {
  //Main Command to activate
  name: 'minigame',
  aliases: 'm',
  execute(message, args, client) {
    try {

      if (args[0].toLowerCase() == 'zahl') {
        let random = 1
        random = Math.floor(Math.random() * 10 + 1);
        //console.log(`random number is ${random}`)
        let filter = m => m.author.id == message.author.id
        //Waitng for message reply from the same author

        message.channel.send(`Herzlich wilkommen zum Zahlenratespiel gib bitte eine Zahl zwischen 1 und 10 an, die du raten willst, du hast 30s Zeit`).then(() => {
          message.channel.awaitMessages(filter, {
            max: 1,
            time: 30000,
            errors: ['time']
          })
            .then(message => {
              message = message.first()
              if (message.content == random) {
                message.reply(`Herzlichen Glückwunsch, du hast richtig geraten!`)
              } else {
                message.reply(`Leider nicht richtig geraten, versuch's doch gleich noch einmal!`)
              }
            })
            //Error catch and when Time is up
            .catch(collected => { message.channel.send(`Leider zu langsam!`) })
        })
      }
      else if (args[0].toLowerCase() == 'rps') {
        //random string choicer
        var rps = ['Stein', 'Papier', 'Schere'];
        let filter = m => m.author.id == message.author.id
        let temp = Math.floor(Math.random() * 3);
        let random = rps[temp]
        //console.log(`rps is ${rps}`)
        //console.log(`answer is ${random}`)
        //Waiting for message reply from author
        message.channel.send(`Herzlich Wilkommen zum Klassiker Schere, Stein, Papier, gib bitte Schere Stein oder Papier ein je nachdem was du raten willst, du hast 30s Zeit!`).then(() => {
          message.channel.awaitMessages(filter, {
            max: 1,
            time: 30000,
            errors: ['time']
          })
            .then(message => {
              message = message.first()
              let messagec = message.content
              if (messagec[0].toUpperCase() + messagec.slice(1).toLowerCase() == random) {
                message.reply(`Herzlichen Glückwunsch, du hast richtig geraten!`)
              } else {
                message.reply(`Leider nicht richtig geraten, versuch's doch gleich noch einmal!`)
              }
            })
            //Error catch and when Time is up
            .catch(collected => { message.channel.send(`Leider zu langsam!`) })

        })
      }
    } catch {
      message.channel.send(`Du hast ein Argument für diesen command vergessen.`)
    }
  }
}


