import React, { ChangeEvent, useEffect, useState } from 'react';
import { useHistory } from 'react-router';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { addMessage, getMessagesAsync, Msg } from '../features/messages/messagesSlice';
import { getChannelsAsync } from '../features/session/sessionSlice';
import useWebSocket, { ReadyState } from 'react-use-websocket';
import './Chat.css';

export const Chat = () => {
    const sessionStatus = useAppSelector(state => state.session.status)
    const history = useHistory()
    const dispatch = useAppDispatch()

    const token = useAppSelector(state => state.session.token)

    const [selectedServer, setSelectedServer] = useState(0)
    const [selectedChannel, setSelectedChannel] = useState(0)

    const [input, setInput] = useState('')

    const servers = useAppSelector(state => state.session.servers)
    const msgs = useAppSelector(state => state.messages)

    const href = window.location.href
    const socketUrl = `ws${href.substring(4, href.length-4)}connect`;
    const {
        sendMessage,
        lastMessage,
        readyState,
    } = useWebSocket(socketUrl);

    useEffect(() => {
        if(servers.length == 0)
            return;

        const serverId = servers[selectedServer].serverId

        sendMessage(JSON.stringify({
            type: "change",
            msg: {
                serverId
            }
        }))
    }, [selectedServer])

    useEffect(() => {
        if(servers.length > 0 && readyState == ReadyState.OPEN){
            sendMessage(JSON.stringify({
                token,
                serverId: servers[selectedServer].serverId
            }))
        }
    }, [servers, readyState])

    useEffect(() => {
        if(lastMessage != null){
            const msg = JSON.parse(lastMessage?.data)
            if(msg.hasOwnProperty('status')){
                console.log('ready')
            }else{
                dispatch(addMessage({channelId: msg.channelId, msg}))
            }
        }
    }, [lastMessage])

    useEffect(() => {
        dispatch(getChannelsAsync())
    }, [])

    useEffect(() => {
        console.log('effect')
        if(servers.length == 0)
            return;

        const channelId = servers[selectedServer].channels[selectedChannel].channelId
        if(!msgs.hasOwnProperty(channelId) || msgs[channelId].length == 0){
            dispatch(getMessagesAsync({channelId, timestamp: Date.now(), amount: 20}))
        }
    })

    if(servers.length == 0)
        return (<div>Loading</div>)

    const selectedServerChannels = servers[selectedServer].channels

    var messages: Msg[] = []
    var id = servers[selectedServer].channels[selectedChannel].channelId
    if(msgs.hasOwnProperty(id))
        messages = msgs[id.toString()]
    const selectedChannelMsgs = messages

    const selectServer = (id: number) => () => {
        const idx = servers.findIndex(server => server.serverId == id)
        setSelectedServer(idx)
        setSelectedChannel(0)
    }

    const selectChannel = (id: number) => () => {
        const idx = selectedServerChannels.findIndex(channel => channel.channelId == id)
        setSelectedChannel(idx)
    }

    const inputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault()
        setInput(event.target.value)
    }

    const send = () => {
        const channelId = servers[selectedServer].channels[selectedChannel].channelId

        sendMessage(JSON.stringify({
            type: "send",
            msg: {
                timestamp: Date.now(),
                text: input,
                channelId
            }
        }))
        setInput('')
    }

    const formatTimestamp = (timestamp: number) => {
        var date = new Date(timestamp);
        var hours = date.getHours();
        var minutes = "0" + date.getMinutes();
        var seconds = "0" + date.getSeconds();
        var year = date.getFullYear();
        var month = date.getMonth();
        var day = date.getDay()
        return day + '/' + month + '/' + year + ' ' + hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
    }

    if(sessionStatus != 'active')
        history.push('/login')

    const selectedServerId = servers[selectedServer].serverId
    const selectedChannelId = servers[selectedServer].channels[selectedChannel].channelId

    return (
        <div>
            <div className={"serverPanel"}>
                {servers.map(server => 
                    <div key={server.serverId}>
                        <p>{server.name}</p>
                        <button onClick={selectServer(server.serverId)}>{(server.serverId == selectedServerId) ? "" : "SELECT"}</button>
                    </div>
                )}
            </div>
            <div className={"channelsPanel"}>
                {selectedServerChannels.map(channel => 
                    <div key={channel.channelId}>
                        <p>{channel.name}</p>
                        <button onClick={selectChannel(channel.channelId)}>{(channel.channelId == selectedChannelId) ? "" : "SELECT"}</button>
                    </div>
                )}
            </div>
            <div className={"chat"}>
                {selectedChannelMsgs.map(msg => 
                    <div key={msg.userId * msg.timestamp}>
                        <p>[{formatTimestamp(msg.timestamp)}] {msg.username}: {msg.text}</p>
                    </div>
                )}
                <form>
                    <p>Message:</p>
                    <input
                    type='text'
                    onChange={inputChange}
                    />
                </form>
                <button onClick={send}>
                    SEND
                </button>
            </div>
        </div>
    )
}