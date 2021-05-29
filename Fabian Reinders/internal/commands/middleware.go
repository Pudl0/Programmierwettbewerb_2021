package commands

// Middleware, die vor der Command-Execution ausgeführt wird
// und diese notfalls verhindern kann
type Middleware interface {
	Execute(ctx *Context, cmd Command) (next bool, err error)
}
