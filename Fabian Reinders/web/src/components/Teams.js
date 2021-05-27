import { Box, Heading } from "@chakra-ui/layout"
import { useContext, useEffect } from "react"
import Team from "./Team"
import { ApiAddress } from '../config'
import { ApplicationTeamContext } from "../context/ApplicationTeamContext"

const Teams = () => {
    const { approvedTeams, setApprovedTeams, pendingTeams, setPendingTeams, reload } = useContext(ApplicationTeamContext)

    const updateData = () => {
        setApprovedTeams([])
        setPendingTeams([])
        
        fetch(ApiAddress + "/api/team/list", {
            mode: 'cors',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwt')
            }
        })
            .then(async res => {
                if(res.ok) {
                    res = await res.json()
                    await res.forEach(res => {
                        if(res.approved > 0) setApprovedTeams(prev => [...prev, res])
                        else setPendingTeams(prev => [...prev, res])
                    })
                } else if(res.status === 401) {
                    localStorage.removeItem('jwt')
                    window.location.reload()
                } else {
                    alert("Es gab einen Fehler beim Laden der Anmeldungen.")
                }
            })
    }

    useEffect(() => {
        updateData()
    }, [reload]) // eslint-disable-line react-hooks/exhaustive-deps

    return (
        <Box p={5} width={{base: "100%", md: "23%"}} mr={5} overflowX="hidden" borderWidth={1} borderRadius={8} boxShadow="lg" flexDirection="column" align="center" justifyContent="center">
            <Heading size="lg">Teams</Heading>
            {
                pendingTeams.length < 1 ? <p>Keine ausstehenden Teams vorhanden.</p>
                : pendingTeams.map((team, index) => (
                    <Team key={index} team={team} approved={false} />
                ))
            }
            {
                approvedTeams.length < 1 ? <p>Keine angenommenen Teams vorhanden.</p>
                : approvedTeams.map((team, index) => (
                    <Team key={index} team={team} approved={true} />
                ))
            }
        </Box>
    )
}

export default Teams
