package com.zoco.common.chatting;

import com.zoco.common.ZocoConstants;
import com.zoco.obj.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

/**
 * @author dookim
 * clientThread class is blocking io that communicate with server
 * clientThread is singleton instance because one zoco client have to use one network interface
 */
public class ClientThread extends Thread {

	protected static ClientThread clientThread;
	
	private String host;
	private int port;
	private SocketChannel client;
	private StringBuilder sb;
	private User user;
	private Selector selector;
	private CharsetEncoder encoder;
	private Charset charset;
	private volatile boolean isQuit;
	
	/**
	 * @param host
	 * @param port
	 * @param user
	 * @return clientthread instance 
	 * instance is single-ton
	 */
	public static ClientThread getInstance(String host, int port, User user) {
		if (clientThread == null) {
			clientThread = new ClientThread(host, port, user);
		}
		return clientThread;
	}

	/**
	 * 
	 * @param host the server's ip which is connected  
	 * @param port the server's port which is connected
	 * @param user the user instance 
	 *  
	 */
	private ClientThread(String host, int port, User user) {
		this.host = host;
		this.port = port;
		this.sb = new StringBuilder();
		this.user = user;
		this.isQuit = false;
		
		charset = Charset.forName("UTF-8");
		encoder = charset.newEncoder();
	}

	/**
	 *
	 * @throws java.io.IOException
	 * if client normally disconnect connection, client should send fin message.
	 * so, server remove socket and socketchannel
	 */
	public void sendFinMessage() throws IOException {
		String msg = ZocoConstants.PROTOCOL + ZocoConstants.BEHAVIOUR_FIN + "//" + user.id;
		sayToServer(msg);
	}

	
	/**
	 * 
	 * @param oppositeChatId the counterpart who receive this message
	 * @param bookId each chatting room have each bookId, client should send book id in order to ditinguish chatting room
	 * @param chattingIndex since chatting room is created, each message sent by client has auto incremented id, client should keep id in chatting room
	 * @param msgContent the msg that this client want to send
	 * @throws java.io.IOException
	 */
	public void sendMessage(String oppositeChatId, int bookId, int chattingIndex, String msgContent) throws IOException {
		//String msg = "ZocoChat://message//" + bookId + "//" + chattingIndex + "//" + System.currentTimeMillis() + "//" + user.email + "//" + user.chatId + "//" + oppositeChatId + "//" + msgContent;
		String msg = ZocoConstants.PROTOCOL + ZocoConstants.BEHAVIOUR_MESSAGE + "//" + bookId + "//" + chattingIndex + "//" + System.currentTimeMillis() + "//" + user.nickname + "//" + user.id + "//" + oppositeChatId + "//" + msgContent;
		sayToServer(msg);
	}
	
	//i have never comment on source code 
	// so i think it is big experience if i commented my source code
	
	/**
	 * @throws java.io.IOException
	 * when client want to connect server, client does'nt know where to connect.
	 * so, server will guide which server client should connect 
	 * there are two cases
	 * if client receive ask message, client should ask the server from ask message 
	 * if client receive set message, client should connect server in order to send or receive data from socket
	 *  
	 */
	private void sendAskMessage() throws IOException {
		String msg = ZocoConstants.PROTOCOL + ZocoConstants.BEHAVIOUR_ASK + "//" + user.id;
		sayToServer(msg);
	}
	
	/**
	 * @param lastReceivedIndex
	 * @throws java.io.IOException
	 * when client finally connect to server, client should send this init message
	 * the reason why client send init message is to get the unreceived messages
	 */
	private void sendInitMessage(int lastReceivedIndex) throws IOException {
		String msg = ZocoConstants.PROTOCOL + ZocoConstants.BEHAVIOUR_INIT + "//" + user.id + "//" + lastReceivedIndex;
		sayToServer(msg);
	}
	
	/**
	 * 
	 * @param bookId
	 * @param oppositeChatId
	 * @param chattingIndex
	 * @throws java.io.IOException
	 * if client get another client's message from server
	 * client should send cofirm message because the client which sent message confirm whether another client received or not 
	 */
	 public void sendConfirmMessage(int bookId, String oppositeChatId, int chattingIndex) throws IOException {
		String msg = ZocoConstants.PROTOCOL + ZocoConstants.BEHAVIOUR_CONFIRM + "//" + bookId + "//" + user.id + "//" + oppositeChatId + "//" + chattingIndex;
		sayToServer(msg);
	}
	 /**
	  * @throws java.io.IOException
	  * @throws InterruptedException
	  * tryToConnect method literally try to connect the server
	  */
	private void tryToConnect() throws IOException, InterruptedException {
		System.out.println("Client :: started");

		client = SocketChannel.open();
		client.configureBlocking(false);
		client.connect(new InetSocketAddress(host, port));

		selector = Selector.open();
		client.register(selector, SelectionKey.OP_READ);

		while (!Thread.interrupted() && !isQuit && !client.finishConnect()) {
			Thread.sleep(10);
		}

		System.out.println("Client :: connected");
	}

	/**
	 * 
	 * @param msg
	 * @throws java.io.IOException
	 * sayToServer method send client's message to server
	 * if this method is invoked, Seletor.select()'s blocking is released
	 * and clientShannel send message to server
	 */
	private void sayToServer(String msg) throws IOException {
		int len = client.write(encoder.encode(CharBuffer.wrap(msg)));
		System.out.printf("[write :: text : %s / len : %d]\n", msg, len);
	}
	
	
	/**
	 * if client use start() method, extra thread communicate with server 
	 */
	@Override
	public void run() {
		super.run();
		Charset cs = Charset.forName("UTF-8");

		try {

			tryToConnect();
			sendAskMessage();
			ByteBuffer buffer = ByteBuffer.allocate(4096);

			while (!Thread.interrupted() && !isQuit) {

				selector.select();

				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

				while (!Thread.interrupted() && !isQuit  && iter.hasNext()) {

					SelectionKey key = iter.next();
					if (key.isReadable()) {
						buffer.clear();
						int len = client.read(buffer);
						if (len < 0) {
							System.out.println("Client :: server closed");
							isQuit = true;
							break;
						} else if (len == 0) {
							continue;
						}
						// i read this buffer, so i wanna get bytes from this buffer.
						buffer.flip();

						CharBuffer cb = cs.decode(buffer);

						System.out.printf("From Server : ");
						sb.setLength(0);
						while (cb.hasRemaining()) {
							sb.append(cb.get());
						}
						
						String msg = sb.toString();
						System.out.println(msg);
						
						String[] splited = msg.split("//");
						String behavior = null;
						try {
							behavior = splited[1].trim();
						} catch(ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
							continue;
						}

						if (behavior.equals(ZocoConstants.BEHAVIOUR_SET) || behavior.equals(ZocoConstants.BEHAVIOUR_ASK)) {
							close();
							String[] ipAndPort = splited[2].split(":");
							String ip = ipAndPort[0].trim();
							int port = Integer.parseInt(ipAndPort[1]);
							this.host = ip;
							this.port = port;
							tryToConnect();
							if (behavior.equals(ZocoConstants.BEHAVIOUR_SET)) {
								sendInitMessage(-1);
							}
						//"ZocoChat://message//bookId//lastReceivedIndex//chattingIndex//System.currentTimeMillis()//user.email//user.chatId//msgContent;
						}
                        //whether guarate this behaviour or not
                        //because we cannot control all of chatting room, this method should call in chatting room
                        /*
						else if(behavior.equals(ZocoConstants.BEHAVIOUR_MESSAGE)) {
							sendConfirmMessage(Integer.parseInt(splited[2]), splited[7], Integer.parseInt(splited[4]));
						}
						*/
						//갑자기 끊길 경우 대비
						System.out.println();
						buffer.compact();
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close();
		}

	}
	
	/**
	 * escape while loop using boolean flag
	 * after ecaping while loop, maybe socket connection is closed
	 */
	public void quitConnection() {
		isQuit = true;
	}
	
	/**
	 * close this socket between server and client
	 */
	private void close() {
		if (client != null) {
			try {
				client.socket().close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		System.out.println("Client :: done");
	}
}
