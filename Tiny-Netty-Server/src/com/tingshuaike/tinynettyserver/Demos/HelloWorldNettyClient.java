package com.tingshuaike.tinynettyserver.Demos;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Kuby
 *
 */
public class HelloWorldNettyClient {

	public static void main(String[] args) {
		System.out.println("Ready  to  start  the  HelloWorldNettyClient......");
		EventLoopGroup  workerGroup=new  NioEventLoopGroup();
		try{
			Bootstrap  b=new  Bootstrap();
			b.group(workerGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.SO_KEEPALIVE, true)
			.handler(new  ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new  HelloWorldNettyClientHandler());
				}
			});
			
			//start  the  client
			ChannelFuture  f=b.connect("127.0.0.1",8080).sync();
			System.out.println("HelloWorldNettyClient  is  on.....");
			
			//wait  until  the  connection  is  closed
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally
		{
			workerGroup.shutdownGracefully();
		}
		
	}
}
