package org.trimatek.mozo.ui.sockets;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.ui.Config;
import org.trimatek.mozo.ui.service.UIServiceImpl;
import org.trimatek.mozo.ui.tools.Serializer;

public class SocketServer {
	private Selector selector;
	private Map<SocketChannel, List> dataMapper;
	private InetSocketAddress listenAddress;
	private IProject iproject;
	private static Logger logger = Logger.getLogger(SocketServer.class.getName());

	public SocketServer(String address, int port, IProject iproject) throws IOException {
		listenAddress = new InetSocketAddress(address, port);
		dataMapper = new HashMap<SocketChannel, List>();
		this.iproject = iproject;
	}

	public void startServer() throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.socket().bind(listenAddress);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		logger.log(Level.INFO, "MOZO -> UI Socket Server ready and listening");
		while (true) {
			this.selector.select();
			Iterator keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();
				keys.remove();
				if (!key.isValid()) {
					continue;
				}
				if (key.isAcceptable()) {
					this.accept(key);
				} else if (key.isReadable()) {
					this.read(key);
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);
		Socket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		logger.log(Level.INFO, "MOZO -> UI Socket Server connected to: " + remoteAddr);
		dataMapper.put(channel, new ArrayList());
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(Config.BUFFER_SIZE);
		int numRead = -1;
		numRead = channel.read(buffer);
		String msg = null;
		if (numRead == -1) {
			this.dataMapper.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			logger.log(Level.INFO, "MOZO -> UI Socket connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}
		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);

		try {
			UserCommand usrCommand = (UserCommand) Serializer.deserialize(data);
			Class clazz = Class.forName("org.trimatek.mozo.ui.commands." + usrCommand.getId());
			Method method = clazz.getDeclaredMethod("buildInstance", UserCommand.class);
			Command cmd = (Command) method.invoke(null, usrCommand);
			cmd.execute(new UIServiceImpl(iproject));
		} catch (Exception e) {
			msg = "MOZO -> Error while deserializing command class";
			logger.log(Level.SEVERE, msg + e.getMessage(), e);
		}
	}
}