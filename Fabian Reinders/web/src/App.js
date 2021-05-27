/*
                     Web Frontend für den 'Arrangør' Bot
  	
    Discord Bot, der bei der Organisation des Programmier-Wettbewerbs hilft.

    Einsendung für den 'Programmier-Wettbewerb' der 'Digitalen Woche 2021 Leer'

                     Copyright (c) 2021 Fabian Reinders
*/

import React from 'react'
import { useContext, useEffect } from 'react'
import { Switch, Route, BrowserRouter, Redirect } from 'react-router-dom'
import Login from './pages/Login'
import AdminDashboard from './pages/AdminDashboard'
import MemberDashboard from './pages/MemberDashboard'
import Header from './components/Header'
import { UserContext } from './context/UserContext'
import Signup from './pages/Signup'
import SelectTeam from './pages/SelectTeam'
import { ApiAddress } from './config'

const App = () => {

  const { guild, loggedIn, setLoggedIn, setUser, setGuild } = useContext(UserContext)
    
  useEffect(() => {
    // Notwendige Nutzerinfos mit JWT Header von der REST API abrufen
    const getUser = () => {
      fetch(ApiAddress + "/api/auth/get/user", {
        mode: 'cors',
        headers: {
          "Authorization": "Bearer " + localStorage.getItem("jwt")
        }
      })
      .then(async res => {
        if(res.ok) {
          res = await res.json()
          console.log(res);
          setLoggedIn(true);
          setUser(res);
          getGuild()
        }
      })
    }
    
    // State und Code bei der REST API gegen JWT eintauschen
    // und diesen im localStorage speichern
    const getCallback = (state, code) => {
      fetch(ApiAddress + `/api/auth/callback?state=${state}&code=${code}`)
        .then(async res => {
          if(res.ok) {
            res = await res.json()
            console.log(res.jwt);
            localStorage.setItem("jwt", res.jwt)
            getUser()
          }
        })
    }

    const getGuild = () => {
      fetch(ApiAddress + "/api/auth/get/guild",{
          mode: 'cors',
          headers: {
            "Authorization": "Bearer " + localStorage.getItem("jwt")
          }
        })
        .then(async res => {
          if(res.ok) {
            res = await res.json();
            console.log(res);
            setGuild(res)
          } else if(res.status === 401) {
            setLoggedIn(false);
            setGuild(false);
          }
        });
    }

      // Prüfen, ob schon ein JWT im localStorage gespeichert ist
      if(localStorage.getItem("jwt") === null) {
        // Versuchen, State und Code aus den URL Parametern auszulesen
        var params = new URLSearchParams(document.location.search);
        var state = params.get("state");
        var code = params.get("code");
        console.log(state, code)
        if(state !== null || code !== null) {
          getCallback(state, code)
        } else {
          setLoggedIn(false)
          setGuild(false)
        }
      } else {
        getUser()
      }
    }, [setLoggedIn, setUser, setGuild])

  if(loggedIn === "pending" || guild === "pending") {
    return null
  }

  return (
    <BrowserRouter>
      <Header />
      <Switch>
        <Route exact path="/">
          {
            loggedIn
              ? <Redirect to="/dashboard" />
              : <Redirect to="/login" />
          }
        </Route>

        <Route exact path="/dashboard">
          {
            !loggedIn
            ? <Redirect to="/login" /> 
            : guild.user_is_admin
            ? <Redirect to="/dashboard/admin" />
            : <MemberDashboard />
          }
        </Route>

        <Route path="/dashboard/admin">
          {
            !loggedIn
            ? <Redirect to="/login" /> 
            : !guild.user_is_admin
            ? <Redirect to="/dashboard" />
            : <AdminDashboard />
          }
        </Route>

        <Route path="/login">
          {
            loggedIn 
            ? <Redirect to="/dashboard" />
            : <Login />
          }
        </Route>

        <Route path="/signup">
        {
            loggedIn
              ? <Signup />
              : <Redirect to="/login" />
          }
        </Route>

        <Route path="/select">
        {
            loggedIn
              ? <SelectTeam />
              : <Redirect to="/login" />
          }
        </Route>
      </Switch>
    </BrowserRouter>
  );
}

export default App;
