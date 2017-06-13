package com.example.kamil.cukrowag.scale;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.nio.ByteBuffer;

/**
 * Created by kamil on 09.06.17.
 */
public class UsbScale extends Scale {
    UsbManager mUsbManager;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mReadEndpoint;
    private UsbInterface mUsbInterface;
    final private static int WEIGH_REPORT_SIZE = 6;

    public UsbScale(Context context, UsbDevice device) throws Exception {
        mUsbInterface = device.getInterface(0);
        mReadEndpoint = mUsbInterface.getEndpoint(0);
        mUsbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        if ( mUsbManager.hasPermission(device) == false ) {
            throw new Exception("No permission for device");
        }
        mConnection = mUsbManager.openDevice(device);
        if ( mConnection == null ) {
            throw new Exception("Could not open usb device");
        }
        if ( mConnection.claimInterface(mUsbInterface, true) == false ) {
            throw new Exception("Could not claim usb interface");
        }
        // discard first data
        rawread();
    }

    @Override
    public weightReport getWeightReport() throws Exception {
        return new weightReport( rawread() );
    }

    private byte[] rawread() throws Exception {
        final UsbRequest request = new UsbRequest();
        try {
            request.initialize(mConnection, mReadEndpoint);
            final ByteBuffer buf = ByteBuffer.allocate(WEIGH_REPORT_SIZE);
            if (!request.queue(buf, buf.limit())) {
                throw new Exception("Error queueing request.");
            }
            final UsbRequest response = mConnection.requestWait();
            if (response == null) {
                throw new Exception("Null response");
            }
            return buf.array();
        } finally {
            request.close();
        }
    }

}
