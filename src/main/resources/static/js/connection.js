let stompClient
let username
let sessionId

export function connect(user) {
    console.log("Connect.....")
    username = user
    const socket = new SockJS('/chat')
    stompClient = Stomp.over(socket)
    stompClient.debug = f => f;
    stompClient.connect({}, onConnected, onError)
}

const onConnected = () => {
    let url = stompClient.ws._transport.url
    console.log("Url is: " + url);
    const urlParts = url.split("/")
    sessionId = urlParts[urlParts.length - 2]
    console.log("Your current session is: " + sessionId);

    stompClient.subscribe('/topic/public', onMessageReceived)
    stompClient.subscribe('/user/' + sessionId + "/direct", onDirectMessageReceived)

    let connectedMessage = JSON.stringify({
            sender: username,
            messageType: 'CONNECT'
        }
    )
    stompClient.send("/app/chat.newUser", {}, connectedMessage)
    console.log("Connected successfully")
}

const onError = error => {
    console.error("Connection error: " + error)
}

export function sendMessage(messageContent) {
    if (!(messageContent && stompClient))
        return

    const chatMessage = {
        sender: username,
        content: messageContent,
        messageType: 'CHAT',
        time: moment().calendar()
    }
    console.log("Send message " + chatMessage)
    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage))
}

export function sendDirectMessage(messageContent, receiver) {
    if (!(messageContent && receiver && stompClient))
        return

    const chatMessage = {
        sender: username,
        content: messageContent,
        messageType: 'CHAT',
        receiverId: receiver,
        time: moment().calendar()
    }
    console.log("Send direct message " + chatMessage)
    stompClient.send("/app/chat.send.direct", {}, JSON.stringify(chatMessage))
}

const onDirectMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    if (message.messageType === 'CONNECT') {
        console.warn(message.sender + " connected!")
    } else if (message.messageType === 'DISCONNECT') {
        console.warn(message.sender + " disconnected!")
    } else {
        console.log("DirectMessage from " + message.sender + " at " + message.time + ": " + message.content)
    }
}

const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    if (message.messageType === 'CONNECT') {
        console.warn(message.sender + " connected!")
    } else if (message.messageType === 'DISCONNECT') {
        console.warn(message.sender + " disconnected!")
    } else {
        console.log(message.sender + " at " + message.time + ": " + message.content)
    }
}