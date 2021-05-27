package commands

type Command interface {
	Invokes() []string
	AdminPermissionsNeeded() bool
	Execute(ctx *Context) error
}
