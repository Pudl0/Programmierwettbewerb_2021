import React from 'react'
import { createContext, useState } from 'react'

export const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [loggedIn, setLoggedIn] = useState("pending");
    const [user, setUser] = useState("pending");
    const [guild, setGuild] = useState("pending");

    return (
        <UserContext.Provider
            value={{
                loggedIn,
                user,
                guild,
                setLoggedIn,
                setUser,
                setGuild
            }}
        >
            {children}
        </UserContext.Provider>
    )
}