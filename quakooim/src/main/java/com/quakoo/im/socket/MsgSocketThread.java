package com.quakoo.im.socket;

import android.util.Log;


import com.base.utils.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MsgSocketThread {

    private static final String TAG = "PushSocketThread";

    private Socket socket;

    private String ip;
    private String port;
    private InetSocketAddress isa;
    private DataOutputStream DOS;
    private DataInputStream DIS;
    public MsgSocketMap smMap;

    private static MsgSocketThread mInstance;

    public static MsgSocketThread getInstance() {
        if (mInstance == null) {
            Log.d(TAG, "getInstance: null ");
            mInstance = new MsgSocketThread();
        }
        return mInstance;
    }

    public boolean SocketStart(String myip, String myport, String type) {
        this.ip = myip;
        this.port = myport;
        socket = new Socket();
        isa = new InetSocketAddress(ip, Integer.parseInt(port));
        try {
            socket.connect(isa, 60 * 1000);
            LogUtil.d(TAG, "连接成功 " + socket);
            smMap = new MsgSocketMap();
            smMap.setSocket(type, socket);
            return true;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "连接超时 " + e.getMessage());
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "连接失败 " + e.getMessage());
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "连接失败 " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "连接失败 " + e.getMessage());
            return false;
        }
    }

    public DataOutputStream getDOS() throws IOException {
        DOS = new DataOutputStream(this.socket.getOutputStream());
        return DOS;
    }

    public DataInputStream getDIS() throws IOException {
        DIS = new DataInputStream(this.socket.getInputStream());
        return DIS;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setIP(String setip) {
        this.ip = setip;
    }

    public void setPort(String setport) {
        this.port = setport;
    }

    //本地服务器是否连接
    public boolean isConnected() {
        return socket.isConnected();
    }

    public void CloseSocket(String type) {
        smMap.removeMap(type);
    }

    public void AllClose() {
        smMap.clearMap();
    }
}