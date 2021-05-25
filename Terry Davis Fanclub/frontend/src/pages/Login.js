import React from "react"
import {Button, Form, Modal} from "react-bootstrap";
import sha256 from "crypto-js/sha256"
import {Redirect} from "react-router-dom";

function Login(properties) {

    const [password, setPassword] = React.useState("");
    const [valid, setValid] = React.useState(true);

    if (properties.session?.authorized === true) {
        return <Redirect to="/"/>;
    }

    let submit = e => {
        fetch("/api/authorize", {
            method: "POST",
            body: JSON.stringify({
                password: sha256(password).toString()
            }),
            credentials: "include"
        }).then(data => data.json())
            .then(response => {
                console.log(response);
                if (response.success === true) {
                    window.location.reload(false);
                } else {
                    setValid(false);
                }
            });
        e.preventDefault();
        return false;
    }

    return (
        <Modal.Dialog>
            <Form onSubmit={submit}>
                <Modal.Header>
                    <Modal.Title>Login</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <p>Logge dich mit deinem Passwort ein, um das Webinterface zu nutzen. Hast du noch keins festgelegt,
                        steht es in der Konsole.</p>

                    <Form.Group>
                        <Form.Label>Passwort</Form.Label>
                        <Form.Control type="password" placeholder="Passwort" onChange={e => setPassword(e.target.value)}
                                      value={password} isInvalid={!valid}/>
                        <Form.Control.Feedback type="invalid">
                            Falsches Passwort
                        </Form.Control.Feedback>
                    </Form.Group>
                </Modal.Body>

                <Modal.Footer>
                    <Button type="submit">Login</Button>
                </Modal.Footer>
            </Form>
        </Modal.Dialog>
    )
}

export default Login;