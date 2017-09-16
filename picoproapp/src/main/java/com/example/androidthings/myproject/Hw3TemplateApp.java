package com.example.androidthings.myproject;

import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;

import com.google.android.things.contrib.driver.mma8451q.L3GD20;

/**
 * HW3 Template
 * Created by bjoern on 9/12/17.
 * Wiring:
 * USB-Serial Cable:
 *   GND to GND on IDD Hat
 *   Orange (Tx) to UART6 RXD on IDD Hat
 *   Yellow (Rx) to UART6 TXD on IDD Hat
 * Accelerometer:
 *   Vin to 3V3 on IDD Hat
 *   GND to GND on IDD Hat
 *   SCL to SCL on IDD Hat
 *   SDA to SDA on IDD Hat
 * Analog sensors:
 *   Middle of voltage divider to Analog A0..A3 on IDD Hat
 */

public class Hw3TemplateApp extends SimplePicoPro {

    L3GD20 accelerometer;

    float[] xyz = {0.f,0.f,0.f}; //store X,Y,Z acceleration of MMA8451 accelerometer here [units: G]
    float[] xyz_angle = {0.f, 0.f, 0.f};
    double time =0, old_time = 0;
    float a0,a1,a2,a3; //store analog readings from ADS1015 ADC here [units: V]

    public static final double DISCOUNT_FACTOR = 0.8;
    public static final double X_DRIFT_SEC = -.0245;
    public static final double Y_DRIFT_SEC = .0281;
    public static final double Z_DRIFT_SEC = -.01716;

    float[] weights = {0.5f, .25f, .125f, .0625f, .03125f, 0.015625f, 0.0078125f, 0.00390625f, 0.001953125f, 0.0009765625f};
    LinkedList<Float> dataValuesX = new LinkedList<Float>();
    LinkedList<Float> dataValuesY = new LinkedList<Float>();
    LinkedList<Float> dataValuesZ = new LinkedList<Float>();

    public void setup() {
        println("hello");
        // Initialize the serial port for communicating to a PC
        uartInit(UART6,115200);

        // Initialize the Analog-to-Digital converter on the HAT
        //analogInit(); //need to call this first before calling analogRead()

        // Initialize the MMQ8451 Accelerometer
        try {
            accelerometer = new L3GD20("I2C1");
            accelerometer.setMode(L3GD20.MODE_STANDBY); // Sets standby
            delay(50);
            accelerometer.setMode(L3GD20.MODE_ACTIVE);  // Sets on

            // Init weight values
            for (int _ = 0; _ < 10; _++) {
                dataValuesX.addFirst(0.0f);
                dataValuesY.addFirst(0.0f);
                dataValuesZ.addFirst(0.0f);


            }





        } catch (IOException e) {
            Log.e("HW3Template","setup",e);
        }
    }

    public void loop() {


        // read all analog channels and print to UART
        //a0 = analogRead(A0);
//        a1 = analogRead(A1);
//        a2 = analogRead(A2);
//        a3 = analogRead(A3);
//        println(UART6,"A0: "+a0+"   A1: "+a1+"   A2: "+a2+"   A3: "+a3); // this goes to the Serial port
//        println("A0: "+a0+"   A1: "+a1+"   A2: "+a2+"   A3: "+a3); // this goes to the Android Monitor in Android Studio
//        println(UART6,"A0: " + a0);
//        println("A0: " + a0);

        // read I2C accelerometer and print to UART
        try {
            println("try");
            if (time == 0) {
                time = System.currentTimeMillis();
                old_time = time;
            }
            else{
                old_time = time;
                time = System.currentTimeMillis();
            }
//            println("deltaTime: " + (time-old_time));
//            println("" + System.currentTimeMillis());
            xyz = accelerometer.readSample();

            // Weighted value of X, Y, Z
            dataValuesX.addFirst(xyz[0]);
            dataValuesY.addFirst(xyz[1]);
            dataValuesZ.addFirst(xyz[2]);

            /*
            dataValuesX.removeLast();
            dataValuesY.removeLast();
            dataValuesZ.removeLast();
            */

            xyz_angle[0] = 0.0f;
            xyz_angle[1] = 0.0f;
            xyz_angle[2] = 0.0f;

            for (int i = 0; i < 10; i++) {
                xyz_angle[0] += weights[i]*dataValuesX.get(i);
                xyz_angle[1] += weights[i]*dataValuesY.get(i);
                xyz_angle[2] += weights[i]*dataValuesZ.get(i);
            }

            /*
            if ((xyz_angle[0] + xyz[0]*(time-old_time)/1000 <= 50) && (xyz_angle[0] + xyz[0]*(time-old_time)/1000 >= -50))
                if (xyz[0] < 0) {
                    // Add back some value
                    xyz_angle[0] += xyz[0]*(time-old_time)/1000*2 + Math.min(Math.abs(DISCOUNT_FACTOR*xyz[0]*(time-old_time)/1000*2),
                            Math.abs(X_DRIFT_SEC));
                } else {
                    // subtract some value
                    xyz_angle[0] += xyz[0]*(time-old_time)/1000*2 - Math.min(Math.abs(DISCOUNT_FACTOR*xyz[0]*(time-old_time)/1000*2),
                            Math.abs(X_DRIFT_SEC));
                }
            if ((xyz_angle[1] + xyz[1]*(time-old_time)/1000 <= 50) && (xyz_angle[1] + xyz[1]*(time-old_time)/1000 >= -50))
                if (xyz[1] < 0) {
                    // Add back some value
                    xyz_angle[1] += xyz[1]*(time-old_time)/1000*2 + Math.min(Math.abs(DISCOUNT_FACTOR*xyz[1]*(time-old_time)/1000*2),
                            Math.abs(Y_DRIFT_SEC));
                } else {
                    // subtract some value
                    xyz_angle[1] += xyz[1]*(time-old_time)/1000*2 - Math.min(Math.abs(DISCOUNT_FACTOR*xyz[1]*(time-old_time)/1000*2),
                            Math.abs(Y_DRIFT_SEC));
                }
                if ((xyz_angle[2] + xyz[2]*(time-old_time)/1000 <= 50) && (xyz_angle[2] + xyz[2]*(time-old_time)/1000 >= -50)) {
                    if (xyz[2] < 0) {
                        // Add back some value
                        xyz_angle[2] += xyz[2]*(time-old_time)/1000*2 + Math.min(Math.abs(DISCOUNT_FACTOR*xyz[2]*(time-old_time)/1000*2),
                                Math.abs(Z_DRIFT_SEC));
                    } else {
                        // subtract some value
                        xyz_angle[2] += xyz[2]*(time-old_time)/1000*2 - Math.min(Math.abs(DISCOUNT_FACTOR*xyz[2]*(time-old_time)/1000*2),
                                Math.abs(Z_DRIFT_SEC));
                    }
                }

            */


            // xyz_angle[0] = X
            // xyz_angle[1] = Y
            // xyz_angle[2] = Z

            println(UART6,xyz_angle[0]/50+","+xyz_angle[1]/50+","+xyz_angle[2]/50 + "," + a0);
            println("X: "+xyz_angle[0]/50+", Y: "+xyz_angle[1]/50+", Z: "+xyz_angle[2]/50);


            //use this line instead for unlabeled numbers separated by tabs that work with Arduino's SerialPlotter:
            //println(UART6,xyz[0]+"\t"+xyz[1]+"\t"+xyz[2]); // this goes to the Serial port

        } catch (IOException e) {
            Log.e("HW3Template","loop",e);
        }

        delay(100);

    }
}
