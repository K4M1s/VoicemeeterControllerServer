package me.k4m1s.voicemeeterControllerServer;

import java.util.regex.Pattern;

import me.mattco.Voicemeeter;

public class BusSlider {

    private int ID;

    private float gain;
    private float volume;
    private float volumeLeft;
    private float volumeRight;

    private boolean mono;
    private boolean eq;
    private boolean mute;

    private String gainAddress;
    private String monoAddress;
    private String eqAddress;
    private String muteAddress;

    public BusSlider(int ID) {
        this.ID = ID;

        this.gainAddress = String.format("Bus[%d].Gain", this.ID);
        this.monoAddress = String.format("Bus[%d].Mono", this.ID);
        this.eqAddress = String.format("Bus[%d].EQ.on", this.ID);
        this.muteAddress = String.format("Bus[%d].Mute", this.ID);
        readData();
    }

    public void readData() {
        gain = Voicemeeter.getParameterFloat(this.gainAddress);
        volumeLeft = Voicemeeter.getLevel(3, (ID * 8));
        volumeRight = Voicemeeter.getLevel(3, (ID * 8) + 1);
        mono = Voicemeeter.getParameterFloat(this.monoAddress) > 0 ? true : false;
        eq = Voicemeeter.getParameterFloat(this.eqAddress) > 0 ? true : false;
        mute = Voicemeeter.getParameterFloat(this.muteAddress) > 0 ? true : false;
    }

    public void readVU() {
        volumeLeft = Voicemeeter.getLevel(3, (ID * 8));
        volumeRight = Voicemeeter.getLevel(3, (ID * 8) + 1);
    }

    public void setData(String data) {
        String[] values = data.split(Pattern.quote(","));
        gain = Float.parseFloat(values[0]);
        // 1 is volume value
        mono = Boolean.parseBoolean(values[2]);
        eq = Boolean.parseBoolean(values[3]);
        mute = Boolean.parseBoolean(values[4]);
    }

    public void applyChanges() {
        Voicemeeter.setParameterFloat(this.gainAddress, this.gain);
        Voicemeeter.setParameterFloat(this.monoAddress, this.mono?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.eqAddress, this.eq?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.muteAddress, this.mute?1.0f:0.0f);
    }

    double lLevel;
    double rLevel;
    String outLevel;
    public String getLevels() {
        lLevel =  Math.max(-80, Math.min(12, 20 * Math.log10(volumeLeft)));
        rLevel =  Math.max(-80, Math.min(12, 20 * Math.log10(volumeRight)));
        outLevel = String.format("VBS%d:%.1f,%.1f", ID, lLevel, rLevel);
        return outLevel;
    }

    String busValueString;
    public String toString() {
        busValueString = String.format("BS%d:%.2f,%.2f,%b,%b,%b", ID, gain, volume, mono, eq, mute);
        return busValueString;
    }
}
