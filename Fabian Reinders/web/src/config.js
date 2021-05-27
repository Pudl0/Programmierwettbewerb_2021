/*
    Backend (Bot + API) und Frontend (Web-Dashboard) können komplett
    unabhänig voneinander gehostet werden.
    Damit das Dashboard aber weiß, wo es den Bot erreichen kann, muss
    bei 'ApiAddress' der Link eingetragen werden, unter dem die REST API
    des Bots erreichbar ist.
*/

export const ApiAddress = "http://localhost:5000" // ohne das '/api/'