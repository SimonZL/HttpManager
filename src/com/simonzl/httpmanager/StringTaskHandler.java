package com.simonzl.httpmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * TaskHandler<T> 子类，泛型T指定为String
 * @author SimonZl
 *
 */
public abstract class StringTaskHandler extends TaskHandler<String> {

	@Override
	public String parseResult(InputStream result) {
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
		return sb.toString();
	}
}
