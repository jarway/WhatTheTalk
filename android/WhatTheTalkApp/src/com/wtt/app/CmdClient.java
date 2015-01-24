package com.wtt.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.wtt.io.C;
import com.wtt.io.CmdObject;
import com.wtt.io.CmdReader;
import com.wtt.io.CmdWriter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CmdClient {	
	private final String TAG = "wtt";
	private Socket mSocket;
	private CmdReader mCmdReader;
	private CmdWriter mCmdWriter;
	private Handler mOsMsgHander;
	private boolean mHasLoggedIn = false;
	
	public CmdClient(String ip, int port, Handler osMsgHandler) throws IOException {
		try {
			Log.d(TAG, "Connecting to " + ip + ":" + port + ".");
			mSocket = new Socket();
			mSocket.connect(new InetSocketAddress(ip, port), 3000);
			
			mCmdReader = new CmdReader(mSocket.getInputStream());
			mCmdWriter = new CmdWriter(mSocket.getOutputStream());

			mOsMsgHander = osMsgHandler;
			Log.d(TAG, "Connected to WhatTheTalk~");
		}
		catch (IOException e) {
			if (mSocket != null && mSocket.isConnected())
				mSocket.close();
			throw e;
		}
	}
	
	public void start() {
		new Thread(mRecvCmdRun).start();
	}
	
	public void sendCmd(CmdObject cmdObj) {
		try {
			mCmdWriter.writeCmd(cmdObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//show the message string on screen
		Message osMsg = new Message();
		osMsg.obj = cmdObj;
		mOsMsgHander.sendMessage(osMsg);
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
	
	private Runnable mRecvCmdRun = new Runnable() {
		@Override
		public void run() {
			try {
				while (true) {
					CmdObject cmdObj = mCmdReader.readCmd();
					if (cmdObj == null) { continue; }
					
					if (!mHasLoggedIn) {
						if (cmdObj.getAction().equals(C.CmdAction.login)) {

						}
						continue;
					}
					
					switch (cmdObj.getAction()) {
					case C.CmdAction.login:
						if (cmdObj.getStatus() != 200)
							Log.i(TAG, "Hey " + cmdObj.getID() + ", your log info is wrong.");
						else {
							Log.i(TAG, "Hey " + cmdObj.getID() + ", you are logged in now.");
							mHasLoggedIn = true;
						}
						break;
					case C.CmdAction.chat:
					case C.CmdAction.echo:
						if (cmdObj.getContentType().equals(C.CmdContent.text)) {
							Message osMsg = new Message();
							osMsg.obj = cmdObj;
							mOsMsgHander.sendMessage(osMsg);		
						}
						break;
					}
				}
			}
			catch (IOException e) {
				Log.d(TAG, "Byebye, WhatTheTalk~");
				disconnect();
			}			
		}
	};
}
