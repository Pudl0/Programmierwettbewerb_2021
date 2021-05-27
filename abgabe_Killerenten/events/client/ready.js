module.exports = (client) => {
  //when client (bot) online sending console message for check and setting activity
  try {
    client.on('ready', () => {
      console.log(`Now Logged in as ${client.user.username} with ${client.users.cache.size} users!`);
      
      client.user.setActivity('euren Befehlen', { type: 'LISTENING' })
    })
  }
  catch (e) {
  }
}