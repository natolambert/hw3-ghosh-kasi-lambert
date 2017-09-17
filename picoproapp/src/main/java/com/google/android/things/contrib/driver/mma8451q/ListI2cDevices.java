package com.google.android.things.contrib.driver.mma8451q;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;

import java.util.List;

/**
 * Created by ksk on 9/14/17.
 */

public class ListI2cDevices {
    private static final String TAG = ListI2cDevices.class.getSimpleName();

    public static void main(String[] args ) {
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getI2cBusList();
        if (deviceList.isEmpty()) {
            Log.i(TAG, "No I2C bus available on this device.");
        } else {
            Log.i(TAG, "List of available devices: " + deviceList);
        }
    }
}
