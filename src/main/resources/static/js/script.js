let stompClient
let username

function connect(user) {
    console.log("Connect...")
    username = user
    const socket = new SockJS('/chat-example')
    stompClient = Stomp.over(socket)
    stompClient.debug = f => f;
    stompClient.connect({}, onConnected, onError)
}

const onConnected = () => {
    stompClient.subscribe('/topic/public', onMessageReceived)

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

function sendMessage(messageContent) {
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