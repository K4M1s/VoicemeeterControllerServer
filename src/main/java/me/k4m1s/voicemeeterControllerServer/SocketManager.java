package me.k4m1s.voicemeeterControllerServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class SocketManager extends Thread {
    private ServerSocket server;
    private SocketListenerThread listener;

    public SocketManager() throws IOException {
        this.server = new ServerSocket(22587);
        System.out.println("Server created!");
    }

    public void run() {
        listener = new SocketListenerThread(this.server);
        this.listenToSocketConnections();
        listener.start();
        new Thread(()-> {
            try {
                Set<Entry<Socket, SocketCommunication>> setOfEntries;
                Iterator<Entry<Socket, SocketCommunication>> iterator;
                Entry<Socket, SocketCommunication> entry;
                while(true) {
                    setOfEntries = listener.getSocketsCommunicationThread().entrySet();
                    iterator = setOfEntries.iterator();
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        if (System.currentTimeMillis() - entry.getValue().getLastPingTime() > 2500) {
                            try {
                                entry.getKey().close();
                                iterator.remove();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Thread.sleep(500);
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Listening for connections!");
    }

    private void listenToSocketConnections() {
        this.listener.onSocketConnect((socket) -> {
            System.out.println("New socket connected: " + socket.getInetAddress());
        });
    }
    
    public SocketListenerThread getSocketListener() {
        return this.listener;
    }
}
