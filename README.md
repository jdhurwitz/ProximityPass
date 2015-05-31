# P2PWifi-master
<b> Simplified Protocol for Reference </b>

Server is defined as the device that requests a file. The server will be the device to which the file is transferred.

Client receives the ip address of the server after a connection is established. The file transfer can begin.

Server Side
<br>
-Request file
<br>
-Select device and connect
<br>
-Wait for transfer
<br>
-Receive file and save to specified directory

Client side
-Send file
<br>
—wait in peer devices list until ip address of server is received
<br>
-start file chooser
<br>
-choose the file
<br>
-send the file

Note that there is nothing defined in the simplified protocol that confirms the file was transferred successfully. 

