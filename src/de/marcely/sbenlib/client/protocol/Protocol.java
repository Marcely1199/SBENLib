package de.marcely.sbenlib.client.protocol;

import de.marcely.sbenlib.client.ServerEventListener;
import de.marcely.sbenlib.client.SocketHandler;
import de.marcely.sbenlib.network.ConnectionInfo;
import de.marcely.sbenlib.network.ProtocolType;
import de.marcely.sbenlib.network.packets.Packet;
import de.marcely.sbenlib.util.SThread;
import lombok.Getter;

public abstract class Protocol {
	
	@Getter protected final ConnectionInfo connectionInfo;
	@Getter protected final SocketHandler socketHandler;
	@Getter protected final ServerEventListener listener;
	
	@Getter protected SThread thread = null;
	
	@Getter protected boolean running = false;
	
	public Protocol(ConnectionInfo connInfo, SocketHandler socketHandler, ServerEventListener listener){
		this.connectionInfo = connInfo;
		this.socketHandler = socketHandler;
		this.listener = listener;
	}
	
	public boolean sendPacket(byte[] packet){
		return _sendPacket(packet);
	}
	
	public boolean sendPacket(Packet packet){
		return sendPacket(packet.encode());
	}
	
	
	public abstract ProtocolType getType();
	
	public abstract boolean run();
	
	public abstract boolean close();
	
	protected abstract boolean _sendPacket(byte[] packet);
}
