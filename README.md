HW3 - Space Shooter Game (Ghosh - Kasi - Lambert)
=====================================
![Alt text](/Photos/system_image1.JPG?raw=true "Optional Title")

A video demonstration of this project can be found at: https://www.youtube.com/watch?v=Z5vldrjRAMI

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

Introduction
============

The instructions for this assignment were to construct a playable game or musical instrument with the PicoPro development board and provided sensors / serial cable. We decided on implementing a game for fun, and then on a space shooter for simple control scheme for a fast paced game.

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


3. Unity Game
-------------

The exact game that we used was the 2D Space Shooter provided with Unity’s tutorials. The completed game had a Player Control file in C#. To interface in serial, the port had to be initialized and we had to read in using a SerialPort.ReadLine() command. Unfortunately it is a known bug in C# right now that this command is faulty. To accommodate a SerialPort.ReadTo(“\n”) command was used instead which has the same functionality,

After interpreting the serial commands - i.e. converting from string to float values. They were then assigned to their respective axis. This allowed direct control of the values that governed the spaceship’s velocity in all 4 directions and also the ability to fire. 

To fire, a threshold value was established in Unity. Therefore, the space was discretized into 2 (on and off) and the flexible resistor with an analog output only served the same purpose as a switch.

![Alt text](/Photos/game_image1.PNG?raw=true "Optional Title")


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




