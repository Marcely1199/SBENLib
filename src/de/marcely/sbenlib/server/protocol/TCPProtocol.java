package de.marcely.sbenlib.server.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import de.marcely.sbenlib.network.ConnectionInfo;
import de.marcely.sbenlib.network.ProtocolType;
import de.marcely.sbenlib.network.packets.Packet;
import de.marcely.sbenlib.server.ServerEventListener;
import de.marcely.sbenlib.server.ServerStartInfo;
import de.marcely.sbenlib.server.Session;

public class TCPProtocol extends Protocol {
	
	private ServerSocket socket;
	
	public TCPProtocol(ConnectionInfo conn, ServerEventListener listener, int maxClients){
		super(conn, listener, maxClients);
	}
	
	@Override
	public ProtocolType getType(){
		return ProtocolType.TCP;
	}

	@Override
	protected ServerStartInfo _run(){
		if(!running){
			try{
				
				this.socket = new ServerSocket(connectionInfo.PORT, maxClients, connectionInfo.IP);
				
				// server client
				this.thread = new Thread(){
					public void run(){
						while(running){
							try{
								final Socket client = socket.accept();
								
								final Thread thread = new Thread(){
									public void run(){
										try{
											final InputStream inStream = client.getInputStream();
											final Session session = listener.getSession(client.getInetAddress(), client.getPort());
											
											while(running){
												if(inStream.available() >= 1){
													final byte[] packet = new byte[inStream.available()];
													inStream.read(packet);
													
													listener.onPacketReceive(session, packet);
												}
											}
										}catch(IOException e){
											e.printStackTrace();
										}
									}
								};
								
								final Session session = new Session(client.getInetAddress(), client.getPort(), thread, client.getOutputStream());
								
								listener.onClientRequest(session);
								thread.start();
								
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}
				};
				this.thread.start();
				
				this.running = true;
				return ServerStartInfo.SUCCESS;
			}catch(IOException e){
				return ServerStartInfo.FAILED_UNKOWN;
			}
			
		}else
			return ServerStartInfo.FAILED_ALREADYRUNNING;
	}

	@Override
	public boolean close(){
		if(running){
			
			try{
				this.socket.close();
				this.running = false;
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
			
			return true;
		}else
			return false;
	}

	@Override
	public boolean sendPacket(Session session, byte[] packet){
		if(running){
			try{
				((OutputStream) session.getObj()).write(packet);
				sendPacket(session, Packet.SEPERATOR);
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
			
			return true;
		}else
			return false;
	}
}