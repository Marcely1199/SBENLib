package de.marcely.sbenlib.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import de.marcely.sbenlib.compression.Base64;
import de.marcely.sbenlib.network.ByteArraysCombiner;
import de.marcely.sbenlib.network.Network;
import de.marcely.sbenlib.network.packets.Packet;
import de.marcely.sbenlib.network.packets.PacketLogin;
import de.marcely.sbenlib.network.packets.PacketLoginReply;
import de.marcely.sbenlib.server.protocol.Protocol;
import lombok.Getter;

public class SocketHandler {
	
	@Getter private final SBENServer server;
	@Getter private final Protocol protocol;
	@Getter private final HashMap<String, Session> sessions = new HashMap<String, Session>();
	
	private ByteArraysCombiner combiner;
	
	public SocketHandler(SBENServer server, int maxClients){
		this.server = server;
		combiner = new ByteArraysCombiner(Packet.SEPERATOR[0]);
		
		this.protocol = server.getConnectionInfo().PROTOCOL.getServerInstance(server.getConnectionInfo(), server, new ServerEventListener(){
			public void onClientRequest(Session session){
				sessions.put(session.getIdentifier(), session);
			}

			public void onClientDisconnect(Session session){
				sessions.remove(session.getIdentifier());
			}

			public void onPacketReceive(Session session, byte[] data){
				final List<byte[]> packets = combiner.addBytes(data);
				
				for(byte[] rawPacket:packets){
					// decode base64
					rawPacket = Base64.decode(rawPacket);
					
					// work with packet
					final byte id = rawPacket[0];
					
					switch(id){
					case Packet.TYPE_LOGIN:
						
						final PacketLogin packet = new PacketLogin();
						packet.decode(rawPacket);
						
						final PacketLoginReply packet_reply = new PacketLoginReply();
						
						if(packet.version_protocol == Network.PROTOCOL_VERSION)
							packet_reply.reply = PacketLoginReply.REPLY_SUCCESS;
						else if(packet.version_protocol < Network.PROTOCOL_VERSION)
							packet_reply.reply = PacketLoginReply.REPLY_FAILED_PROTOCOL_OUTDATED_CLIENT;
						else
							packet_reply.reply = PacketLoginReply.REPLY_FAILED_PROTOCOL_OUTDATED_SERVER;
						
						packet_reply.encode();
						session.sendPacket(packet_reply);
						
						break;
					case Packet.TYPE_DATA:
						break;
					case Packet.TYPE_ACK:
						break;
					case Packet.TYPE_NAK:
						break;
					case Packet.TYPE_PING:
						break;
					}
				}
			}

			public List<Session> getSessions(){
				return (List<Session>) sessions.values();
			}

			public Session getSession(InetAddress address, int port){
				final String identifier = address.getHostAddress() + ":" + port;
				
				return sessions.get(identifier);
			}
			
		}, maxClients);
	}
	
	public boolean isRunning(){
		return this.protocol.isRunning();
	}
	
	public int getMaxClients(){
		return this.protocol.getMaxClients();
	}
	
	public ServerStartInfo run(){
		return protocol.run();
	}
	
	public boolean close(){
		return protocol.close();
	}
	
	public boolean sendPacket(Session session, Packet packet){
		return protocol.sendPacket(session, packet);
	}
}
