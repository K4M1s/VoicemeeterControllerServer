package me.k4m1s.voicemeeterControllerServer;

import java.util.regex.Pattern;

import me.mattco.Voicemeeter;

public class HardwareSlider {

    private int ID;

    private float comp;
    private float gate;

    private float gain;
    private float volume;
    private float volumeLeft;
    private float volumeRight;

    private boolean a1;
    private boolean a2;
    private boolean a3;

    private boolean b1;
    private boolean b2;

    private boolean mono;
    private boolean solo;
    private boolean mute;

    private String compAddress;
    private String gateAddress;
    private String gainAddress;
    private String a1Address;
    private String a2Address;
    private String a3Address;
    private String b1Address;
    private String b2Address;
    private String monoAddress;
    private String soloAddress;
    private String muteAddress;

    public HardwareSlider(int ID) {
        this.ID = ID;

        this.compAddress = String.format("Strip[%d].comp", this.ID);
        this.gateAddress = String.format("Strip[%d].gate", this.ID);
        this.gainAddress = String.format("Strip[%d].Gain", this.ID);
        this.a1Address = String.format("Strip[%d].A1", this.ID);
        this.a2Address = String.format("Strip[%d].A2", this.ID);
        this.a3Address = String.format("Strip[%d].A3", this.ID);
        this.b1Address = String.format("Strip[%d].B1", this.ID);
        this.b2Address = String.format("Strip[%d].B2", this.ID);
        this.monoAddress = String.format("Strip[%d].Mono", this.ID);
        this.soloAddress = String.format("Strip[%d].Solo", this.ID);
        this.muteAddress = String.format("Strip[%d].Mute", this.ID);

        readData();
    }

    public void readData() {
        this.comp = Voicemeeter.getParameterFloat(this.compAddress);
        this.gate = Voicemeeter.getParameterFloat(this.gateAddress);
        this.gain = Voicemeeter.getParameterFloat(this.gainAddress);

        volumeLeft = Voicemeeter.getLevel(1, (ID * 2));
        volumeRight = Voicemeeter.getLevel(1, (ID * 2) + 1);

        this.a1 = Voicemeeter.getParameterFloat(this.a1Address) > 0 ? true : false;
        this.a2 = Voicemeeter.getParameterFloat(this.a2Address) > 0 ? true : false;
        this.a3 = Voicemeeter.getParameterFloat(this.a3Address) > 0 ? true : false;
        this.b1 = Voicemeeter.getParameterFloat(this.b1Address) > 0 ? true : false;
        this.b2 = Voicemeeter.getParameterFloat(this.b2Address) > 0 ? true : false;
        this.mono = Voicemeeter.getParameterFloat(this.monoAddress) > 0 ? true : false;
        this.solo = Voicemeeter.getParameterFloat(this.soloAddress) > 0 ? true : false;
        this.mute = Voicemeeter.getParameterFloat(this.muteAddress) > 0 ? true : false;
    }

    public void readVU() {
        volumeLeft = Voicemeeter.getLevel(1, (ID * 2));
        volumeRight = Voicemeeter.getLevel(1, (ID * 2) + 1);
    }

    public void setData(String data) {
        String[] values = data.split(Pattern.quote(","));
        this.comp = Float.parseFloat(values[0]);
        this.gate = Float.parseFloat(values[1]);
        this.gain = Float.parseFloat(values[2]);
        // 3 is volume value
        this.a1 = Boolean.parseBoolean(values[4]);
        this.a2 = Boolean.parseBoolean(values[5]);
        this.a3 = Boolean.parseBoolean(values[6]);
        this.b1 = Boolean.parseBoolean(values[7]);
        this.b2 = Boolean.parseBoolean(values[8]);
        this.mono = Boolean.parseBoolean(values[9]);
        this.solo = Boolean.parseBoolean(values[10]);
        this.mute = Boolean.parseBoolean(values[11]);
    }

    public void applyChanges() {
        Voicemeeter.setParameterFloat(this.compAddress, this.comp);
        Voicemeeter.setParameterFloat(this.gateAddress, this.gate);
        Voicemeeter.setParameterFloat(this.gainAddress, this.gain);
        Voicemeeter.setParameterFloat(this.a1Address, this.a1?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.a2Address, this.a2?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.a3Address, this.a3?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.b1Address, this.b1?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.b2Address, this.b2?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.monoAddress, this.mono?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.soloAddress, this.solo?1.0f:0.0f);
        Voicemeeter.setParameterFloat(this.muteAddress, this.mute?1.0f:0.0f);
    }

    double lLevel;
    double rLevel;
    String outLevel;
    public String getLevels() {
        lLevel =  Math.max(-80, Math.min(12, 20 * Math.log10(volumeLeft)));
        rLevel =  Math.max(-80, Math.min(12, 20 * Math.log10(volumeRight)));
        outLevel = String.format("VHS%d:%.1f,%.1f", ID, lLevel, rLevel);
        return outLevel;
    }

    String stripValueString;
    public String toString() {
        stripValueString = String.format("HS%d:%.2f,%.2f,%.2f,%.2f,%b,%b,%b,%b,%b,%b,%b,%b", ID, comp, gate, gain, volume, a1, a2, a3, b1, b2, mono, solo, mute);
        return stripValueString;
    }
}
