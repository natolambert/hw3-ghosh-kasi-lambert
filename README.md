HW3 - Space Shooter Game (Ghosh - Kasi - Lambert)
=====================================
![Alt text](/Photos/system_image1.JPG?raw=true "Optional Title")

A video demonstration of this project can be found at: https://www.youtube.com/watch?v=Z5vldrjRAMI
Those coming to learn on the Android Things Platform will get the most use out of the L3GD20 driver which I ported from the mma8451q acceleromoter driver. Code structure was from the Arduino driver found online.

Pre-requisites
--------------
- Unity engine
- Android Things compatible board
- Android Studio 2.2+
- Materials 

File Notes:
-----------
- Basic Pico Pro App Files
- Hw3TemplateApp.java: holds the state logic for reading sensor values and communicating via serial
- SpaceShooter_IDD: Unity game files modified to read serial from the picopro
- L3GD20.java: Driver for L3GD20 3-axis gyro

Team Roles:
-----------
- Ashi Ghosh: Hardware construction, serial integration
- Kaushik Kasi: Unity game integration, main state logic
- Nathan Lambert: Gyro driver, electrical construction
- All: Testing

Introduction
============

The instructions for this assignment were to construct a playable game or musical instrument with the PicoPro development board and provided sensors / serial cable. We decided on implementing a game for fun, and then on a space shooter for simple control scheme for a fast paced game. The game involves X and Y movement, and an on or off firing mechanism. For the movement control, we average, post process, and threshold gyro values to translate into playable movement. Firing control is a basic threshold from a flex sensor resistive divider (more in software).

Hardware
========
![Alt text](/Photos/balance_board1.jpg?raw=true "Optional Title")

As shown in the figure, the construction was made out of a large board that was sanded to remove corners and to also add cosmetic shape. This was attached to a 3d printed hemisphere that acted as a surface to balance around. The 3d printed part was designed to save filament and provide a ‘space shooter’ feel. In the final of 2 iterations it was larger in size and had a greater curvature. It was attached to the base board with an intermediary block of wood to allow for more height.
![Alt text](/Photos/balance_board2.JPG?raw=true "Optional Title")

On the front of the base board, a piece of wood and a strip of velcro was added to serve as the mounting position for the gyro.
![Alt text](/Photos/flex_fire1.JPG?raw=true "Optional Title")

Additionally, as seen in the system image, we used an older print for the balance pad to house the flex sensor and create an immersive space shooter trigger.

Software
=======

1. PicoPro Logic
----------------
The PicoPro logic provides the central hub for sensor readings and transfers them in a usable form to the unity engine through serial. The 3 axes of data are taken once every 100ms and adjusted for game playability. The fire mechanism reads analog values from the A0 pin, and measures a change in a voltage divider from the flex sensor to provide on/off actuation.

2. PicoPro Gyro Driver
----------------------
Instead of the provided accelerometer, we decided to use a 3-axis gyro to differentiate between the x and y direction for full movement in the space shooter game. The Adafruit option available from the Jacobs store is what we implemented https://www.adafruit.com/product/1032. With this device, we ported the provided Arduino driver library over with minimal functionality. The included driver only includes initialization to the desired functionality and reading sensor data, so it does not integrate with the overarching Android Things sensor database, which would make it easier for others to pick up and use. 

3. State Logic
--------------
The game requires x and y velocity components from the board as input in order to drive the spaceship around the playable screen. We can read angular acceleration from the gyrometer, and then try to integrate that value from each axis (with time) to get velocity in each direction. However, a common problem with gyrometers is that the integrated values tend to drift upwards and saturate because the noise from each reading is integrated into the velocity value. We tried to measure how much drift is accumulated in each axis per-second and subtract it from each reading, but that wasn’t successful at mitigating drift (probably because the drift is proportional to the movement of the sensor). We also briefly considered combining accelerometer and gyroscope data using an algorithm like DCM, but weren’t certain whether that would have a successful outcome. Our final solution uses a weighted average over the last second of gyroscope readings to come up with an estimate of which direction the board is tilted -- that value is fed into a thresholding function in the game itself to translate the sensor measurements into discrete velocity values for the spaceship in the X and Y axes.

4. Unity Game
-------------

The exact game that we used was the 2D Space Shooter provided with Unity’s tutorials. The completed game had a Player Control file in C#. To interface in serial, the port had to be initialized and we had to read in using a SerialPort.ReadLine() command. Unfortunately it is a known bug in C# right now that this command is faulty. To accommodate a SerialPort.ReadTo(“\n”) command was used instead which has the same functionality,

After interpreting the serial commands - i.e. converting from string to float values. They were then assigned to their respective axis. This allowed direct control of the values that governed the spaceship’s velocity in all 4 directions and also the ability to fire. 

To fire, a threshold value was established in Unity. Therefore, the space was discretized into 2 (on and off) and the flexible resistor with an analog output only served the same purpose as a switch.

![Alt text](/Photos/game_image1.PNG?raw=true "Optional Title")

Reflection
==========

We ran into a decent amount of trouble trying to use the 3-axis driver. To begin with, using this different sensor required us to spend our early project hours writing a new driver. Then decoding angular acceleration values into game movement was not easy, as for a left tilt on the balance board you will see positive and negative values, so it involved so parameter tweaking. All software and integration aside, the PicoPro board gave us unexplained challenge. We tried two boards unsuccessfully, and then switched toa third to have our project function. At one point, one of the 3.3V pins was shorted to ground, which could be a failure mode? That being said other oddities existed too. 

Ultimately, the new sensor and weirdness of the developer environment limited our ability to make a polished game. The system is playable, but upon use one will see how the movement control is intuitive, but with a bit of added randomness. This project was frustrating at times, but ultimately rewarding.


License
-------

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.




