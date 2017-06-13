package com.example.kamil.cukrowag.scale;

/**
 * Created by kamil on 09.06.17.
 */
public class weightReport {
    //
    // **UNITS** is an array of all the unit abbreviations as set forth by *HID
    // Point of Sale Usage Tables*, version 1.02, by the USB Implementers' Forum.
    // The list is laid out so that the unit code returned by the scale is the
    // index of its corresponding string.
    //
    final static String UNITS[] = {
            "units",        // unknown unit
            "mg",           // milligram
            "g",            // gram
            "kg",           // kilogram
            "cd",           // carat
            "taels",        // lian
            "gr",           // grain
            "dwt",          // pennyweight
            "tonnes",       // metric tons
            "tons",         // avoir ton
            "ozt",          // troy ounce
            "oz",           // ounce
            "lbs"           // pound
    };
    public byte status;
    public byte unit;
    public double weight; // in grams

    public byte getStatus() {
        return status;
    }

    public double getWeight() throws Exception {
        if ( allOk() ) {
            return weight;
        } else {
            throw new Exception("weightReport status: "+this.statusMessage());
        }
    }

    weightReport(byte[] dat) throws Exception {
        //
        // Gently rip apart the scale's data packet according to *HID Point of Sale
        // Usage Tables*.
        //
        byte report = dat[0];
        status = dat[1];
        unit   = dat[2];
        // Accoring to the docs, scaling applied to the data as a base ten exponent
        byte expt   = dat[3];
        // convert to machine order at all times
        weight = (double)((int)( ((int)(dat[5]&0xff)) << 8 | ((int)(dat[4]&0xff)) ));
        // since the expt is signed, we do not need no trickery
        weight = weight * Math.pow(10, expt);

        if(report != 0x03 && report != 0x04) {
            throw new Exception("Error reading scale data\n");
        }

        switch(getUnit()) {
            case "mg": weight *= 1000; unit=2; break;
            case "g": break;
            case "kg": weight /= 1000; unit=2; break;
            case "tonnes": weight /= 1000000; unit=2; break;
        }

        weight *= minusSign();
    }

    public boolean allOk() {
        switch(status) {
            case 0x01:
                return false;
            case 0x02:
                return true;
            case 0x03:
                return false;
            case 0x04:
                return true;
            case 0x05:
                return true;
            case 0x06:
                return true;
            case 0x07:
                return false;
            case 0x08:
                return false;
            default:
                return false;
        }
    }

    public int minusSign() {
        switch(status) {
            case 0x01:
                return 0;
            case 0x02:
                return 0;
            case 0x03:
                return 1;
            case 0x04:
                return 1;
            case 0x05:
                return -1;
            case 0x06:
                return 0;
            case 0x07:
                return 0;
            case 0x08:
                return 0;
            default:
                return 0;
        }
    }

    public final String getUnit() {
        return UNITS[unit];
    }

    public String statusMessage() {
        switch(status) {
            case 0x01:
                return "Scale reports Fault\n";
            case 0x02:
                return "Scale is zero'd...: "+String.valueOf(weight)+" "+UNITS[unit]+"\n";
            case 0x03:
                return "Weighing...\n";
            //
            // 0x04 is the only final, successful status, and it indicates that we
            // have a finalized weight ready to print. Here is where we make use of
            // the `UNITS` lookup table for unit names.
            //
            case 0x04:
                return String.valueOf(weight)+" "+UNITS[unit]+"\n";
            case 0x05:
                return "Scale reports Under Zero: -"+ String.valueOf(weight)+" "+UNITS[unit]+"\n";
            case 0x06:
                return "Scale reports Over Weight\n";
            case 0x07:
                return "Scale reports Calibration Needed\n";
            case 0x08:
                return "Scale reports Re-zeroing Needed!\n";
            default:
                return "Unknown status code: "+String.valueOf(status)+"\n";
        }
    }
}
