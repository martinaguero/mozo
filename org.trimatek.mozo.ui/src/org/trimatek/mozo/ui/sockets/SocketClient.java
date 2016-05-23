package org.trimatek.mozo.ui.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.tools.Serializer;

public class SocketClient {

	private UserCommand userCommand;
	private static Logger logger = Logger.getLogger(SocketClient.class.getName());

	public SocketClient(UserCommand userCommand) {
		this.userCommand = userCommand;
	}

	public void startClient() throws IOException, InterruptedException {
		InetSocketAddress hostAddress = new InetSocketAddress("localhost", Config.SOCKET_MOZO_PORT);
		SocketChannel client = SocketChannel.open(hostAddress);
		logger.log(Level.INFO, "MOZO: UI Socket Client started");
		byte[] message = null;
		try {
			message = Serializer.serialize(userCommand);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "MOZO: UI Socket Client error while receiving command class " + e.getMessage(), e);
		}
		ByteBuffer buffer = ByteBuffer.wrap(message);
		client.write(buffer);
		buffer.clear();
		client.close();
	}
}