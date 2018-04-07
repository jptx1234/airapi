package com.github.jptx1234.airapi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompressUtils {
	private final static Logger logger = LoggerFactory.getLogger(CompressUtils.class);

	public static byte[] compress(String dataString) {
		byte[] data = dataString.getBytes(StandardCharsets.UTF_8);
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		byte[] output = null;
		try {
			outputStream.close();
			output = outputStream.toByteArray();
		} catch (IOException e) {
			logger.error("压缩文字时关闭流出现异常", e);
		}
		return output;
	}

	public static String decompress(byte[] data) {
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		String outputString = null;
		try {
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
			byte[] output = outputStream.toByteArray();
			outputString = new String(output, StandardCharsets.UTF_8);
		}catch (Exception e) {
			logger.error("解压缩文字时出现异常", e);
		}
		return outputString;
	}

}
