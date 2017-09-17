package com.google.android.things.contrib.driver.mma8451q;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by nato on 9/13/17.
 */

public class L3GD20 implements AutoCloseable {
    private static final String TAG = L3GD20.class.getSimpleName();

    /**
     * I2C slave address of the L3GD20.
     */
    public static final int L3GD20_ADDRESS = 0x6B; // if A is GND, its 0x1C

    static final int L3GD20_POLL_TIMEOUT = 100; // Max # of read attempts
    static final int L3GD20_ID = 0xD4;
    static final int L3GD20H_ID = 0xD7;

    // Sesitivity values from the mechanical characteristics in the datasheet.
    static final float GYRO_SENSITIVITY_250DPS  = 0.00875F;
    static final float GYRO_SENSITIVITY_500DPS  = 0.0175F;
    static final float GYRO_SENSITIVITY_2000DPS = 0.070F;

    // Defines all the registers
    private static final int GYRO_REGISTER_WHO_AM_I            = 0x0F;   // 11010100   r
    private static final int GYRO_REGISTER_CTRL_REG1           = 0x20;   // 00000111   rw
    private static final int GYRO_REGISTER_CTRL_REG2           = 0x21;   // 00000000   rw
    private static final int GYRO_REGISTER_CTRL_REG3           = 0x22;   // 00000000   rw
    private static final int GYRO_REGISTER_CTRL_REG4           = 0x23;   // 00000000   rw
    private static final int GYRO_REGISTER_CTRL_REG5           = 0x24;   // 00000000   rw
    private static final int GYRO_REGISTER_REFERENCE           = 0x25;   // 00000000   rw
    private static final int GYRO_REGISTER_OUT_TEMP            = 0x26;   //            r
    private static final int GYRO_REGISTER_STATUS_REG          = 0x27;   //            r
    private static final int GYRO_REGISTER_OUT_X_L             = 0x28;   //            r
    private static final int GYRO_REGISTER_OUT_X_H             = 0x29;   //            r
    private static final int GYRO_REGISTER_OUT_Y_L             = 0x2A;   //            r
    private static final int GYRO_REGISTER_OUT_Y_H             = 0x2B;   //            r
    private static final int GYRO_REGISTER_OUT_Z_L             = 0x2C;   //            r
    private static final int GYRO_REGISTER_OUT_Z_H             = 0x2D;   //            r
    private static final int GYRO_REGISTER_FIFO_CTRL_REG       = 0x2E;   // 00000000   rw
    private static final int GYRO_REGISTER_FIFO_SRC_REG        = 0x2F;   //            r
    private static final int GYRO_REGISTER_INT1_CFG            = 0x30;   // 00000000   rw
    private static final int GYRO_REGISTER_INT1_SRC            = 0x31;   //            r
    private static final int GYRO_REGISTER_TSH_XH              = 0x32;   // 00000000   rw
    private static final int GYRO_REGISTER_TSH_XL              = 0x33;   // 00000000   rw
    private static final int GYRO_REGISTER_TSH_YH              = 0x34;   // 00000000   rw
    private static final int GYRO_REGISTER_TSH_YL              = 0x35;   // 00000000   rw
    private static final int GYRO_REGISTER_TSH_ZH              = 0x36;   // 00000000   rw
    private static final int GYRO_REGISTER_TSH_ZL              = 0x37;   // 00000000   rw
    private static final int GYRO_REGISTER_INT1_DURATION       = 0x38;    // 00000000   rw


    private static final int GYRO_RANGE_250DPS  = 250;
    private static final int GYRO_RANGE_500DPS  = 500;
    private static final int GYRO_RANGE_2000DPS = 2000;

    // Define range
    private static final int mRange = GYRO_RANGE_250DPS;


    private class gyroRawData_s {
        int x;
        int y;
        int z;
    }

    private static final int REG_OUT_X_MSB = 0x01;



      /* Set CTRL_REG1 (0x20)
   ====================================================================
   BIT  Symbol    Description                                   Default
   ---  ------    --------------------------------------------- -------
   7-6  DR1/0     Output data rate                                   00
   5-4  BW1/0     Bandwidth selection                                00
     3  PD        0 = Power-down mode, 1 = normal/sleep mode          0
     2  ZEN       Z-axis enable (0 = disabled, 1 = enabled)           1
     1  YEN       Y-axis enable (0 = disabled, 1 = enabled)           1
     0  XEN       X-axis enable (0 = disabled, 1 = enabled)           1

       write8(GYRO_REGISTER_CTRL_REG1, 0x00);   Clear


     */



    private I2cDevice mDevice;

    /**
     * Power mode.
     */
    public @interface Mode {}

    public static final int MODE_STANDBY = 0x00; // STANDBY //see table 61 in datahseet
    public static final int MODE_ACTIVE = 0x0F; // ACTIVE




    /**
     * Create a new MMA7660FC driver connected to the given I2C bus.
     * @param bus
     * @throws IOException
     */
    public L3GD20(String bus) throws IOException {
        PeripheralManagerService pioService = new PeripheralManagerService();
        I2cDevice device = pioService.openI2cDevice(bus, L3GD20_ADDRESS);
        try {
            connect(device);
        } catch (IOException|RuntimeException e) {
            try {
                close();
            } catch (IOException|RuntimeException ignored) {
            }
            throw e;
        }
    }

    /**
     *  LEAVE THIS OR SOMETHING SIMILAR
     *
     * Create a new MMA7660FC driver connected to the given I2C device.
     * @param device
     * @throws IOException
     */
    /*package*/ L3GD20(I2cDevice device) throws IOException {
        connect(device);
    }

    private void connect(I2cDevice device) throws IOException {
        if (mDevice != null) {
            throw new IllegalStateException("device already connected");
        }
        mDevice = device;
        //setSamplingRate(RATE_120HZ);
    }


    /**
     * Close the driver and the underlying device.
     */
    @Override
    public void close() throws IOException {
        if (mDevice != null) {
            try {
                mDevice.close();
            } finally {
                mDevice = null;
            }
        }
    }

    /**
     * Set current power mode.
     * @param mode
     * @throws IOException
     * @throws IllegalStateException
     */
    public void setMode(int mode) throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("device not connected");
        }
        mDevice.writeRegByte(GYRO_REGISTER_CTRL_REG1, (byte) mode);
    }

    /**
     * Read an accelerometer sample.
     * @return acceleration over xyz axis in G.
     * @throws IOException
     * @throws IllegalStateException
     */
    public float[] readSample() throws IOException, IllegalStateException {
        if (mDevice == null) {
            throw new IllegalStateException("device not connected");
        }
//        byte[] sample = new byte[6];
//        mDevice.readRegBuffer(REG_OUT_X_MSB,sample,6);
//
//        byte xlo = sample[0]; //
//        byte xhi = sample[1];
//        byte ylo = sample[2];
//        byte yhi = sample[3];
//        byte zlo = sample[4];
//        byte zhi = sample[5];

        byte xlo = mDevice.readRegByte(GYRO_REGISTER_OUT_X_L);   //            r
        byte xhi = mDevice.readRegByte(GYRO_REGISTER_OUT_X_H);   //            r
        byte ylo = mDevice.readRegByte(GYRO_REGISTER_OUT_Y_L);   //            r
        byte yhi = mDevice.readRegByte(GYRO_REGISTER_OUT_Y_H);   //            r
        byte zlo = mDevice.readRegByte(GYRO_REGISTER_OUT_Z_L);   //            r
        byte zhi = mDevice.readRegByte(GYRO_REGISTER_OUT_Z_H);   //            r

        // Shift values to create properly formed integer (low byte first)
        int x = (xlo | (xhi << 8));
        int y = (ylo | (yhi << 8));
        int z = (zlo | (zhi << 8));

        // Default Sensitivity = 256 DPS
        float multiplier = GYRO_SENSITIVITY_250DPS;

        if (mRange == GYRO_RANGE_250DPS) {
            multiplier = GYRO_SENSITIVITY_250DPS;
        }else if (mRange == GYRO_RANGE_500DPS) {
            multiplier = GYRO_SENSITIVITY_500DPS;
        }else if (mRange == GYRO_RANGE_2000DPS) {
            multiplier = GYRO_SENSITIVITY_2000DPS;
        }

        return new float[] {
                ((float)x) * multiplier,
                ((float)y) * multiplier,
                ((float)z) * multiplier
        };
    }
}

