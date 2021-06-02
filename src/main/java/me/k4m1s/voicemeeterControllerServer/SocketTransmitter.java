package me.k4m1s.voicemeeterControllerServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SocketTransmitter extends Thread {

    private Socket socket;

    private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(30);

    SocketTransmitter(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (!socket.isClosed()) {
                out.println(queue.take());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<String> getQueue() {
        return this.queue;
    }
    
}
