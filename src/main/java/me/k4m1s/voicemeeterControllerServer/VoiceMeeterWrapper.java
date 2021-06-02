package me.k4m1s.voicemeeterControllerServer;

import java.util.ArrayList;
import java.util.List;

import me.mattco.Voicemeeter;

public class VoiceMeeterWrapper extends Thread {

    private long lastDataTime = System.currentTimeMillis();
    private List<String> referenceValues = new ArrayList<>();

    public enum SliderType {
        HardwareSlider,
        SoftwareSlider,
        BusSlider
    }

    private HardwareSlider[] hSliders = new HardwareSlider[3];
    private SoftwareSlider[] sSliders = new SoftwareSlider[2];
    private BusSlider[] bSliders = new BusSlider[5];

    public void run() {
        registerShutdownHook();
        Voicemeeter.init();
        Voicemeeter.login();

        for(int i=0; i<3; i++)
            hSliders[i] = new HardwareSlider(i);
        for(int i=0; i<2; i++)
            sSliders[i] = new SoftwareSlider(i);
        for(int i=0; i<5; i++)
            bSliders[i] = new BusSlider(i);

        try {
            while (!Thread.interrupted()) {
                readVU();
                if (Voicemeeter.areParametersDirty()) {
                    readSlidersData();
                    if (System.currentTimeMillis() - lastDataTime > 500) {
                        for(String data : getChangedStripData()) {
                            Main.getInstance().getSocketManager().getSocketListener().sendData(data);
                        }
                    }
                } else {
                    referenceValues = getAllSlidersData();
                }
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSliderData(SliderType type, int ID, String data) {
        switch(type) {
            case HardwareSlider:
                hSliders[ID].setData(data);
                hSliders[ID].applyChanges();
                break;
            case SoftwareSlider:
                sSliders[ID].setData(data);
                sSliders[ID].applyChanges();
                break;
            case BusSlider:
                bSliders[ID].setData(data);
                bSliders[ID].applyChanges();
                break;
        }
    }

    public void setReceivedFromRemote() {
        lastDataTime = System.currentTimeMillis();
    }

    private void readSlidersData() {
        for(int i=0; i<3; i++)
            hSliders[i].readData();
        for(int i=0; i<2; i++)
            sSliders[i].readData();
        for(int i=0; i<5; i++) {
            bSliders[i].readData();
        }    
    }

    private void readVU() {
        for(int i=0; i<3; i++)
            hSliders[i].readVU();
        for(int i=0; i<2; i++)
            sSliders[i].readVU();
        for(int i=0; i<5; i++) {
            bSliders[i].readVU();
        }    
    }


    List<String> current;
    List<String> changed;
    private List<String> getChangedStripData() {
        current = this.getAllSlidersData();
        if (referenceValues.size() == 0) {
            return current;
        }

        changed = new ArrayList<>();
        for(int i=0; i<current.size(); i++) {
            if (!current.get(i).equals(referenceValues.get(i))) {
                changed.add(current.get(i));
            }
        }

        return changed;
    }

    /**
     * Get slider data string by passing its type and ID. Returns null if wrong type or ID passed.
     * @param type Slider type
     * @param ID Slider ID
     * @return Slider data.
     */
    public String getSliderData(SliderType type, int ID)
    {
        switch(type) {
            case HardwareSlider:
                return hSliders[ID].toString();
            case SoftwareSlider:
                return sSliders[ID].toString();
            case BusSlider:
                return bSliders[ID].toString();
            default:
                return null;
        }
    }
    List<String> data;
    public List<String> getAllSlidersData() {
        data = new ArrayList<>();
        for(int i=0; i<3; i++)
            data.add(hSliders[i].toString());
        for(int i=0; i<2; i++)
            data.add(sSliders[i].toString());
        for(int i=0; i<5; i++)
            data.add(bSliders[i].toString());
        return data;
    }

    List<String> dataVU;
    public List<String> getAllSlidersVU() {
        dataVU = new ArrayList<>();
        for(int i=0; i<3; i++)
            dataVU.add(hSliders[i].getLevels());
        for(int i=0; i<2; i++)
            dataVU.add(sSliders[i].getLevels());
        for(int i=0; i<5; i++)
            dataVU.add(bSliders[i].getLevels());
        return dataVU;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("In shutdown hook");
                Voicemeeter.logout();
            }
        }, "Shutdown-thread"));
    }

}
