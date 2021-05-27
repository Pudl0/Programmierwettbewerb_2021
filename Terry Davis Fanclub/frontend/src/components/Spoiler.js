import "./Spoiler.res/style.scss"
import React from "react"
import {CaretDownFill, CaretUpFill} from "react-bootstrap-icons";
import {Collapse} from "react-bootstrap";

function Spoiler(properties) {

    const [collapsed, setCollapsed] = React.useState(properties.collapsed ?? true);

    let icon = collapsed ? <CaretDownFill/> : <CaretUpFill/>;

    return (
        <div className="spoiler">
            <a onClick={() => setCollapsed(!collapsed)}><h5>{icon}{properties.title}</h5></a>
            <Collapse in={!collapsed}>
                <div>
                    {properties.children}
                </div>
            </Collapse>
        </div>
    );
}

export default Spoiler;