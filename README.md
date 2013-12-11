actioncrafter-bukkit
====================


This plugin enables real-time command execution based on external events by using websockets via the Pusher (www.pusher.com) service. Any device, application or service that can trigger pusher events can trigger commands on the bukkit server. It also adds a new server command to trigger events back to the pusher channel.

The primary motivation for this project was to have Arduino (www.arduino.cc) sensors and buttons affect the Minecraft world, such as a proximity sensor connected to the Arduino cause a Minecraft door to open. Using the new 'actioncrafter' server command, it is also possible to have in-game objects, such as buttons and pressure plates turn on Arduino pins.

By combining redstone, other bukkit plugins and any service that talks with Pusher, an unlimited number of inventions are possible.

One key plugin you might consider getting is the RedstoneCommand (http://dev.bukkit.org/bukkit-plugins/redstonecommand/) plugin that lets a server command turn a redstone tourch on/off. 

Some of the inventions I have created are:

* Open the double doors in my Minecraft house when I sit at my computer by using a ultrasonic sensor on my Arduino.
* Push buttons in Minecraft which turn on a lamp in my office by triggering a digitial pin on the Arduino that is connected to a transistor and a relay.
* Have a light sensor connected to the Arduino set the Minecraft world's time based on the amount of light the sensor is receiving.
* Send text messages to my phone via Twilio when someone pushes a Minecraft button outside of my house.
