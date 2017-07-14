# SBENLib
This library provides a complete network system which is very easy to use. You are able to create a whole server with only 2 lines!<br />
The other very great feature of this library is that you are able decide easily between the protocols UDP or TCP and between ZLib and GLib (remember: only with 2 lines).<br />
An other great feature is that you are able to decide between a secured or a normal data packet.<br />
A secured packet is getting encrypted before it's getting send.<br />
If you are using UDP as a protocol you are able different priorities for your data packet: You are able to decide if it's not important that the packet is getting lost, if the packet shouldn't get lost but the order isn't important or if the packet shouldn't get lost and the order should be correct.

# Code examples
Server:
```java
final SBENServer server = new SBENServer(new ConnectionInfo("localhost", 6234, ProtocolType.UDP, CompressionType.ZLib), 1 /* Max clients */);
final boolean success = server.run() == ServerStartInfo.SUCCESS;
```
Client:
```java
final SBENServerConnection client = new SBENServerConnection(new ConnectionInfo("localhost", 6234, ProtocolType.UDP, CompressionType.ZLib));
final boolean success = server.run();
```

# State
This library is currently in a very early stage.<br />
Currently you are not even able to create your own packets.

# Requirements
- Lombok
- Java 7 or higher

# Copyright
(c) 2017 Marcely.de

# License
BSD-3-Clause
