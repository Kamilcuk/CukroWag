package com.example.kamil.cukrowag.activity;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by kamil on 11.06.17.
 */

public class HandleSerializable {
    static boolean canLoad(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            if (fis == null) {
                return false;
            }
            ObjectInputStream is = new ObjectInputStream(fis);
            if (is == null) {
                fis.close();
                return false;
            }
            fis.close();
            is.close();
        } catch(IOException ioe) {
            return false;
        }
        return true;
    }
    static void save(Context context, Serializable obj, String fileName) throws IOException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        if ( fos == null ) {
            throw new IOException("fos == null");
        }
        ObjectOutputStream os = new ObjectOutputStream(fos);
        if ( os == null ) {
            fos.close();
            throw new IOException("fos == null");
        }
        os.writeObject(obj);
        os.close();
        fos.close();
    }
    static Object load(Context context, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        if ( fis == null ) {
            throw new IOException("fis == null");
        }
        ObjectInputStream is = new ObjectInputStream(fis);
        if ( is == null ) {
            fis.close();
            throw new IOException("is == null");
        }
        Object obj = (Object)is.readObject();
        is.close();
        fis.close();
        if ( obj == null ) {
            throw new IOException("obj = null");
        }
        return obj;
    }
}
