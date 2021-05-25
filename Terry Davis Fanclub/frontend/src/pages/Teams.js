import "./Teams.res/style.scss"
import {Button, Form, Modal} from "react-bootstrap";
import {PlusCircle, TrashFill} from "react-bootstrap-icons";
import React from "react";
import {CirclePicker} from "react-color";

function Teams() {

    const [teams, setTeams] = React.useState(null);
    const [response, setResponse] = React.useState(true);
    const [showDialog, setShowDialog] = React.useState(false);
    const [teamName, setTeamName] = React.useState("");
    const [teamColor, setTeamColor] = React.useState("#f44336");

    let fetchTeams = () => {
        fetch("/api/teams/get", {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(teams => setTeams(teams));
    }

    if (!teams) {
        fetchTeams();
        return null;
    }

    let submit = e => {
        e.preventDefault();
        if (teamName.length === 0) {
            return false;
        }
        fetch("/api/teams/add", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                name: teamName,
                color: teamColor
            })
        }).then(data => data.json())
            .then(response => {
                setResponse(response);
                if (response.success === true) {
                    setShowDialog(false);
                    setTeamName("");
                    setTeamColor("#f44336");
                    fetchTeams();
                }
            });
        return false;
    }

    let teamList = (<p>Es gibt noch keine Teams.</p>);
    if (teams.length > 0) {
        teamList = [];
        for (let team of teams) {
            teamList.push(
                <div className="team">
                    <div className="color" style={{backgroundColor: team.color}}/>
                    <h4>
                        {team.name}
                    </h4>
                    <Button variant="danger" size="sm" onClick={() => {
                        fetch("/api/teams/delete", {
                            method: "POST",
                            credentials: "include",
                            body: JSON.stringify(team)
                        }).then(fetchTeams);
                    }}><TrashFill/></Button>
                </div>
            );
        }
    }

    return (<div className="teams">
        <h2>Teams</h2>
        {teamList}
        <Button variant="primary" className="add-team" onClick={() => setShowDialog(true)}><PlusCircle/> Öffentliches
            Team hinzufügen</Button>

        <Modal show={showDialog} onHide={() => setShowDialog(false)} backdrop="static"
               className={localStorage.getItem("theme")}>
            <Modal.Header closeButton>
                <Modal.Title>Team erstellen</Modal.Title>
            </Modal.Header>

            <Form onSubmit={submit}>
                <Modal.Body>
                    <Form.Group>
                        <Form.Label>Team-Name</Form.Label>
                        <Form.Control type="text" value={teamName} onChange={e => setTeamName(e.target.value)}
                                      isInvalid={response?.success === false}/>
                        <Form.Control.Feedback type="invalid">
                            Team existiert bereits
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>Team-Farbe</Form.Label>
                        <CirclePicker color={teamColor} onChangeComplete={e => setTeamColor(e.hex)}/>
                    </Form.Group>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="primary" type="submit" disabled={teamName.length === 0}>Speichern</Button>
                </Modal.Footer>
            </Form>
        </Modal>
    </div>);
}

export default Teams;