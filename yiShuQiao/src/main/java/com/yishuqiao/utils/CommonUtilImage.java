package com.yishuqiao.utils;

import android.os.Environment;

public class CommonUtilImage {

	public static String getPath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().toString()
					+ "/dianping/data/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().toString()
					+ "/dianping/data/"; // filePath: /data/data/
		}
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

}
