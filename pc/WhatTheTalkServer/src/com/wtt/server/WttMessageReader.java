package com.wtt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class WttMessageReader {
	private BufferedReader mBufReader;
	private WttMessage mMsg;
	
	public WttMessageReader(InputStream in) {
		try {
			mBufReader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mMsg = new WttMessage();
	}
	
	public WttMessage readMessage() throws IOException {
		final String TAG = "[WttMessageReader][readMessage] ";
		String line = mBufReader.readLine();
		if (line == null)
			throw new IOException("End of stream reached.");
		
		if (line.isEmpty()) {
			return null;
		}
		if (line.length() < 6 || !line.substring(0, 5).equals("WTTP/")) {
			System.out.println(TAG + "Parse version error, input: " + line);
			return null;
		}
		
		mMsg.reset();
		mMsg.setVersion(line.substring(5));
		
		line = mBufReader.readLine();
		if (line == null)
			throw new IOException("End of stream reached.");
		
		int contentLen = 0;
		do {
			String[] ary = parseHeaderLine(line);
			if (ary == null)
				return null;
			
			String prop = ary[0];
			String value = ary[1];
			switch (prop) {
			case C.MsgProp.userOS:
				mMsg.setUserOS(value);
				break;
			case C.MsgProp.action:
				mMsg.setAction(value);
				break;
			case C.MsgProp.utc:
				mMsg.setUTC(Long.parseLong(value));
				break;
			case C.MsgProp.contentLength:
				contentLen = Integer.parseInt(value);
				break;
			case C.MsgProp.contentType:
				mMsg.setContentType(value);
				break;
			case C.MsgProp.id:
				mMsg.setID(value);
				break;
			default:
				System.out.println(TAG + "Unrecognized property:" + prop);
				return null;
			}
			line = mBufReader.readLine();
			if (line == null)
				throw new IOException("End of stream reached.");
		} while (line != null && !line.isEmpty());
		
		if (contentLen > 0) {
			byte[] byteAry = new byte[contentLen];
			char[] charAry = new char[contentLen];
			
			int i = 0;
			while (i < contentLen) {
				int len = mBufReader.read(charAry, i, contentLen - i);
				for (int k = i; k < i + len; ++k)
					byteAry[k] = (byte)charAry[k];
				i += len;
			}
			mMsg.setContent(byteAry);
		}
		return mMsg;
	}
		
	private String[] parseHeaderLine(String line) {
		final String TAG = "[WttMessageReader][parseHeaderLine] ";
		if (line == null) {
			System.out.println(TAG + "Null line error.");
			return null;
		}		
		
		String[] ary = line.split(": ");
		if (ary == null || ary.length != 2) {
			System.out.println(TAG + "Parse header line error:" + line);
			return null;
		}
		return ary;
	}
	
	
}
