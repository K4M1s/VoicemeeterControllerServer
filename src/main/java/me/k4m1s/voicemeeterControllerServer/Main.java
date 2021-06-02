package me.k4m1s.voicemeeterControllerServer;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

public class Main {

    private SocketManager socketManager;
    private VoiceMeeterWrapper vmWrapper;
    private static Main instance;

    public static void main(String[] args) throws InterruptedException {
        try {
            Locale.setDefault(Locale.US);
            new Main();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Main() throws IOException {
        this.socketManager = new SocketManager();
        this.socketManager.start();

        this.vmWrapper = new VoiceMeeterWrapper();
        this.vmWrapper.start();

        // if (SystemTray.isSupported()) {

        // }
        try {
            java.awt.SystemTray systemTray = java.awt.SystemTray.getSystemTray();

            java.awt.Image image = ImageIO.read(getClass().getClassLoader().getResource("icon.png"));

            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            java.awt.PopupMenu popupMenu = new java.awt.PopupMenu();
    
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                systemTray.remove(trayIcon);
                System.exit(0);
            });
    
            popupMenu.add(exitItem);
            trayIcon.setPopupMenu(popupMenu);
            systemTray.add(trayIcon);
        } catch(Exception e) {
            e.printStackTrace();
        }
        


        Main.instance = this;
    }

    public VoiceMeeterWrapper getVMWrapper() {
        return this.vmWrapper;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }

    public static Main getInstance() { return Main.instance; }

}
