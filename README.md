# P2PWifi-master
<b> Simplified Protocol for Reference </b>

Server is defined as the device that requests a file. The server will be the device to which the file is transferred.

Client receives the ip address of the server after a connection is established. The file transfer can begin.

Server Side
-Request file
-Select device and connect
-Wait for transfer
-Receive file and save to specified directory

Client side
-Send file
—wait in peer devices list until ip address of server is received—
-start file chooser
-choose the file
-send the file

Note that there is nothing defined in the simplified protocol that confirms the file was transferred successfully. 

