Proximity Pass Protocol Specification
=====================================

About the protocol
------------------

This is a protocol to quickly transmit files over ad hoc wifi between two
android devices. This is intended to be similar in functionality to the
Airdrop protocol for iOS devices.

### Physical layer

All communication will be done over ad hoc wifi.

### Transport layer

All compatible clients must connect over TCP connections. They should
connect on port 8888.

Discovering devices
-------------------

### Security concerns

What if I share my phone number on a network with strangers?

 - Share it one digit at a time. The receivers must respond with a digit.
   If the number they're sending doesn't match with my contact book, I know
   to stop sending out my phone number
 - This would be secure for both me (looking for new devices) and the
   devices I'm finding on my network
 - This approach has performance concerns

### The goal of this part of the protocol

The end goal is to build a list of phone numbers and the associated IP
addresses of nearby devices. This list can be used by the sender to identify
nearby devices and select which device to send a file to.

Handshake
---------

Once a user discovers the device he or she wishes to send a file to, that
device must confirm the transfer is OK.

The sending device must send a `request-to-send` of the form, in plain text:

```
RTS\n
<sender's phone-number>\n
<file name>\n
<size in bytes of file>\n
```

An example such packet would look like:

```
RTS\n
13101234567\n
May-5-2015-030603.jpg\n
73700\n
```

The recipient **may** respond with a `confirm-to-send` in the following manner,
in plain text:

```
CTS\n
<receiver's phone-number>\n
<file name>\n
<size in bytes of file>\n
```

An example such packet would look like:

```
CTS\n
13109876543\n
May-5-2015-030603.jpg\n
73700\n
```

The file name is echoed back for redundancy, to make sure that the sender
knows which file has been confirmed. File size is similarly listed for
redundancy, as a way for the recipient to confirm that it has sufficient
storage capacity to save the file.

The receiver **may instead** choose to respond with a `deny-to-send`
response. This sends the opposite signal of a `CTS`: the client is not
permitted to send the file.

An example response looks as follows (the only difference is the response
field):

```
DTS\n
<receiver's phone-number>\n
<file name>\n
<size in bytes of file>\n
```

### Handshaking pt. 2 (optional)

If the file in question is an image file, the protocol also allows for
transmitting a thumbnail of the image during the handshake process. This is
done between the request and response mentioned above. The request to send
is of the same format, but may optionally be responded to with a
`request-for-preview`:

```
RFP\n
<receiver's phone-number>\n
<file name>\n
<size in bytes of file>\n
```

The sender must then respond with a `response-to-preview` as follows:

```
RTP\n
<sender's phone-number>\n
<file name>\n
<size in bytes of file>\n
<thumbnail raw data>
```

The recipient then may respond with a `confirm-to-send` as seen above.

File transfer
-------------

File transfer will be performed over an ad hoc wifi connection. This enables
transfer speeds of up to 10x what is available over Bluetooth alone, while
also avoiding the need to a network connection.

Files must first be sent with a header followed by the actual file in a
`final-file-transfer`:

```
FFT\n
<sender's phone-number>\n
<file name>\n
<size in bytes of file>\n
<Raw file data>
```

Note: this packet should not end in a newline character. This is for
convenience when storing the data on disk.

The server should not not reply to an `FFT` request.
