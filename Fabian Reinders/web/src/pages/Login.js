import React from 'react'
import { Button } from "@chakra-ui/button"
import { Image } from "@chakra-ui/image";
import { Box, Flex, Heading, Text } from "@chakra-ui/layout"
import { ApiAddress } from '../config'

const Login = () => {
    return (
        <Flex mt={10} flexDirection="column" align="center" justifyContent="center">
            <Flex p={8} maxW={{ base: "90%", md: "600px" }} borderWidth={1} borderRadius={8} boxShadow="lg" flexDirection="column" align="center" justifyContent="center" >
                <Box textAlign="center" width="80%">
                    <Heading>Login</Heading>
                    <Text p={5}>Um fortzufahren, logge dich bitte mit deinem Discord-Account ein. Wir verbinden deine Anmeldung mit deinem Discord-Account, so dass du dir später selbst dein Team zuweisen kannst.</Text>
                </Box>
                <Box my={4} textAlign="center">
                    <Button _hover={{bg: "#5d70b3"}} color="#fff" bg="#7289da" as="a" href={ApiAddress + "/api/auth"} size="lg">
                        <Image boxSize="40px" mr={3} alt="" src="/assets/Discord-Logo-White.png" />
                        Mit Discord anmelden
                    </Button>
                </Box>
            </Flex>
        </Flex>
    )
}

export default Login
