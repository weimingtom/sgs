package org.sjtu.transformers.catchme.client;

import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;

public class ClientTest2 implements SimpleClientListener {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_HOST = "localhost";
	public static final String DEFAULT_PORT = "1139";
	public static final String MESSAGE_CHARSET = "UTF-8";
	protected final SimpleClient simpleClient = new SimpleClient(this);
	private final Random random = new Random();
	public static void main(String[] args) throws Exception {
		new ClientTest().login();
	}
	protected void login() throws Exception {

		Properties connectProps = new Properties();
		connectProps.put("host", DEFAULT_HOST);
		connectProps.put("port", DEFAULT_PORT);
		simpleClient.login(connectProps);

		while (true) {
			this.actionPerformed();
		}

	}

	protected static ByteBuffer encodeString(String s) {
		try {
			return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required character set " + MESSAGE_CHARSET
					+ " not found", e);
		}
	}

	protected static String decodeString(ByteBuffer buf) {
		try {
			byte[] bytes = new byte[buf.remaining()];
			buf.get(bytes);
			return new String(bytes, MESSAGE_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new Error("Required character set " + MESSAGE_CHARSET
					+ " not found", e);
		}
	}

	public PasswordAuthentication getPasswordAuthentication() {
		String player = "guest-" + random.nextInt(1000);
		System.out.println("Logging in as " + player);
		String password = "guest";
		return new PasswordAuthentication(player, password.toCharArray());
	}
	
	public void loggedIn() {
		System.out.println("Logged in");
	}

	public void loginFailed(String reason) {
		System.out.println("Login failed: " + reason);
	}

	public void disconnected(boolean graceful, String reason) {
		System.out.println("Disconnected: " + reason);
	}

	public ClientChannelListener joinedChannel(ClientChannel channel) {
		return new NullClientChannelListener();
	}

	public void receivedMessage(ByteBuffer message) {
		System.out.println("Server sent: " + decodeString(message));
	}

	public void reconnected() {
		System.out.println("reconnected");
	}
	
	public void reconnecting() {
		System.out.println("reconnecting");
	}

	public void actionPerformed() {
		if (!simpleClient.isConnected())
			return;

		String text = getInputText();
		send(text);
	}

	Scanner scanner = new Scanner(System.in);

	private String getInputText() {
		return scanner.nextLine();
	}

	protected void send(String text) {
		try {
			ByteBuffer message = encodeString(text);
			simpleClient.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class NullClientChannelListener implements ClientChannelListener {
		public void leftChannel(ClientChannel channel) {
			System.out.println("Unexepected call to leftChannel");
		}

		public void receivedMessage(ClientChannel channel, ByteBuffer message) {
			System.out.println("Channel " + channel.getName() + "'s message: "
					+ decodeString(message));
		}
	}

}
