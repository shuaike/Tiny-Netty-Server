package com.tingshuaike.tinynettyserver.Demos;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author kuby simple block server wait the client all the time,just write "Hi"
 *         to Client
 */
public class BlockServer {
	private static boolean isRun = true;;

	public static void main(String[] args) {
		try {
			System.out.println("Block  Server   start....");
			run();
			System.out.println("Block  Server   has  started....");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void run() throws IOException {
		int port = 8080;
		int backlog = 1024;
		ServerSocket server = new ServerSocket(port, backlog);
		while (isRun) {
			final Socket clientSocket = server.accept();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// just send "Hi" to Client
					OutputStreamWriter writer = null;
					String  response="Hi";
					String  request=System.currentTimeMillis()+"-"+clientSocket.hashCode();
					try {
						writer = new OutputStreamWriter(clientSocket.getOutputStream());
						writer.write("Hi");
						System.out.println("receive the  request:"+request);
						System.out.println("response   the  request:"+request +" :"+response);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (writer != null) {
							try {
								writer.close();
								clientSocket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}

	public static void close() {
		isRun = false;
	}
}
