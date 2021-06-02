package me.k4m1s.voicemeeterControllerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class SocketListenerThread extends Thread {

    private ServerSocket server;
    private List<Consumer<Socket>> onConnectCallbacks = new ArrayList<>();
    private HashMap<Socket, SocketCommunication> socketsCommunicationThread = new HashMap<>();

    public SocketListenerThread(ServerSocket server) {
        this.server = server;
    }

    public void run() {
        try {
            while (true) {
                Socket socket = server.accept();
                callOnSocketConnect(socket);
                socketsCommunicationThread.put(socket, new SocketCommunication(socket));
                socketsCommunicationThread.get(socket).start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        for(Entry<Socket, SocketCommunication> entry : socketsCommunicationThread.entrySet()) {
            entry.getValue().sendData(data);
        }
    }

    public void onSocketConnect(Consumer<Socket> callback) {
        onConnectCallbacks.add(callback);
    }

    private void callOnSocketConnect(Socket connectedSocket) {
        for (Consumer<Socket> socket : onConnectCallbacks) {
            socket.accept(connectedSocket);   
        }
    }

    public HashMap<Socket, SocketCommunication> getSocketsCommunicationThread() {
        return socketsCommunicationThread;
    }
}
