import React from "react"
import "./Home.res/style.scss"
import {Badge, Button, Form} from "react-bootstrap";
import {Pencil, Plus} from "react-bootstrap-icons";

function Home() {

    const [info, setInfo] = React.useState(null);
    const [edit, setEdit] = React.useState(false);
    const [newName, setNewName] = React.useState("");
    const inputFile = React.useRef(null);

    let fetchInfo = () => {
        fetch("/api/bot/info", {
            method: "GET",
            credentials: "include"
        }).then(data => data.json())
            .then(info => {
                setInfo(info);
                setNewName(info.username);
            });
    };

    if (!info) {
        fetchInfo();
        return null;

    } else {
        let servers = <p>Dein Bot befindet sich noch auf keinen Servern. Benutze den Link, um ihn zu deinem Server
            hinzufügen!</p>;

        let leave = guildId => {
            fetch(`/api/bot/leave/${guildId}`, {
                method: "GET",
                credentials: "include"
            }).then(data => data.json())
                .then(response => {
                    if (response.success === true) {
                        window.location.reload(false);
                    }
                });
        };

        if (info && info.guilds.length > 0) {
            servers = [];
            for (let guild of info.guilds) {
                servers.push(<div className="guild">
                    <img src={guild.img} alt="Guild"/>
                    <span>{guild.name}</span>
                    <Button variant="danger" size="sm" onClick={() => {
                        leave(guild.id)
                    }}>Verlassen</Button>
                </div>);
            }
        }

        let openFileDialog = () => {
            inputFile.current.click();
        }

        let uploadProfilePicture = e => {
            if (e.target.files.length !== 1) {
                return;
            }
            console.log(e.target.files);
            let file = e.target.files[0];
            let formData = new FormData();
            formData.append(0, file);
            fetch("/api/bot/image", {
                method: "POST",
                credentials: "include",
                body: formData
            }).then(data => data.json())
                .then(response => {
                    if (response.success === true) {
                        fetchInfo();
                    }
                });
        }

        let onKey = e => {

            if (e.key === "Escape") {
                e.preventDefault();
                setEdit(false);

            } else if (e.key === "Enter") {
                e.preventDefault();
                e.target.disabled = true;
                if (newName === "") {
                    return;
                }
                fetch("/api/bot/name", {
                    method: "POST",
                    credentials: "include",
                    body: JSON.stringify({
                        name: newName
                    })
                }).then(data => data.json())
                    .then(response => {
                        if (response.success === true) {
                            fetchInfo();
                        } else {
                            setNewName(info.username);
                        }
                        setEdit(false);
                    });
            }
        }

        let botName;
        if (!edit) {
            botName = (<><span onClick={() => setEdit(true)}>{info.username}</span><Pencil className="edit"
                                                                                           onClick={() => setEdit(true)}/></>);
        } else {
            botName = (<Form.Control type="text" value={newName} className="edit-bot-name"
                                     onChange={e => setNewName(e.target.value)} onKeyDown={onKey}/>);
        }

        return (<div className="home">
            <h2>Bot-Status</h2>
            <div className="bot-info">
                <div className="profile-picture" style={{backgroundImage: `url(${info.profileImg})`}}
                     onClick={openFileDialog}>
                    <Pencil className="pencil"/>
                </div>
                <h4 className="bot-name">
                    {botName}
                    <span className="discriminator">#{info.discriminator}</span><Badge variant="success">ONLINE</Badge>
                    <br/>
                    <a href={`https://discord.com/oauth2/authorize?client_id=${info.clientId}&scope=bot&permissions=8`}
                       target="_blank" rel="noreferrer">
                        <Button variant="primary" size="sm"><Plus/>Zu Server hinzufügen</Button>
                    </a>
                </h4>
            </div>

            <div className="servers">
                <h2>Server</h2>
                {servers}
            </div>

            <input type="file" ref={inputFile} style={{display: "none"}} onChange={uploadProfilePicture}/>
        </div>);
    }
}

export default Home;