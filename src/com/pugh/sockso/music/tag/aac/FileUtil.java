package com.pugh.sockso.music.tag.aac;

import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {
	public static long readLong(byte[] data, int offset, int bytes) {
		long value = 0;
		for (int i = 0; i < bytes && (i + offset) < data.length; i++) {
			value = (value << 8) + (int) data[i + offset];
		}
		return value;
	}

	public static long readLong(FileInputStream fis, int bytes)
			throws IOException {
		long value = 0;
		for (int i = 0; i < bytes; i++) {
			value = (value << 8) + fis.read();
		}
		return value;
	}
}
