function Logout() {

    fetch("/api/logout", {
        method: "GET",
        credentials: "include"
    }).then(() => {
        window.location.reload(false);
    });
}

export default Logout;