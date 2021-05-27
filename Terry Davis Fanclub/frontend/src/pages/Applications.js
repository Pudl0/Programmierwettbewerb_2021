import "./Applications.res/style.scss"
import React from "react"
import {Button, ListGroup} from "react-bootstrap";
import {XLg} from "react-bootstrap-icons";

function Applications() {

    const [applications, setApplications] = React.useState(null);

    let loadApplications = () => {
        fetch("/api/applications/get", {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(applications => setApplications(applications));
    }

    if (!applications) {
        loadApplications();
        return null;
    }

    let dateFormatOptions = {
        day: "numeric",
        year: "numeric",
        month: "long",
        hour: "numeric",
        minute: "numeric"
    };

    let deleteApplication = email => {
        fetch("/api/applications/delete", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({
                email: email
            })
        }).then(loadApplications);
    }

    let downloadPath = "/api/applications/get/csv/anmeldungen.csv";
    if (process.env.NODE_ENV !== "production") {
        downloadPath = "http://localhost:8000" + downloadPath;
    }

    let button = (<a href={downloadPath} download><Button>CSV-Download für Excel</Button></a>);
    if (applications.length < 1) {
        button = null;
    }

    let applicationsHtml = (<p>Es gibt noch keine Anmeldungen.</p>);

    if (applications.length > 0) {
        applicationsHtml = [];
        for (let application of applications) {
            applicationsHtml.push(<ListGroup.Item>
                <a onClick={() => deleteApplication(application.email)}><XLg/> </a>
                {application.name} ({application.email})
                <span className="date"> {new Date(application.created).toLocaleString("de", dateFormatOptions)}</span>
            </ListGroup.Item>);
        }
        applicationsHtml = (<ListGroup>
            {applicationsHtml}
        </ListGroup>);
    }

    return (<div className="applications">
        <h2>Anmeldungen</h2>
        {applicationsHtml}
        {button}
    </div>);
}

export default Applications;