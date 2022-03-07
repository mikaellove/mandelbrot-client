package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {

    private final InetAddress ip;
    private final int port;
    private DataInputStream serverDis;
    private DataOutputStream serverDos;

    public SocketClient(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connectSocketToServer(){
        try {
            Socket socket = new Socket(ip,port);

            serverDis = new DataInputStream(socket.getInputStream());
            serverDos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
