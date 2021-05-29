package api

import (
	"github.com/gofiber/fiber/v2"
	jwtware "github.com/gofiber/jwt/v2"
)

// Middleware, die einen JWT Authorization Header verlangt und validiert
func Protected() fiber.Handler {
	return jwtware.New(jwtware.Config{
		SigningKey:   []byte("gbt3FPVq5#MwU8SM^hpvUJwxEw"),
		ContextKey:   "jwtoken",
		ErrorHandler: jwtError,
	})
}

func jwtError(ctx *fiber.Ctx, err error) error {
	if err.Error() == "Missing or malformed JWT" {
		return ctx.SendStatus(401)
	}
	return ctx.SendStatus(401)
}
