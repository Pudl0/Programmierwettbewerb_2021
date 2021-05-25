import "./App.res/style.scss"
import "./theme/dark.scss"
import React from "react"
import {BrowserRouter as Router, Redirect, Route, Switch} from "react-router-dom";
import Login from "./pages/Login";
import {Modal, Nav} from "react-bootstrap";
import Home from "./pages/Home";
import Settings from "./pages/Settings";
import Logout from "./pages/Logout";
import Announcements from "./pages/Announcements";
import Teams from "./pages/Teams";
import Applications from "./pages/Applications";

function App() {

    const [session, setSession] = React.useState(null);

    if (localStorage.getItem("theme") === null) {
        localStorage.setItem("theme", "dark");
    }

    if (localStorage.getItem("theme") === "dark") {
        document.body.style.backgroundColor = "#161616";
    }

    if (!session) {
        fetch("/api/session", {
            method: "GET",
            credentials: "include"
        })
            .then(data => data.json())
            .then(session => setSession(session));

        return null;
    }

    if (localStorage.getItem("theme") === "dark") {
        return <div className="dark">{getApp(session)}</div>;
    } else {
        return getApp(session);
    }
}

function getApp(session) {

    let loginRedirect = null;
    if (!session?.authorized) {
        loginRedirect = (
            <Route>
                <Redirect to="/login"/>
            </Route>
        );
    }

    return (
        <Router>

            <Switch>
                <Route path="/login">
                    <Login session={session}/>
                </Route>
                {loginRedirect}

                <Modal.Dialog size="xl">
                    <Modal.Header>
                        <Nav>
                            <Nav.Item>
                                <Nav.Link href="/">Home</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link href="/settings">Einstellungen</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link href="/announcements">Ankündigungen</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link href="/teams">Teams</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link href="/applications">Anmeldungen</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link href="/logout">Logout</Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Modal.Header>

                    <Modal.Body>
                        <Route exact path="/">
                            <Home/>
                        </Route>
                        <Route exact path="/settings">
                            <Settings/>
                        </Route>
                        <Route exact path="/announcements">
                            <Announcements/>
                        </Route>
                        <Route exact path="/teams">
                            <Teams/>
                        </Route>
                        <Route exact path="/applications">
                            <Applications/>
                        </Route>
                        <Route exact path="/logout">
                            <Logout/>
                        </Route>
                    </Modal.Body>
                </Modal.Dialog>
            </Switch>
        </Router>
    );
}

export default App;