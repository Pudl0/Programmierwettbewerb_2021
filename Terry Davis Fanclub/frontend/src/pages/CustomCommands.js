import React from "react"
import {Button, Form, ListGroup, ListGroupItem, Modal} from "react-bootstrap";
import {PlusCircle, Trash} from "react-bootstrap-icons";
import "./CustomCommands.res/style.scss"

function CustomCommands() {

    const [commands, setCommands] = React.useState(null);
    const [showDialog, setShowDialog] = React.useState(false);
    const [label, setLabel] = React.useState("mein-befehl");
    const [desc, setDesc] = React.useState("Mein neuer Befehl 🎉");
    const [commandResponse, setCommandResponse] = React.useState("Das ist mein neuer Befehl 😄");
    const [response, setResponse] = React.useState(null);

    let fetchCommands = () => {
        fetch("/api/customcommand/get", {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(commands => setCommands(commands));
    };

    if (!commands) {
        fetchCommands();
        return null;
    }

    let deleteCommand = commandName => {
        fetch(`/api/customcommand/delete/${commandName}`, {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(response => {
                if (response.success === true) {
                    fetchCommands();
                }
            });
    }

    let commandList = <p>Du hast noch keine eigenen Befehle eingerichtet.</p>;
    if (commands.length > 0) {
        commandList = [];
        for (let command of commands) {
            commandList.push(<ListGroupItem>
                <Button variant="danger" size="sm" onClick={() => deleteCommand(command.label)}><Trash/></Button>
                <strong>Befehl</strong><br/>
                {command.label}<br/>
                <strong>Beschreibung</strong><br/>
                {command.desc}<br/>
                <strong>Antwort</strong><br/>
                {command.response}
            </ListGroupItem>);
        }
        commandList = <ListGroup>{commandList}</ListGroup>;
    }

    let submit = e => {
        e.preventDefault();
        fetch("/api/customcommand/add", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                label: label,
                response: commandResponse,
                desc: desc
            })
        }).then(data => data.json())
            .then(response => {
                setResponse(response);
                if (response.success === true) {
                    fetchCommands();
                    setShowDialog(false);
                }
            });
        return false;
    }

    return (<div className="custom-commands">
        <h2>Eigene Befehle</h2>
        {commandList}
        <Button onClick={() => setShowDialog(true)}><PlusCircle/> Befehl hinzufügen</Button>

        <Modal show={showDialog} onHide={() => setShowDialog(false)} backdrop="static"
               className={localStorage.getItem("theme")}>

            <Modal.Header closeButton>
                <Modal.Title>Befehl erstellen</Modal.Title>
            </Modal.Header>

            <Form onSubmit={submit}>
                <Modal.Body>
                    <Form.Group>
                        <Form.Label>Befehl</Form.Label>
                        <Form.Control type="text" value={label}
                                      onChange={e => setLabel(e.target.value.replace(" ", ""))}
                                      isInvalid={response?.success === false}/>
                        <Form.Control.Feedback type="invalid">
                            Befehl existiert bereits
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Beschreibung</Form.Label>
                        <Form.Control type="text" value={desc} onChange={e => setDesc(e.target.value)}/>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Antwort</Form.Label>
                        <Form.Control type="text" value={commandResponse}
                                      onChange={e => setCommandResponse(e.target.value)}/>
                    </Form.Group>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="primary" type="submit"
                            disabled={label.length === 0 || commandResponse.length === 0 || desc.length === 0}>
                        Speichern
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    </div>);
}

export default CustomCommands;