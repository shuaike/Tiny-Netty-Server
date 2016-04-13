package com.tingshuaike.tinynettyserver.Demos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

public class NioServer {
	// Reactor Thread Flag
	private static boolean ISRUN = true;
	private static String RSPPRE = "Hi,";
	public  static  int  SERVER_PORT=8081;
	public  static  String   SERVER_IP="127.0.0.1";

	public static void main(String[] args) {
		System.out.println("NIOServer  start....");
		run();
		System.out.println("NIOServer  is  on...");
	}

	/**
	 * Nio Server
	 */
	public static void run() {
		ServerSocketChannel serverChannl;
		int backlog = 1024;
		try {
			// create ServerSocket
			serverChannl = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannl.socket();
			serverSocket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT), backlog);

			// create the Selector Object
			Selector selector = Selector.open();

			// regist the accept event to the selector
			serverChannl.register(selector, SelectionKey.OP_ACCEPT);

			// build the reactor model
			while (ISRUN) {
				// reactor thread check the IO whether is OK?
				int readynumber = selector.select();
				if (readynumber > 0) {
					Set<SelectionKey> keys = selector.selectedKeys();
					while (keys.iterator().hasNext()) {
						// get the key and then remove from the key set
						SelectionKey selectionKey = keys.iterator().next();
						// Accept Event
						if (selectionKey.equals(SelectionKey.OP_ACCEPT)) {
							// get the request Socket
							ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
							SocketChannel request = server.accept();
							System.out.println("Server  has  receive  request:" + System.currentTimeMillis() + "-"
									+ request.hashCode());
							// config the request Socket to NonBlock
							request.configureBlocking(false);
							// regist the read msg Event to the select
							request.register(selector, SelectionKey.OP_READ);
						}
						// msg ready for reading Event
						else if (selectionKey.equals(SelectionKey.OP_READ)) {
							ByteBuffer byteBuffer = ByteBuffer.allocate(10);
							SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
							// ready to put the data to the ByteBuffer
							byteBuffer.flip();
							clientSocket.read(byteBuffer);
							String msg;
							Charset cs = Charset.forName("UTF-8");
							msg = new String(byteBuffer.array(), cs);
							System.out.println(msg);
						}
						// msg ready for writing Event
						else if (selectionKey.equals(SelectionKey.OP_WRITE)) {
							ByteBuffer sendBuffer = ByteBuffer
									.wrap((RSPPRE + System.currentTimeMillis()).getBytes("UTF-8"));
							SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
							clientSocket.write(sendBuffer);
						}
						// establish the connection
						else if (selectionKey.equals(SelectionKey.OP_CONNECT)) {

						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close() {
		ISRUN = false;
	}
}
