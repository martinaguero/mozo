package org.trimatek.mozo.ui.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.ui.tools.Serializer;

public class SocketClient {

	public void startClient() throws IOException, InterruptedException {

		InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8090);
		SocketChannel client = SocketChannel.open(hostAddress);

		System.out.println("Client... started");

		String threadName = Thread.currentThread().getName();

		byte[] message = null;

		UserCommand command = new UserCommand();
		Version version = new Version(
				"https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.pom");
		command.setId("LoadProxy");
		command.setVersion(version);

		try {
			message = Serializer.serialize(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ByteBuffer buffer = ByteBuffer.wrap(message);
		client.write(buffer);
		// System.out.println(messages [i]);
		buffer.clear();
		Thread.sleep(5000);
		// }
		client.close();
	}
}