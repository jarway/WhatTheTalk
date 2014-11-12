package com.wtt.app;

import java.io.IOException;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
	private static final String TAG = "wtt";
	private EditText mIpEdit, mPortEdit, mNameEdit;
	private Button mConnectBtn, mWhatBtn;
	private MessageClient mMsgClient;
	private ListView mMsgListView;
	private ArrayAdapter<String> mMsgAdapter;
	private ArrayList<String> mMsgAryList = new ArrayList<String>();
	
	private final int MSG_CLIENT_SUCCESS = 0;
	private final int MSG_CLIENT_ERROR_IO = 1;
	private final int MSG_CLIENT_ERROR_NUMBER_FORMAT = 2;
	
	private Handler mMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			String msgStr = (String)msg.obj;
			mMsgAryList.add(msgStr);
			mMsgAdapter.notifyDataSetChanged();
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllViews();
    }

    public void onConnectBtnClick(View view) {
    	Log.d(TAG, "onConnectBtnClick");
    	AsyncTask<String, Void, Integer> msgClientTask = new AsyncTask<String, Void, Integer>() {
    		@Override
    		protected Integer doInBackground(String... params) {
    			try {
    				String[] address = params[0].split(":");
    				String ip = address[0];
    				int port = Integer.parseInt(address[1]);
    				
    				mMsgClient = new MessageClient(ip, port, mMsgHandler);
    				mMsgClient.start();
    				
    				return MSG_CLIENT_SUCCESS;
    			}
    	    	catch (NumberFormatException e) {
    	    		Log.e(TAG, "Invalid port number: " + e.getMessage());
    	    		return MSG_CLIENT_ERROR_NUMBER_FORMAT;
    	    	}
    			catch (IOException e) {
    				Log.e(TAG, "Open socket error: " + e.getMessage());
    				return MSG_CLIENT_ERROR_IO;
    			}
    		}
    		
    		@Override
    		protected void onPostExecute(Integer result) {
    			switch (result) {
    			case MSG_CLIENT_SUCCESS:
    				mWhatBtn.setEnabled(true);
    				break;
    			case MSG_CLIENT_ERROR_IO:
    				mConnectBtn.setEnabled(true);
    				break;
    			case MSG_CLIENT_ERROR_NUMBER_FORMAT:
    				mConnectBtn.setEnabled(true);
    				mPortEdit.requestFocus();
    				break;
    			default:
    				throw new AssertionError("Unrecognized result of async task!!");
    			}
    		};
        };
        
        mConnectBtn.setEnabled(false);
        String address = mIpEdit.getText().toString() + ":" + mPortEdit.getText().toString();
        msgClientTask.execute(address);
    }
    
    public void onWhatBtnClick(View view) {
    	Log.d(TAG, "onWhatBtnClick");
    	mMsgClient.sendMessage(mNameEdit.getText().toString() + ": What!?");
    }
        
    @Override
    protected void onDestroy() {
    	Log.d(TAG, "onDestroy");
    	super.onDestroy();
    	if (mMsgClient != null && mMsgClient.isAlive())
    		mMsgClient.disconnect();
    }
    
    private void setAllViews() {
    	mIpEdit = (EditText)findViewById(R.id.ipEdit);
    	mPortEdit = (EditText)findViewById(R.id.portEdit);
    	mNameEdit = (EditText)findViewById(R.id.nameEdit);
    	mConnectBtn = (Button)findViewById(R.id.connectBtn);
    	mWhatBtn = (Button)findViewById(R.id.whatBtn);
    	mMsgListView = (ListView)findViewById(R.id.msgListView);
    	
    	mWhatBtn.setEnabled(false);
    	
    	mMsgAdapter = new ArrayAdapter<String>(this, R.layout.msg_list_item, R.id.msgText, mMsgAryList);
    	mMsgListView.setAdapter(mMsgAdapter);
    	mMsgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }
}