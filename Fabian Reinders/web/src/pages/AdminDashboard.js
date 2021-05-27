import React from 'react'
import { Flex, Spacer} from '@chakra-ui/react'
import Applications from '../components/Applications'
import Teams from '../components/Teams'
import { ApplicationTeamProvider } from '../context/ApplicationTeamContext'

const AdminDashboard = () => {
    return (
        <Flex flexDirection={{base: "column", md: "row"}} mx={10}>
            <ApplicationTeamProvider>
                <Teams />
                <Spacer />
                <Applications />
                <Spacer />
            </ApplicationTeamProvider>
        </Flex>
    )
}

export default AdminDashboard