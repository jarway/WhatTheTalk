package com.wtt.server;

public final class C {
	public static final String wttpVersion = "0.1";
	public static final String wttsVersion = "0.14.12.15";
	
	public static final class MsgProp {
		public static final String id = "ID";
		public static final String status = "Status";
		public static final String userOS = "User-OS";
		public static final String action = "Action";
		public static final String utc = "UTC";
		public static final String contentLength = "Content-Length";
		public static final String contentType = "Content-Type";
	}
	
	public static final class MsgContent {
		public static final String text = "text";
		public static final String audioPCM = "audio/pcm";
	}
	
	public static final class MsgAction {
		public static final String login = "login";
		public static final String talk = "talk";
		public static final String echo = "echo";
	}	
}
