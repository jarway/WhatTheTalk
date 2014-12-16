package com.wtt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class MessageAgent extends Thread {
	private Socket mSocket;
	private SocketAddress mClientAddress;	
	private MessageServer mMsgServer;
	private BufferedWriter mBufWriter;
	private WttMessageReader mMsgReader;
	private WttMessageWriter mMsgWriter;
	private String mUserOS = "";
	private String mID = "";
	
	public MessageAgent(Socket socket, MessageServer msgServer) throws IOException {
		try {
			mSocket = socket;
			mClientAddress = socket.getRemoteSocketAddress();
			mMsgServer = msgServer;
			
			mMsgReader = new WttMessageReader(mSocket.getInputStream());
			mBufWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "ISO-8859-1"));
			mMsgWriter = new WttMessageWriter(mSocket.getOutputStream());
			
			sendText("Welcome to WhatTheTalk. Please login first!\r\n");
			System.out.println("\nClient connected! (" + mClientAddress + ").");
		}
		catch (IOException e) {
			if (mSocket != null && mSocket.isConnected())
				mSocket.close();
			throw new IOException("MessageAgent create stream error.");
		}
	}
	
	public void sendText(String text) {
		try {
			mBufWriter.write(text);
			mBufWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(WttMessage msg) {
		try {
			mMsgWriter.writeMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				WttMessage msg = mMsgReader.readMessage();
				if (msg == null)
					continue;
				
				if (mID.equals("")) {
					if (!msg.getAction().equals(C.MsgAction.login) || msg.getID().equals(""))
						sendText("Login error!\r\n");
					else {
						mID = msg.getID();
						mUserOS = msg.getUserOS();
						msg.setStatus("200");
						sendMessage(msg);
					}
					continue;
				}
				
				if (msg.getAction().equals(C.MsgAction.talk)) {
					if (msg.getContentType().equals(C.MsgContent.text))
						System.out.println("User@" + mClientAddress + " talks: " + msg.getContentInText());
					mMsgServer.boradcast(this, msg);
				}
				else if (msg.getAction().equals(C.MsgAction.echo)) {
					if (msg.getContentType().equals(C.MsgContent.text))
						System.out.println("User@" + mClientAddress + " echos: " + msg.getContentInText());
					sendMessage(msg);
				}

			}
		} catch (IOException e) {
			System.out.println("Client disconnected! (" + mClientAddress + ")");
			mMsgServer.removeAgent(this);
			disconnect();
		}
	}
}
