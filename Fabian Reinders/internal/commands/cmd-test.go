package commands

type CommandTest struct{}

func (commandTest *CommandTest) Invokes() []string {
	return []string{"test"}
}

func (commandTest *CommandTest) AdminPermissionsNeeded() bool {
	return true
}

func (commandTest *CommandTest) Execute(ctx *Context) (err error) {
	err = ctx.Session.MessageReactionAdd(ctx.Message.ChannelID, ctx.Message.ID, "✅")
	return
}
