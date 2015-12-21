Proximity Pass
==============

About
-----

This is an Android application designed to transfer files peer-to-peer over
Wifi.

This was implemented as a final course project for CS m117 at UCLA.

Video Demo
----------

Check out our [video
demo](https://www.youtube.com/watch?v=hSBmEm1fshI&feature=youtu.be) on youtube.

Protocol and networking
-----------------------

Feel free to check out the [open protocol
specification](https://github.com/jdhurwitz/ProximityPass/blob/master/ProtocolSpecification.md)
for the project

For this project, we aimed a completely peer-to-peer communication standard. All
communication happens over ad hoc wifi. We chose to use wifi as the standard, as
opposed to Bluetooth or NFC for the following reasons:

 1. Wifi is available on essentially every smart phone.
 2. Not all phones have NFC capability, so this eliminates the need for that.
 3. P2P Wifi supports faster data transfer than Bluetooth 4.0 (roughly 250 Mbps
    vs 25 Mbps)

Installation
------------

Right now, we don't have our app in the app store. Installation, however, is
still fairly simple if you have the Android SDK. Just clone our repo and open it
in Android Studio. From there, you can easily load a build of the app onto your
Android device.

Contributing
------------

If you see any bugs in our implementation, or if you have any ideas how to
improve our protocol, we encourage contributions! This is now an open source
project, and we welcome any help in maintaining it.
