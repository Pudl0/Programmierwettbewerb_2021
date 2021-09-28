import { Button } from '@chakra-ui/button'
import { Box, Flex, Heading, Text } from '@chakra-ui/layout'
import { Select } from '@chakra-ui/select'
import React, { useContext, useEffect, useState } from 'react'
import { UserContext } from '../context/UserContext'
const SelectTeam = () => {

    const { user } = useContext(UserContext)

    const [approvedTeams, setApprovedTeams] = useState([])
    const [selectedTeam, setSelectedTeam] = useState(0)

    const submitChoice = () => {
        if(selectedTeam === '') {
            alert("Bitte wähle ein Team aus")
            return
        }

        fetch("/api/team/select", {
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwt')
            },
            body: JSON.stringify({user_id: user.id, team_id: Number(selectedTeam)})
        })
        console.log(selectedTeam)
    }

    useEffect(() => {
        fetch("/api/team/list", {
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwt')
            }
        })
            .then(async res => {
                if(res.ok) {
                    res = await res.json()
                    await res.forEach(res => {
                        if(res.approved > 0) setApprovedTeams(prev => [...prev, res])
                    })
                } else if(res.status === 401) {
                    localStorage.removeItem('jwt')
                    window.location.reload()
                } else {
                    alert("Es gab einen Fehler beim Laden der Anmeldungen.")
                }
            })
    }, [])

    return (
        <Flex mt={10} flexDirection="column" align="center" justifyContent="center">
            <Flex p={8} maxW={{ base: "90%", md: "600px" }} borderWidth={1} borderRadius={8} boxShadow="lg" flexDirection="column" align="center" justifyContent="center" >
                <Box textAlign="center" width="80%">
                    <Heading>Team auswählen</Heading>
                    <Text p={5}>Unten kannst du aus allen Teams wählen, die bereits akzeptiert wurden und ihnen beitreten.</Text>
                </Box>
                <Box my={4} textAlign="center">
                    <Select mb={5} placeholder="Team wählen" onChange={e => setSelectedTeam(e.target.value)}>
                        {approvedTeams.map(team => (
                            <option key={team.id} value={team.id}>{team.name}</option>
                        ))}
                    </Select>
                    <Button onClick={e => submitChoice()}>
                        Auswahl abschicken
                    </Button>
                </Box>
            </Flex>
        </Flex>
    )
}

export default SelectTeam
