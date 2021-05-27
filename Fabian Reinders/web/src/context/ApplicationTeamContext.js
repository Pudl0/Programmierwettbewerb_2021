import React from 'react'
import { createContext, useState } from 'react'

export const ApplicationTeamContext = createContext();

export const ApplicationTeamProvider = ({ children }) => {
    // Application states
    const [approvedApplications, setApprovedApplications] = useState([])
    const [pendingApplications, setPendingApplications] = useState([])
    
    // Team states
    const [approvedTeams, setApprovedTeams] = useState([])
    const [pendingTeams, setPendingTeams] = useState([])

    // State, der dafür sagt, dass Daten neugeladen und Components neu gerendert werden
    const [reload, setReload] = useState(false)

    return (
        <ApplicationTeamContext.Provider
            value={{
                approvedApplications,
                pendingApplications,
                approvedTeams,
                pendingTeams,
                reload,
                setApprovedApplications,
                setPendingApplications,
                setApprovedTeams,
                setPendingTeams,
                setReload
            }}
        >
            {children}
        </ApplicationTeamContext.Provider>
    )
}