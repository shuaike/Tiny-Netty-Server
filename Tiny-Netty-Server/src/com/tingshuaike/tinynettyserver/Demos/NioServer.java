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
import java.util.Iterator;
import java.util.Set;

/**
 * @author kuby 2016.04.15
 * Single Reactor Thread  Server  Model
 */
public class NioServer {
	// Reactor Thread Flag
	private static boolean ISRUN = true;
	private static String RSPPRE = "Hi,";
	public  static  int  SERVER_PORT=13000;
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
			// create the Selector Object
			Selector selector = Selector.open();
			// create ServerSocket
			serverChannl = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannl.socket();
			serverSocket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT), backlog);
			//config  to  the  nonblock  beacause default  is  block
			serverChannl.configureBlocking(false);

			// regist the accept event to the selector
			serverChannl.register(selector, SelectionKey.OP_ACCEPT);

			// build the reactor model
			while (ISRUN) {
				// reactor thread check the IO whether is OK?
				int readynumber = selector.select(200);
				if (readynumber > 0) {
					//get  the selectionKey and  remove  the  key  from  iterator
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> it=keys.iterator();
					while (it.hasNext()) {
						// get the key and then remove from the key set
						SelectionKey selectionKey = it.next();
						it.remove();
						// Accept Event
						if (selectionKey.isValid()&&selectionKey.isAcceptable()){
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
						else if (selectionKey.isValid()&&selectionKey.isReadable()) {
							//attention here  ByteBuffer is  a head Object(But  building  ByteBuffer out  of  JVM  in Netty)
							ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
							SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
//							clientSocket.configureBlocking(false);
							// ready to put the data to the ByteBuffer
							byteBuffer.clear();
							clientSocket.read(byteBuffer);
							//ready to  read
							byteBuffer.flip();
							String msg;
							Charset cs = Charset.forName("UTF-8");
							msg = new String(byteBuffer.array(), cs);
							System.out.println(msg);
							
							String  rsp=RSPPRE + System.currentTimeMillis();
							ByteBuffer sendBuffer = ByteBuffer
									.wrap((rsp).getBytes("UTF-8"));
							System.out.println("NioServer response the msg: "+rsp);
							clientSocket.write(sendBuffer);
						}
						// write Event only need  when write half package
						else if (selectionKey.equals(SelectionKey.OP_WRITE)) {
							//2 ways to  create ByteBuffer:1.use  ByteBuffer.allocate(...)  2.warp(...)
							//here  the ByteBuffer is a heap  Object
							String  msg=RSPPRE + System.currentTimeMillis();
							ByteBuffer sendBuffer = ByteBuffer
									.wrap((msg).getBytes("UTF-8"));
							//synchronized  send  the  msg
							SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
							System.out.println("NioServer response the msg: "+msg);
							clientSocket.write(sendBuffer);
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
