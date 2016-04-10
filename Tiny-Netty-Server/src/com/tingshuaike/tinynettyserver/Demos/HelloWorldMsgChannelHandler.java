package com.tingshuaike.tinynettyserver.Demos;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * hand the IO Event such as 'socket read' 'new request connect'
 * @author zts 2016.04.10
 */
public class HelloWorldMsgChannelHandler extends SimpleChannelInboundHandler {
	// hand the msg read event
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		try{
			String  str;
			str=new String(((ByteBuf) msg).array());
			String  rsp="request-"+System.currentTimeMillis()+"-:"+str;
			System.out.println(rsp);
			ByteBuf  bufRes=ctx.alloc().buffer(4);
			bufRes.writeBytes(rsp.getBytes());
			//just  write  the  msg  to  the  buffer
			ctx.write(bufRes);
			//send  the  msg  in  the  buffer  
			ctx.flush();
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

	//the  connection  establish  or  ready  to  send msg,now we  all  send  currentTime  to  the  client
	//1.create  the  ByteBuf   2.write  the  msg  to  the  ByteBuf  3.ChannelHandlerContext  send  the  msg   4.when  the  send  opetation is  finished  close  the  ctx.
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
	   ByteBuf  time=ctx.alloc().buffer(4);
	   time.writeInt((int)(System.currentTimeMillis()/1000L+2208988800L));
	   final ChannelFuture  f=	ctx.writeAndFlush(time);
	   f.addListener(new  ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			// TODO Auto-generated method stub
			assert  f==future;
			ctx.close();
		}
	});
		
	}
}
