package com.wtt.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessageClient extends Thread {	
	private final String TAG = "wtt";
	private Socket mSocket;
	private BufferedReader mBufReader;
	private PrintStream mPrintStream;
	private Handler mMsgHander;
	
	public MessageClient(String ip, int port, Handler msgHandler) throws IOException {
		try {
			Log.d(TAG, "Connecting to " + ip + ":" + port + ".");
			mSocket = new Socket();
			mSocket.connect(new InetSocketAddress(ip, port), 3000);
			mBufReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mPrintStream = new PrintStream(mSocket.getOutputStream());
			mMsgHander = msgHandler;
			Log.d(TAG, "Connected to WhatTheTalk~");
		}
		catch (IOException e) {
			if (mSocket != null && mSocket.isConnected())
				mSocket.close();
			throw e;
		}
	}
	
	public void sendMessage(String msgStr) {
		mPrintStream.println(msgStr);
		
		Message msg = new Message();
		msg.obj = msgStr;
		mMsgHander.sendMessage(msg);
	}
	
	public boolean isConnected() {
		if (mSocket != null)
			return mSocket.isConnected();
		else
			return false;
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
				String msgStr = mBufReader.readLine();
				if (msgStr == null) {
					throw new IOException("Null message!");
				}
				Log.d(TAG, "someone says: " + msgStr);
				
				Message msg = new Message();
				msg.obj = msgStr;
				mMsgHander.sendMessage(msg);
			}
		}
		catch (IOException e) {
			Log.d(TAG, "Byebye, WhatTheTalk~");
			disconnect();
		}
	}
}
