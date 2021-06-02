package me.k4m1s.voicemeeterControllerServer;

import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import me.k4m1s.voicemeeterControllerServer.VoiceMeeterWrapper.SliderType;

public class SocketCommunication extends Thread {

    private long lastPingTime = System.currentTimeMillis();

    private SocketReceiver receiver;
    private SocketTransmitter transmitter;


    private Timer timer;

    public SocketCommunication(Socket socket) {

        timer = new Timer();

        receiver = new SocketReceiver(socket);
        receiver.start();

        transmitter = new SocketTransmitter(socket);
        transmitter.start();

        sendInitialData();
    }

    public void run() {
        receiver.onMessage(message -> {
            if (message.equals("ping")) {
                this.sendData("pong");
                lastPingTime = System.currentTimeMillis();
            } else {
                System.out.println(message);
                parseReceivedData(message);
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                List<String> vuValues = Main.getInstance().getVMWrapper().getAllSlidersVU();
                vuValues.forEach(value -> {
                    sendData(value);
                });
            }
        }, 0, 1000/30L);

    }

    private String[] splited;
    private String type;
    private int id;

    private void parseReceivedData(String data) {
        splited = data.split(Pattern.quote(":"));
        type = splited[0].substring(0, 2);
        id = Integer.parseInt(splited[0].substring(2));
        Main.getInstance().getVMWrapper().setReceivedFromRemote();
        switch(type) {
            case "HS":
                Main.getInstance().getVMWrapper().setSliderData(SliderType.HardwareSlider, id, splited[1]);
                break;
            case "SS":
                Main.getInstance().getVMWrapper().setSliderData(SliderType.SoftwareSlider, id, splited[1]);
                break;
            case "BS":
                Main.getInstance().getVMWrapper().setSliderData(SliderType.BusSlider, id, splited[1]);
                break;
        }
    }

    public void sendData(String data) {
        try {
            transmitter.getQueue().put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getLastPingTime() {
        return this.lastPingTime;
    }

    Stack<String> syncData = new Stack<>();

    public void sendSyncData() {
        syncData.addAll(Main.getInstance().getVMWrapper().getAllSlidersData());
        while(!syncData.empty()) {
            sendData(syncData.pop());
        }
    }

    private void sendInitialData() {
        List<String> data = Main.getInstance().getVMWrapper().getAllSlidersData();
        sendData("INITIALSTART");
        for(int i=0; i<data.size(); i++) {
            sendData(data.get(i));
        }
        sendData("INITIALEND");
    }

}
