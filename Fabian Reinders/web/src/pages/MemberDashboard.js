import React from 'react'
import { Button } from "@chakra-ui/button"
import { DragHandleIcon, EditIcon } from "@chakra-ui/icons"
import { Image } from "@chakra-ui/image"
import { Box, Divider, Flex, Heading, Text } from "@chakra-ui/layout"
import { useContext } from "react"
import { UserContext } from "../context/UserContext"
import { useColorMode } from '@chakra-ui/react'
import { Link as ReactRouterLink } from 'react-router-dom'

const UserDashboard = () => {
    const { user, guild } = useContext(UserContext)
    const { colorMode } = useColorMode();

    return (
        <Flex mt={10} flexDirection="column" align="center" justifyContent="center">
            <Flex p={8} maxW={{ base: "90%", md: "600px" }} borderWidth={1} borderRadius={8} boxShadow="lg" flexDirection="column" align="center" justifyContent="center" >
                <Box textAlign="center" width="80%">
                    <Heading>Hallo, {user.username}!</Heading>
                    {guild.user_is_member && <Text p={5}>Du bist bereits auf unserem Discord! Sehr gut! Bitte wähle aus, ob du dich für den Wettbewerb anmelden möchtest oder dir dein Team zuweisen möchtest.</Text>}
                    {!guild.user_is_member && <Text p={5}>Du scheinst noch nicht auf dem Discord-Server zu sein... Um dich (über den Bot) anmelden zu können und/oder dein Team wählen zu können, tritt bitte dem Discord-Server bei und logge dich mit einem Discord-Account hier ein, der auf dem Server ist.</Text>}
                </Box>
                <Divider mt={3} mb={3} />
                {guild.user_is_member &&
                    (<Box my={4}>
                        <Button as={ReactRouterLink} to="/signup" width="100%" size="md">
                            <EditIcon mr={3} />
                            Für den Wettbewerb anmelden
                        </Button>
                        <Divider mt={3} mb={3} />
                        <Button as={ReactRouterLink} to="/select" width="100%" size="md">
                            <DragHandleIcon mr={3} />
                            Team auswählen/ändern
                        </Button>
                    </Box>)
                }
                {!guild.user_is_member &&
                    (<Box my={4}>
                        <Button width="100%" as="a" href={guild.invite_link} target="_blank" size="md">
                            {colorMode === 'light' ? <Image boxSize="30px" mr={3} alt="" src="/assets/Discord-Logo-Black.png" /> : <Image boxSize="30px" mr={3} alt="" src="/assets/Discord-Logo-White.png" />}
                            Dem Discord-Server beitreten
                        </Button>
                        <Divider my={5} />
                        <Text mt={5} textAlign="center">Wenn du dem Server beigetreten bist, kannst du einfach diese Seite aktualisieren.</Text>
                    </Box>)
                }
            </Flex>
        </Flex>
    )
}

export default UserDashboard
