package com.wtt.server;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class WttMessageWriter {
	private BufferedWriter mBufWriter;
	private BufferedOutputStream mBufOut;
	
	public WttMessageWriter(OutputStream out) {
		try {
			mBufWriter = new BufferedWriter(new OutputStreamWriter(out, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mBufOut = new BufferedOutputStream(out);
	}
	
	public void writeMessage(WttMessage msg) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("WTTP/").append(msg.getVersion()).append("\r\n")
				.append(C.MsgProp.action).append(": ").append(msg.getAction()).append("\r\n")
				.append(C.MsgProp.utc).append(": ").append(System.currentTimeMillis() / 1000).append("\r\n");

		if (msg.getAction().equals(C.MsgAction.login)) {
			builder.append(C.MsgProp.id).append(": ").append(msg.getID()).append("\r\n")
					.append(C.MsgProp.status).append(": ").append("200").append("\r\n")
					.append("\r\n");
			mBufWriter.write(builder.toString());
			mBufWriter.flush();
			return;
		}
		
		if (msg.getContent().length == 0) {
			builder.append("\r\n");
			mBufWriter.write(builder.toString());
			mBufWriter.flush();
			return;
		}
		
		builder.append(C.MsgProp.contentLength).append(": ").append(msg.getContent().length).append("\r\n")
				.append(C.MsgProp.contentType).append(": ").append(msg.getContentType()).append("\r\n")
				.append("\r\n");
		mBufWriter.write(builder.toString());
		mBufWriter.flush();
		
		mBufOut.write(msg.getContent());
		mBufOut.flush();
	}
}
