package org.trimatek.mozo.tools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;

public class SocketServer {
	private Selector selector;
	private Map<SocketChannel, List> dataMapper;
	private InetSocketAddress listenAddress;
	private DispatcherService dispatcherService;
	private static Logger logger = Logger.getLogger(SocketServer.class.getName());

	public SocketServer(String address, int port, DispatcherService dispatcherService) throws IOException {
		listenAddress = new InetSocketAddress(address, port);
		dataMapper = new HashMap<SocketChannel, List>();
		this.dispatcherService = dispatcherService;
	}

	public void startServer() throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.socket().bind(listenAddress);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		logger.log(Level.INFO, "MOZO: Socket server started");
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
		logger.log(Level.INFO, "MOZO: Socket server connected to: " + remoteAddr);
		dataMapper.put(channel, new ArrayList());
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int numRead = -1;
		numRead = channel.read(buffer);
		String msg = null;
		if (numRead == -1) {
			this.dataMapper.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			logger.log(Level.INFO, "MOZO: Socket connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}
		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);

		try {
			UserCommand usrCommand = (UserCommand) Serializer.deserialize(data);
			Class clazz = Class.forName("org.trimatek.mozo.commands." + usrCommand.getId());
			
			Method method = clazz.getDeclaredMethod("buildInstance", UserCommand.class);
			Command cmd = (Command)method.invoke(null, usrCommand);
			
			cmd.execute(dispatcherService);

		} catch (ClassNotFoundException ce) {
			msg = "MOZO: Error while deserilizing command class";
			logger.log(Level.SEVERE, msg + ce.getMessage(), ce);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MozoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}