import {Alert, Button, Form} from "react-bootstrap";
import React from "react";
import "./Settings.res/style.scss"
import sha256 from "crypto-js/sha256";
import Spoiler from "../components/Spoiler";
import dict from "../data/dict"

function Settings(properties) {

    const [oldPassword, setOldPassword] = React.useState("");
    const [newPassword, setNewPassword] = React.useState("");
    const [newPasswordRepeat, setNewPasswordRepeat] = React.useState("");
    const [response, setResponse] = React.useState(null);
    const [success, setSuccess] = React.useState(false);
    const [config, setConfig] = React.useState(null);
    const [darkMode, setDarkMode] = React.useState(localStorage.getItem("theme") === "dark");
    let newConfig = {...config};

    if (!config) {
        fetch("/api/config/get", {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(config => setConfig(config));

        return null;
    }

    let isValidPasswordForm = () => {
        return newPassword.length >= 8 && newPassword.length <= 32 && newPassword === newPasswordRepeat && oldPassword.length > 0;
    }

    let submitPassword = e => {

        e.preventDefault();
        if (!isValidPasswordForm()) {
            return false;
        }

        fetch("/api/password", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                password: sha256(oldPassword).toString(),
                newPassword: sha256(newPassword).toString()
            })
        }).then(data => data.json())
            .then(response => {
                setResponse(response);
                setOldPassword("");
                setNewPassword("");
                setNewPasswordRepeat("");
            });

        return false;
    }

    let responseAlert = null;
    if (response?.success === true) {
        responseAlert = <Alert variant="success">Passwort geändert</Alert>;
    }

    let getForm = (obj, parent) => {

        let formGroups = [];

        for (let i in obj) {
            if (!obj.hasOwnProperty(i)) continue;
            if (typeof obj[i] == "object") {
                formGroups.push(
                    <Spoiler title={dict[i] ?? i} collapsed={false}>
                        {getForm(obj[i], i + ".")}
                    </Spoiler>
                );
            } else {
                formGroups.push(
                    <Form.Group>
                        <Form.Label>{dict[parent + i] ?? parent + i}</Form.Label>
                        <Form.Control type="text" value={obj[i]} onChange={
                            e => {
                                obj[i] = e.target.value;
                                setConfig(newConfig);
                            }
                        }/>
                    </Form.Group>
                );
            }
        }

        return formGroups;
    }

    let confirmationMessage = null;
    if (success) {
        confirmationMessage = (<Alert variant="success">Konfiguration erfolgreich gespeichert.</Alert>);
    }

    let submitConfig = e => {
        e.preventDefault();
        fetch("/api/config/update", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                ...config
            }),
            headers: {
                contentType: "application/json"
            }
        }).then(data => data.json())
            .then(response => {
                if (response.success === true) {
                    setSuccess(true);
                    window.scrollTo(0, 0);
                }
            });
        return false;
    };

    let changeTheme = () => {
        localStorage.setItem("theme", darkMode ? "light" : "dark");
        setDarkMode(!darkMode);
        window.location.reload(false);
    };

    return (<div className="settings">

        <h2>Einstellungen</h2>

        <h4>Passwort</h4>
        <Form onSubmit={submitPassword} className="password-form">
            <Form.Group>
                {responseAlert}
                <Form.Label>Momentanes Passwort</Form.Label>
                <Form.Control type="password" placeholder="Momentanes Passwort"
                              onChange={e => setOldPassword(e.target.value)} value={oldPassword}
                              isInvalid={response?.success === false}/>
                <Form.Control.Feedback type="invalid">
                    Falsches Passwort
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group>
                <Form.Label>Neues Passwort</Form.Label>
                <Form.Control type="password" placeholder="Neues Passwort"
                              onChange={e => setNewPassword(e.target.value)} value={newPassword}
                              isInvalid={newPassword !== "" && (newPassword.length < 8 || newPassword.length > 32)}/>
                <Form.Control.Feedback type="invalid">
                    Passwort muss zwischen 8 und 32 Zeichen lang sein
                </Form.Control.Feedback>
            </Form.Group>
            <Form.Group>
                <Form.Label>Neues Passwort wiederholen</Form.Label>
                <Form.Control type="password" placeholder="Neues Passwort wiederholen"
                              onChange={e => setNewPasswordRepeat(e.target.value)} value={newPasswordRepeat}
                              isInvalid={newPassword !== newPasswordRepeat}/>
                <Form.Control.Feedback type="invalid">
                    Passwörter müssen übereinstimmen
                </Form.Control.Feedback>
            </Form.Group>
            <Button variant="primary" type="submit" disabled={!isValidPasswordForm()}>Passwort speichern</Button>
        </Form>

        <h4>Dark Mode</h4>
        <div className="dark-mode">
            <Form.Check type="switch" id="dark-mode-switch" label="Dark Mode" checked={darkMode}
                        onChange={changeTheme}/>
        </div>

        <h4>Konfiguration</h4>
        {confirmationMessage}
        <Form onSubmit={submitConfig}>
            {getForm(newConfig, "")}
            <Button variant="primary" type="submit">Konfiguration speichern</Button>
            <p>Einige Änderungen werden möglicherweise erst bei einem Neustart wirksam.</p>
        </Form>

    </div>);
}

export default Settings;