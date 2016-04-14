package com.tingshuaike.tinynettyserver.Demos;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * @author kuby
 *  2016.04.12
 */
public class NioClient {
	public  static  boolean  ISRUN=true;
	public  static  void  main(String[]  args)
	{
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
			socketChannl=SocketChannel.open();
			socketChannl.configureBlocking(false);
			selector=Selector.open();
			
			//regist  read  event  to  the  selector
			socketChannl.register(selector, SelectionKey.OP_READ);
			socketChannl.register(selector, SelectionKey.OP_CONNECT);
			//connect  to  the  server
			socketChannl.connect(remote);
			
			//build  the  reactor  mode
			while(ISRUN)
			{
				//check   whether  there  is  a  net  event  has happed?
				if(selector.select()>0)
				{
					//get  the  key   and  query  the  event's  type
						Set<SelectionKey> keys = selector.selectedKeys();
						while (keys.iterator().hasNext()) {
							// get the key and then remove from the key set
							SelectionKey selectionKey = keys.iterator().next();
							// msg ready for reading Event
							 if (selectionKey.equals(SelectionKey.OP_READ)) {
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
							// establish the connection
							else if (selectionKey.equals(SelectionKey.OP_CONNECT)) {
								//when  eatablish  the  connection  regist  the  write  Event  to  the  selector
							}
							// ready  to  write  data  to  the  server
							else if (selectionKey.equals(SelectionKey.OP_WRITE)) {
								//when  eatablish  the  connection  regist  the  write  Event  to  the  selector
								
							}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
