package org.trimatek.mozo.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.Config;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.tools.Serializer;

public class SocketClient {

	private UserCommand userCommand;
	private static Logger logger = Logger.getLogger(SocketClient.class.getName());

	public SocketClient(UserCommand userCommand) {
		this.userCommand = userCommand;
	}

	public void startClient() throws IOException, InterruptedException {

		InetSocketAddress hostAddress = new InetSocketAddress("localhost", Config.SOCKET_UI_PORT);
		SocketChannel client = SocketChannel.open(hostAddress);

		logger.log(Level.INFO, "MOZO: Service socket client started");

		// String threadName = Thread.currentThread().getName();

		byte[] message = null;

		try {
			message = Serializer.serialize(userCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ByteBuffer buffer = ByteBuffer.wrap(message);
		client.write(buffer);
		// System.out.println(messages [i]);
		buffer.clear();
		// Thread.sleep(5000);
		// }
		client.close();
	}
}