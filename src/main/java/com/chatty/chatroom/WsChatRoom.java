package com.chatty.chatroom;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/chatroom/{nickname}")
public class WsChatRoom {
    private static CopyOnWriteArraySet<WsChatRoom> wsChatRooms = new CopyOnWriteArraySet<>();

    private Session session;


    private String nickname;

    @OnOpen
    public void onOpen(Session session, @PathParam("nickname")String nickname){
        this.nickname = nickname;
        this.session = session;
        wsChatRooms.add(this);
        this.session.getAsyncRemote().sendText("已连接，当前在线人数为" + wsChatRooms.size());
    }

    @OnClose
    public void onClose() {
        wsChatRooms.remove(this);
        broadcast(nickname + "离开");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        broadcast(nickname + ":"+ message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void broadcast(String message){
        for(WsChatRoom w : wsChatRooms){
            w.session.getAsyncRemote().sendText(message);
        }
    }

}
