package com.wtt.server;

import java.io.UnsupportedEncodingException;

public class WttMessage {
	private String mID;
	private String mStatus;
	private String mVersion;
	private String mUserOS;
	private String mAction;
	private long mUTC;
	private String mContentType;
	private byte[] mContent;
	
	public WttMessage() {
		reset();
	}
	
	public void reset() {
		mID = "";
		mVersion = "";
		mUserOS = "";
		mAction = "";
		mUTC = 0;
		mContentType = "";
		mContent = new byte[0];
	}

	public void setID(String id) {
		mID = id;
	}
	public String getID() {
		return mID;
	}

	public void setStatus(String status) {
		mStatus = status;
	}
	public String getStatus() {
		return mStatus;
	}
	
	public void setVersion(String version) {
		mVersion = version;
	}
	public String getVersion() {
		return mVersion;
	}
	
	public void setUserOS(String userOS) {
		mUserOS = userOS;
	}
	public String getUserOS() {
		return mUserOS;
	}
	
	public void setAction(String action) {
		mAction = action;
	}
	public String getAction() {
		return mAction;
	}
	
	public void setUTC(long utc) {
		mUTC = utc;
	}
	public long getUTC() {
		return mUTC;
	}
	
	public void setContentType(String contentType) {
		mContentType = contentType;
	}
	public String getContentType() {
		return mContentType;
	}
	
	public void setContent(byte[] content) {
		mContent = content;
	}
	public byte[] getContent() {
		return mContent;
	}
	public String getContentInText() {
		String text = "";
		try {
			text = new String(mContent, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
}
