import React from 'react'
import { LockIcon, MoonIcon, SunIcon } from "@chakra-ui/icons"
import { Heading, Flex } from "@chakra-ui/layout"
import { IconButton, Image, useColorMode } from '@chakra-ui/react'
import { useContext } from "react"
import { UserContext } from "../context/UserContext"

const Header = () => {
    const { user, loggedIn } = useContext(UserContext)
    const { colorMode, toggleColorMode } = useColorMode();

    const logout = () => {
        localStorage.removeItem("jwt")
        window.location.reload()
    }

    return (
        <Flex justifyContent="space-between" p={6} width="full">
            <Flex alignItems="center">
                <Image mr={2} rounded={10} boxSize="50px" src={`/assets/favicon-32x32.png`} />
                <Heading fontSize="2xl">Arrangør</Heading>
            </Flex>
            <Flex alignItems="center">
                {loggedIn && (
                    <Image mr={3} display={{base: "none", sm: "inherit"}} justifyContent="flex-end" rounded={10} boxSize="35px" src={`https://cdn.discordapp.com/avatars/${user.id}/${user.avatar}.png`} />)}
                {loggedIn && <Heading mr={3} display={{base: "none", sm: "inherit"}} fontSize="1xl">{user.username}</Heading>}
                {loggedIn && <IconButton
                    icon={<LockIcon />}
                    mr={3}
                    ml={3}
                    onClick={e => logout()}
                    variant="ghost"
                />}
                <IconButton
                    icon={colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
                    onClick={toggleColorMode}
                    variant="ghost"
                />
            </Flex>
        </Flex>
    )
}

export default Header