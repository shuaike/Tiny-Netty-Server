package com.tingshuaike.tinynettyserver.Demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author kuby 2016.04.12 block client
 */
public class BlockClient {
	public static void main(String[] args) {
		System.out.println("Block  client   start....");
//		run();
		 runTest(1000);
	}
	
	public  static  void  runTest(int  size)
	{
		if(size<0)
		{
			size=1;
		}
		for(int i=0;i<size;i++)
		{
			 run();
		}
	}
	
	public  static   void  run()
	{
		Socket  client=new  Socket();
		SocketAddress address = new InetSocketAddress("127.0.0.1", 8080);
		BufferedReader  rspReader=null;
		try {
			client.connect(address);
			 rspReader=	new BufferedReader(new  InputStreamReader(client.getInputStream()));
			String  msg=rspReader.readLine();
			System.out.println("receive  the  rsponse msg:"+msg);
		} catch (IOException e) {
			e.printStackTrace();
		}finally
		{
			if(null!=rspReader)
			{
				 try {
					rspReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
