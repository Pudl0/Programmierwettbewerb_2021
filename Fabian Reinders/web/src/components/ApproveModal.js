import { ArrowForwardIcon } from '@chakra-ui/icons'
import { Button, FormControl, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Text, useDisclosure } from '@chakra-ui/react'
import { useContext, useState } from 'react'
import { ApiAddress } from '../config'
import { ApplicationTeamContext } from '../context/ApplicationTeamContext'

const ApproveModal = ({ isApplication, application, team }) => {
    const { reload, setReload } = useContext(ApplicationTeamContext)
    const { isOpen, onOpen, onClose } = useDisclosure()

    const [name, setName] = useState(isApplication ? application.name : team.name)
    const [status, setStatus] = useState("pending")

    const commitChange = (changeType) => {
      fetch(ApiAddress + `/api/${isApplication ? 'application' : 'team'}/${changeType}`, {
        mode: 'cors',
        method: changeType === 'accept' ? 'PUT' : 'DELETE',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
          "Authorization": "Bearer " + localStorage.getItem("jwt")
        },
        body: changeType === 'accept'
                ? isApplication ? JSON.stringify({id: application.id, name: name}) : JSON.stringify({id: team.id, name: name}) 
                : isApplication ? JSON.stringify({id: application.id}) : JSON.stringify({id: team.id})
      
        })
          .then(async res => {
            if(res.ok) setStatus(true)
            else setStatus(false)
            onClose()
            setReload(!reload ? true : false)
          })
    }

    return (
      <>
        <Button mt={3} onClick={onOpen} rightIcon={<ArrowForwardIcon />} colorScheme="teal" variant="outline">Erstellen</Button>

        <Modal isOpen={isOpen} onClose={onClose}>
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>{ isApplication ? 'Anmeldung' : 'Team ' } annehmen / bearbeiten</ModalHeader>
            <ModalCloseButton />
            <ModalBody pb={6}>
              <FormControl>
                <FormLabel>Name</FormLabel>
                <Input value={name} onChange={e => setName(e.target.value)} />
              </FormControl>
  
            { isApplication && 
                (<FormControl mt={4}>
                    <FormLabel>E-Mail</FormLabel>
                    <Input readOnly={true} value={application.email} disabled />
                </FormControl>)
            }

            { isApplication && 
                (<FormControl mt={4}>
                    <FormLabel>Team</FormLabel>
                    <Input readOnly={true} value={application.team.name} disabled />
                </FormControl>)
            }

            </ModalBody>
  
              {
                status === "pending"
                ? (
                  <ModalFooter>
                    <Button onClick={e => { commitChange('accept'); }} colorScheme="blue" mr={3}>Speichern & Annehmen</Button>
                    <Button onClick={e => { commitChange('decline'); }}>Ablehnen</Button>
                  </ModalFooter>
                  ) : status ? <Text textAlign="center" mb={5} color="green">Erfolg!</Text> : <Text textAlign="center" mb={5} color="red">Ein Fehler ist aufgetreten!</Text>
              }
          </ModalContent>
        </Modal>
      </>
    )
}

export default ApproveModal
