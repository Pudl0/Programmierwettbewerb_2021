import { Box, Heading } from "@chakra-ui/layout"
import Application from "./Application"
import { ApiAddress } from '../config'
import { useContext, useEffect } from "react"
import { ApplicationTeamContext } from "../context/ApplicationTeamContext"

const Applications = () => {
    const { approvedApplications, setApprovedApplications, pendingApplications, setPendingApplications, reload } = useContext(ApplicationTeamContext)

    const updateData = () => {
        setApprovedApplications([])
        setPendingApplications([])

        fetch(ApiAddress + "/api/application/list", {
            mode: 'cors',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('jwt')
            }
        })
            .then(async res => {
                if(res.ok) {
                    res = await res.json()
                    await res.forEach(res => {
                        if(res.accepted > 0) setApprovedApplications(prev => [...prev, res])
                        else setPendingApplications(prev => [...prev, res])
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
        <Box p={5} width={{base: "100%", md: "68%"}} overflowX="hidden" borderWidth={1} borderRadius={8} boxShadow="lg" flexDirection="column" align="center" justifyContent="center">
                <Heading size="lg">Ausstehende Anmeldungen</Heading>
                {
                    pendingApplications.length < 1 ? <p>Keine ausstehenden Anmeldungen vorhanden.</p>
                    : pendingApplications.map((application, index) => (
                        <Application key={index} application={application} accepted={false} />
                    ))
                }
                <Heading mt={50} size="lg">Angenommene Anmeldungen</Heading>
                {
                    approvedApplications.length < 1 ? <p>Keine angenommenen Anmeldungen vorhanden.</p>
                    : approvedApplications.map((application, index) => (
                        <Application key={index} application={application} accepted={true} />
                    ))
                }
        </Box>
    )
}

export default Applications
