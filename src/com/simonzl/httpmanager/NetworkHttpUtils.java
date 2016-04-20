package com.simonzl.httpmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * 网络判断工具类
 * @author SimonZl
 *
 */
public class NetworkHttpUtils {

	private static final String TAG = "NetworkHttpUtils";
	
	/** 
	 * 对网络连接状态进行判断 
	 * @return  true, 可用； false， 不可用 
	 */  
	public static boolean isOpenNetwork(Context context) {  
	    ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
	    if(activeNetwork != null && activeNetwork.isConnected()) {
	    	boolean isAvailable = activeNetwork.isAvailable();
	    	Log.d(TAG, isAvailable+"");
	        return isAvailable;
	    }  
	    return false;  
	}
}
