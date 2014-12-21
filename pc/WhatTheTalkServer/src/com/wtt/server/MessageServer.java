package com.wtt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MessageServer extends Thread {
	private ServerSocket svrSocket;
	private ArrayList<MessageAgent> msgAgentlist;
	
	public MessageServer(int port) throws IOException {
		svrSocket = new ServerSocket(port);
		msgAgentlist = new ArrayList<MessageAgent>();
		System.out.println("WhatTheTalk is open on port " + svrSocket.getLocalPort() + ".");
	}
	
	public void removeAgent(MessageAgent leaver) {
		synchronized (msgAgentlist) {
			msgAgentlist.remove(leaver);
		}		
	}
	
	public void boradcast(MessageAgent sender, String msg) {
		synchronized (msgAgentlist) {
			for (MessageAgent msgAgent : msgAgentlist) {
				if (msgAgent == sender)
					continue;
				msgAgent.sendMessage(msg);
			}
		}
	}
	
	public void disconnect() {
		try {
			svrSocket.close();
			for (MessageAgent item : msgAgentlist) {
				item.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket agentSocket = svrSocket.accept();
				MessageAgent msgAgent = new MessageAgent(agentSocket, this);
				synchronized (msgAgentlist) {
					msgAgentlist.add(msgAgent);
				}
				msgAgent.start();
			}
			catch (IOException e) {
				if (e.getMessage().equals("MessageAgent create stream error."))
					continue;
				else {
					System.out.println("WhatTheTalk closed.");
					break;
				}
			}
		}
	}
}
