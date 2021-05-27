const { MessageEmbed } = require('discord.js');
module.exports = {
    name: 'help',
    execute(message, args, client) {
        //two different Embed messages one for Admin and one for members
        const messageembedhelpadmin = new MessageEmbed()
            .setTitle("Help")
            .setColor('BLUE')
            .setDescription('Hier bist du richtig, wenn du Hilfe brauchst.\n\n**!application oder!a** startet einen Abfrage zur Anmeldung woraufhin eine E-mail zum Organisator gesendet wird.\n\n**!ban** bannt einen Nutzer, allerdings nur wenn du die Berechtigung hast.\n\n**!cat** sendet ein Katzenbild.\n\n**!create** team erstellst du ein neues Team mit Sprach- und Textchanneln, den du dann joinen kannst, dafür einfach den Teamnamen hinter !create team festlegen, auch lassen sich nachträglich Rollen mit **!create role erstellen** (nur für Admins!).\n\nMit **!join team** kannst du einem bereits bestehenden Team joinen, dafür einfach den Teamnamen hinter !join team eingeben.\n\nMit **!kick** kannst du einen Member kicken, wenn du die Berechtigung hast.\n\nMit **!leave** team kannst du dein Team wieder verlassen, hierfür einfach den Teamnamen hinter !leave team eingeben\n\n**!minigame** startet ein Zahlenratespiel.\n\nMit **!play** kannst du nach einem Youtube Titel suchen, indem du den Titel hinter !play eingibst, dieser wird dann im Bot im Channel abgespielt.\n\n**!meme** sendet ein beliebiges Meme in den channel\n\nMit **!code** kannst du in stackoverflow nach unterschiedlichen code Fragen suchen, hierfür einfach deine Sucheingabe hinter !code eingeben.\n\nMit **!announcement** kannst du ein announcement Embed in ein vorher festgelegten channel senden, hierfür die Werte in folgender Reihenfolge tippen (COLOR TITLE(zwingen benötigt) DESCRIPTION URL THUMBNAIL IMAGE), jeder nicht genannte Wert ist mit ... zu ersetzen (nur für Admins!).')
            .addField('!config/!c', '!config setzt mit annid welcomeid ping die Channel id bzw Rolle für die welcome message bzw announcements bzw für gepingte user fest, mit application und opencreate lassen sich dann mit true und false die Anmeldung und die Teamerstellung freischalten. Außerdem lässt sich mit **!config logid** die channeld-id für den bot log ändern', true)
        //message for normal members
        const messageembedhelp = new MessageEmbed()
            .setTitle("Help")
            .setColor('BLUE')
            .setDescription('Hier bist du richtig, wenn du Hilfe brauchst.\n\n**!application oder!a** startet einen Abfrage zur Anmeldung woraufhin eine E-mail zum Organisator gesendet wird.\n\n**!ban** bannt einen Nutzer, allerdings nur wenn du die Berechtigung hast.\n\n**!cat** sendet ein Katzenbild.\n\n**!create** team erstellst du ein neues Team mit Sprach- und Textchanneln, den du dann joinen kannst, dafür einfach den Teamnamen hinter !create team festlegen.\n\nMit **!join team** kannst du einem bereits bestehenden Team joinen, dafür einfach den Teamnamen hinter !join team eingeben.\n\nMit **!kick** kannst du einen Member kicken, wenn du die Berechtigung hast.\n\nMit **!leave** team kannst du dein Team wieder verlassen, hierfür einfach den Teamnamen hinter !leave team eingeben\n\n**!minigame** startet ein Zahlenratespiel.\n\nMit **!play** kannst du nach einem Youtube Titel suchen, indem du den Titel hinter !play eingibst, dieser wird dann im Bot im Channel abgespielt.\n\n**!meme** sendet ein beliebiges Meme in den channel\n\nMit **!code** kannst du in stackoverflow nach unterschiedlichen code Fragen suchen, hierfür einfach deine Sucheingabe hinter !code eingeben.')
        if (message.member.hasPermission('ADMINISTRATOR')) {
            message.author.send(messageembedhelpadmin)
        } else {
            message.author.send(messageembedhelp)
        }
    }
}