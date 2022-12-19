import * as connection from "./connection.js";
import React from 'react';
import ReactDOM from 'react-dom/client';

function Hello(props) {
    return <h1>Hello World!</h1>;
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<Hello />);

function connectWithUserName(user) {
    connection.connect(user)
}

function sendPublicMessage(user) {
    connection.sendMessage(user)
}

function sendPrivateMessage(user) {
    connection.sendDirectMessage(user)
}