package com.simonzl.httpmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * TaskHandler<T> 子类，泛型T指定为JSONObject
 * @author SimonZl
 *
 */
public abstract class JsonObjectTaskHandler extends TaskHandler<JSONObject> {

	@Override
	public JSONObject parseResult(InputStream result) {
		// TODO Auto-generated method stub
		// InputStream to String
		StringBuilder sb = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(result, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (result != null)
				try {
					result.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		try {
			// String to JsonObject
			return new JSONObject(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
