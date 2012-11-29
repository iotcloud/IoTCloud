package cgl.iotcloud.samples.arducopter.mssg;

import cgl.iotcloud.core.message.data.ObjectDataMessage;

public class MRIMessage extends ObjectDataMessage {

    private long timeUsec;
    private int xacc;
    private int xgyro;
    private int xmag;
    private int yacc;
    private int ygyro;
    private int ymag;
    private int zacc;
    private int zgyro;
    private int zmag;

    public long getTimeUsec() {
        return timeUsec;
    }

    public void setTimeUsec(long timeUsec) {
        this.timeUsec = timeUsec;
    }

    public int getXacc() {
        return xacc;
    }

    public void setXacc(int xacc) {
        this.xacc = xacc;
    }

    public int getXgyro() {
        return xgyro;
    }

    public void setXgyro(int xgyro) {
        this.xgyro = xgyro;
    }

    public int getXmag() {
        return xmag;
    }

    public void setXmag(int xmag) {
        this.xmag = xmag;
    }

    public int getYacc() {
        return yacc;
    }

    public void setYacc(int yacc) {
        this.yacc = yacc;
    }

    public int getYgyro() {
        return ygyro;
    }

    public void setYgyro(int ygyro) {
        this.ygyro = ygyro;
    }

    public int getYmag() {
        return ymag;
    }

    public void setYmag(int ymag) {
        this.ymag = ymag;
    }

    public int getZacc() {
        return zacc;
    }

    public void setZacc(int zacc) {
        this.zacc = zacc;
    }

    public int getZgyro() {
        return zgyro;
    }

    public void setZgyro(int zgyro) {
        this.zgyro = zgyro;
    }

    public int getZmag() {
        return zmag;
    }

    public void setZmag(int zmag) {
        this.zmag = zmag;
    }
}
