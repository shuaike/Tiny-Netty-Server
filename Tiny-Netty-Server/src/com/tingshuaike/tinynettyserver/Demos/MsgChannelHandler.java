package com.tingshuaike.tinynettyserver.Demos;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * hand the IO Event such as 'socket read' 'new request connect'
 * 
 * @author zts 2016.04.10
 */
public class MsgChannelHandler extends SimpleChannelInboundHandler {
	// hand the msg read event
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		try{
			String  str;
			str=new String(((ByteBuf) msg).array());
			System.out.println("request-"+System.currentTimeMillis()+"-:"+str);
		}finally
		{
			//release  the bytebuf  of  msg
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
