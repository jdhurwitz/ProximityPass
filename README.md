P2PWifi-master
==============

Simplified Protocol for Reference
---------------------------------

Server is defined as the device that requests a file. The server will be the
device to which the file is transferred.

Client receives the IP address of the server after a connection is
established. The file transfer can begin.

Server Side

 - Request file
 - Select device and connect
 - Wait for transfer
 - Receive file and save to specified directory

Client side

 - Send file
 - Wait in peer devices list until IP address of server is received
 - Start file chooser
 - Choose the file
 - Send the file

Successful file transfer can be assumed due to TCP reliability
