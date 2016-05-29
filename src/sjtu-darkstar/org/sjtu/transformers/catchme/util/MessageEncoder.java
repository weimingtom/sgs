package org.sjtu.transformers.catchme.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class MessageEncoder {
	public static final String MESSAGE_CHARSET = "UTF-8";
	public static ByteBuffer encodeString(String s) {
		try {
			return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required character set " + MESSAGE_CHARSET
					+ " not found", e);
		}
	}
	public static String decodeString(ByteBuffer message) {
		try {
			byte[] bytes = new byte[message.remaining()];
			message.get(bytes);
			return new String(bytes, MESSAGE_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required character set " + MESSAGE_CHARSET
					+ " not found", e);
		}
	}
}

