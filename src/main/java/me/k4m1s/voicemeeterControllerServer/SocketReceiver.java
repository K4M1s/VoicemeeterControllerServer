package me.k4m1s.voicemeeterControllerServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SocketReceiver extends Thread {

    private Socket socket;

    private List<Consumer<String>> callbacks = new ArrayList<>();

    SocketReceiver(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while (!socket.isClosed()) {
                message = reader.readLine();
                for(int i=0; i<callbacks.size(); i++) {
                    callbacks.get(i).accept(message);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Consumer<String> callback) {
        callbacks.add(callback);
    }
    
}
