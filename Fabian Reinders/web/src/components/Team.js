import { DragHandleIcon } from "@chakra-ui/icons"
import { Flex, VStack } from "@chakra-ui/layout"
import ApproveModal from "./ApproveModal"

const Team = ({ team, approved }) => {
    return (
        <Flex p={5} m={5} flexDirection="column" justifyContent="space-between" alignItems="center" borderWidth={1} borderRadius={8} boxShadow="lg">
            <VStack mb={{base: 5, md: 0}} boxSize="100%"><DragHandleIcon boxSize="25px" /><p style={{fontSize: "17px"}}>{team.name === "" ? 'Kein Team' : team.name}</p></VStack>
            { !approved && <ApproveModal isApplication={false} team={team} /> }
        </Flex>
    )
}

export default Team
