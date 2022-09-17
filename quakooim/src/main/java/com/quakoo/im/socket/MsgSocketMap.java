package com.quakoo.im.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MsgSocketMap {

    private static final String TAG = "ImSocketMap";

    public static Map<String, Socket> socketMap = new HashMap<String, Socket>();

    private Socket smSocket;

    public MsgSocketMap() {
    }

    public void setSocket(String str, Socket socket) {
        socketMap.put(str, socket);
    }
    
    public Socket getSocket(String key){
        smSocket = socketMap.get(key);
        return smSocket;
    }
    
    public Map<String,Socket> getMap(){
        return socketMap;
    }
    
    public void removeMap(String key){
        try {
            socketMap.get(key).close();
            socketMap.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearMap(){
        for (Iterator iterator = socketMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry elementEntry = (Map.Entry) iterator.next();
            Socket socketObject = (Socket) elementEntry.getValue();
            try {
                socketObject.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socketMap.clear();
    }
    
}
