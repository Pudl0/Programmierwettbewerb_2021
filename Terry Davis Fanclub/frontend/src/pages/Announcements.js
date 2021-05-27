import React from "react"
import {Button, Form} from "react-bootstrap";
import "./Announcements.res/style.scss"

function Announcements() {

    const [title, setTitle] = React.useState("");
    const [description, setDescription] = React.useState("");

    let submit = e => {
        e.preventDefault();
        fetch("/api/announce", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                title: title,
                description: description
            })
        }).then(() => {
            setTitle("");
            setDescription("");
        });
        return false;
    }

    return (<div className="announcements">

        <h2>Ankündigungen</h2>

        <Form onSubmit={submit}>
            <Form.Group>
                <Form.Label>Titel</Form.Label>
                <Form.Control type="text" value={title} onChange={e => setTitle(e.target.value)}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>Beschreibung</Form.Label>
                <Form.Control as="textarea" rows="6" value={description}
                              onChange={e => setDescription(e.target.value)}/>
            </Form.Group>
            <Button variant="primary" type="submit">Absenden</Button>
        </Form>
    </div>);
}

export default Announcements;