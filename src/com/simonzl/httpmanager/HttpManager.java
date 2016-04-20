package com.simonzl.httpmanager;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 网络请求封装类
 * @author SimonZl
 *
 */
public class HttpManager {
	
	public static final String TAG = "HttpManager";
    // 创建一个定长为3的线程池，可控制线程最大并发数，超出的线程会在队列中等待
	private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
	private Context mContext;
	// 更新UI
	private MyHandler mHandler;

	public HttpManager(Context context) {
		this.mContext = context;
	}

	/**
	 * Get请求
	 * @param url 访问的url
	 * @param handler
	 */
	public void requestGet(final String url, final TaskHandler handler){
		mHandler = new MyHandler(handler);
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				// 判断是否有网络
				if(NetworkHttpUtils.isOpenNetwork(mContext)){
					// Get请求得到InputStream
				    InputStream is = get(url);
				    msg.obj = is;
				}
				mHandler.sendMessage(msg);
			}
		});
	}
	/**
	 * Post请求
	 * @param url 访问的url
	 * @param paramList Post请求传递的参数
	 * @param handler
	 */
	public void requestPost(final String url, final List<BasicNameValuePair> paramList, final TaskHandler handler){
		mHandler = new MyHandler(handler);
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message();
				// 判断是否有网络
				if(NetworkHttpUtils.isOpenNetwork(mContext)){
					// Post请求得到InputStream
					InputStream is = post(url, paramList);
					msg.obj = is;
				}
				mHandler.sendMessage(msg);
			}
		});
	}
	/**
	 * 文件上传请求
	 * @param url 访问的url
	 * @param hashMap 要传入服务端的内容
	 * @param list 文件的sd卡路径集合
	 * @param uploadName 上传文件对应的参数名
	 * @param handler
	 */
	public void requestUpLoad(final String url, final HashMap<String, String> hashMap, final List<String> list, final String uploadName, final TaskHandler handler){
		 mHandler = new MyHandler(handler);
         executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				// 判断是否有网络
				if(NetworkHttpUtils.isOpenNetwork(mContext)){
					// 文件上传请求得到InputStream
				   InputStream is = upLoadFile(url, hashMap, list, uploadName);
				   msg.obj = is;
				}
				mHandler.sendMessage(msg);
			}
		});
	}

	private class MyHandler extends Handler{
		
		private TaskHandler taskHandler;
		
		private MyHandler(TaskHandler taskHandler){
			this.taskHandler = taskHandler;
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			InputStream is = (InputStream) msg.obj;
			if(is == null) // 没有得到InputStream，回调网络请求失败
				taskHandler.onFail();
			else // 成功，并且将返回值回调出去
				taskHandler.onSuccess(taskHandler.parseResult(is));
			super.handleMessage(msg);
		}
	}
	
	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时5秒钟
	private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间5秒钟
	/**
	 * Get请求方式
	 * @param url
	 * @return
	 */
	private InputStream get(String url) {
		if(url == null)
			return null;
		InputStream result = null;
		HttpGet httpGet;
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		try {
			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			result = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Post请求方式
	 * 
	 * @param paramString
	 * @param paramList
	 * @return
	 */
	private InputStream post(String url, List<BasicNameValuePair> paramList) {
		if(url == null)
			return null;
		InputStream result = null;
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(paramList, "utf-8"));
			HttpResponse response = httpClient.execute(httpPost);
			result = response.getEntity().getContent();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 带附件网络协议多个文件上传
	 * 
	 * @param actionUrl  服务端地址
	 * @param hashMap  要传入服务端的内容
	 * @param list  文件的sd卡路径集合
	 * @param uploadName 上传文件对应的参数名
	 * @return 
	 */
	private InputStream upLoadFile(String actionUrl, HashMap<String, String> hashMap, List<String> list, String uploadName) {
		if(actionUrl == null)
			return null;
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/*
			 * 允许Input 、 Output ， 不使用Cache
			 */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			// 填充表单
			Set<Entry<String, String>> entry = hashMap.entrySet();
			Iterator<Entry<String, String>> it = entry.iterator();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				Map.Entry<String, String> me = it.next();
				sb.append(twoHyphens + boundary + end);
				sb.append("Content-Disposition: form-data; name=\""
						+ me.getKey() + "\"" + end);
				sb.append(end);
				sb.append(me.getValue() + end);
			}
			ds.writeBytes(sb.toString());

			FileInputStream fStream = null;
			final int size = list.size();
			for (int i = 0; i < size; i++) {
				// 填充文件
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\""+uploadName+"\";filename=\"" + uploadName + i + ".jpg\"" + end);
				ds.writeBytes(end);
				/* 取得文件的FileInputStream */
				fStream = new FileInputStream(list.get(i));
				/* 设置每次写入1024bytes */
				int bufferSize = 1024 * 10;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = fStream.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				/* close streams */
				fStream.close();
			}
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();
			return con.getInputStream();
		} catch (Exception e) {
			Log.e("Post Photo Exception", e.toString());
			e.printStackTrace();
		}
		return null;
	}
}
