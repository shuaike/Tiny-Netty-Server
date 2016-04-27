package com.tingshuaike.tinynettyserver.Demos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kuby 2016.04.12
 */
public class NioClient {
	public static boolean ISRUN = true;
	private static String MSG = "Hi,I am " + System.currentTimeMillis() + " !";

	public static void main(String[] args) {
		System.out.println("NIO Client  ready  to  start....");
		run();
		System.out.println("NIO Client  ready  is  on....");
	}

	/**
	 * start  the  nio  client
	 */
	public  static  void  run()
	{
		//Create  the  SocketChannl
		SocketAddress remote=new  InetSocketAddress(NioServer.SERVER_IP,NioServer.SERVER_PORT);
		SocketChannel  socketChannl;
		Selector  selector;
		try {
			selector=Selector.open();
			socketChannl=SocketChannel.open();
			socketChannl.configureBlocking(false);
			
			//connecting  to  the  server  current  thread  wouldn't wait  when  you has configed the block to false.  
			socketChannl.connect(remote);
			//regist  connect  event  to  the  selector
			socketChannl.register(selector, SelectionKey.OP_CONNECT);
			
			//build  the  reactor  mode
			while(ISRUN)
			{
				//check   whether  there  is  a  net  event  has happed?
				if(selector.select(200)>0)
				{
					//get  the  key   and  query  the  event's  type
						Set<SelectionKey> keys = selector.selectedKeys();
						Iterator<SelectionKey>   it=keys.iterator();
						while (it.hasNext()) {
							// get the key and then remove from the key set
							SelectionKey selectionKey = it.next();
							it.remove();
							// msg ready for reading Event  receive the  msg  from  the  server
							 if (selectionKey.isValid()&&selectionKey.isReadable()) {
								ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
								SocketChannel clientSocket = (SocketChannel) selectionKey.channel();
								// ready to put the data to the ByteBuffer
								byteBuffer.clear();
								clientSocket.read(byteBuffer);
								byteBuffer.flip();
								String msg;
								Charset cs = Charset.forName("UTF-8");
								msg = new String(byteBuffer.array(), cs);
								System.out.println("NioClient receive response from the server: "+msg);
							}
							// establish the connection
							else if (selectionKey.isValid()&&selectionKey.isConnectable()) {
								SocketChannel  clientChannl=(SocketChannel) selectionKey.channel();
								if(clientChannl.isConnectionPending()){
									clientChannl.finishConnect();
								}
							    //config  the  SocketChannel  nonBolck
								clientChannl.configureBlocking(false);
								// send the  msg  
								clientChannl.write(ByteBuffer.wrap(MSG.getBytes()));
								//ready for  reading the  response  from  the  server
								clientChannl.register(selector, SelectionKey.OP_READ);
							}
							// send  half package
							else if (selectionKey.isValid()&&selectionKey.isWritable()) {
								SocketChannel  clientChannl=(SocketChannel) selectionKey.channel();
							    //config  the  SocketChannel  nonBolck
								clientChannl.configureBlocking(false);
								
								clientChannl.register(selector, SelectionKey.OP_READ);
							}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
