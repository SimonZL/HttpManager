package com.simonzl.httpmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 测试页面，所有测试url填上自己的url，以及想要传递过去的参数
 * @author SimonZl
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	public static final String TAG = "MainActivity";
	/** 文件选择请求码 */
	public static final int FILE_SELECT_CODE = 200;

	// 网络请求封装类
	private HttpManager httpManager;
	private TextView text_Result;
	private Button Btn_Get, Btn_Post, Btn_uploadFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		text_Result = (TextView) findViewById(R.id.text_Result);
		Btn_Get = (Button) findViewById(R.id.Btn_Get);
		Btn_Get.setOnClickListener(this);
		Btn_Post = (Button) findViewById(R.id.Btn_post);
		Btn_Post.setOnClickListener(this);
		Btn_uploadFile = (Button) findViewById(R.id.Btn_uploadFile);
		Btn_uploadFile.setOnClickListener(this);
		// 初始化HttpManager
		httpManager = new HttpManager(this);
	}

	/**
	 * Get测试
	 */
	private void get() {
		// 访问的url
		String url = "";
		httpManager.requestGet(url, new StringTaskHandler() {

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
				text_Result.setText(result);
			}

			@Override
			public void onFail() {
				Toast.makeText(MainActivity.this, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * post测试
	 */
	private void post() {
		// 访问的url
		String url = "";
		// 传递过去的参数集合
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("", ""));
		// 请求
		httpManager.requestPost(url, params, new StringTaskHandler() {

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
				text_Result.setText(result);
			}

			@Override
			public void onFail() {
				Toast.makeText(MainActivity.this, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 打开文件选择提示框
	 */
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
	}

	/**
	 * 上传文件测试
	 * @param filePath 文件路径
	 */
	private void uploadFile(String filePath) {
		// 访问的url
		String url = "";
		// 上传文件对应的参数名
		String uploadName = "file1";
		// 传递给服务端的内容
		HashMap<String, String> hashMap = new HashMap<String, String>();
		// 文件路径集合
		List<String> list = new ArrayList<String>();
		list.add(filePath);
		// 请求
		httpManager.requestUpLoad(url, hashMap, list, uploadName, new StringTaskHandler() {

					@Override
					public void onSuccess(String result) {
						System.out.println(result);
						text_Result.setText(result);
					}

					@Override
					public void onFail() {
						Toast.makeText(MainActivity.this, "网络异常，请检查网络设置",
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Btn_Get:
			get();
			break;
		case R.id.Btn_post:
			post();
			break;
		case R.id.Btn_uploadFile:
			showFileChooser();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case FILE_SELECT_CODE: 
			// 得到文件
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				// 根据Uri得到对应的文件路径
				String path = FileUtils.getPath(this, uri);
				// 不为空
				if (!TextUtils.isEmpty(path)) {
					// 上传文件
					uploadFile(path);
					System.out.println(path);
					text_Result.setText(path);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
