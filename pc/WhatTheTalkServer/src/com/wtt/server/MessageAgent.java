package com.wtt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketAddress;

public class MessageAgent extends Thread {
	private Socket mSocket;
	private SocketAddress mClientAddress;
	private PrintStream mPrintStream;
	private MessageServer mMsgServer;
	private BufferedReader mBufReader;
	
	public MessageAgent(Socket socket, MessageServer msgServer) throws IOException {
		try {
			mSocket = socket;
			mClientAddress = socket.getRemoteSocketAddress();
			mMsgServer = msgServer;
			
			mPrintStream = new PrintStream(mSocket.getOutputStream());
			mBufReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			System.out.println("\nClient connected! (" + mClientAddress + ").");
		}
		catch (IOException e) {
			if (mSocket != null && mSocket.isConnected())
				mSocket.close();
			throw new IOException("MessageAgent create stream error.");
		}
	}
	
	public void sendMessage(String msg) {
		mPrintStream.print(msg + "\r\n");
		mPrintStream.flush();
	}
	
	public void disconnect() {
		try {
			if (mSocket.isConnected())
				mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while (true) {
				String msg = mBufReader.readLine();
				if (msg == null) {
					throw new IOException("Null message!");
				}
				
				System.out.println("User@" + mClientAddress + " says: " + msg);
				mMsgServer.boradcast(this, msg);
			}
		} catch (IOException e) {
			System.out.println("Client disconnected! (" + mClientAddress + ")");
			mMsgServer.removeAgent(this);
			disconnect();
		}
	}
}
