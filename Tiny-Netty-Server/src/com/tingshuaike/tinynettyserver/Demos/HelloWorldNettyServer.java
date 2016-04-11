package com.tingshuaike.tinynettyserver.Demos;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author kuby 2016.04.10 the first demo for netty server demo
 * 
 */
public class HelloWorldNettyServer {
	private int port;

	public HelloWorldNettyServer(int port) {
		this.port = port;
	}

	public void run() throws InterruptedException {
		System.out.println("read to  start  the  helloworldNettyServer.....");
		// the netty server need config two EventLoopGroup but the client need
		// only one
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new HelloWorldMsgChannelHandler());
						};
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			// Bind and start to accept incoming connections
			ChannelFuture f = b.bind(port).sync();
			System.out.println("helloworldNettyServer  is  on.....");
			// wait until the server socket is closed
			// shutdown this server
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args)  throws  Exception {
		int  port;
		if(args.length>0)
		{
			port=Integer.parseInt(args[0]);
		}else
		{
			port=8080;
		}
		new  HelloWorldNettyServer(port).run();
	}
}
