package org.trimatek.mozo.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;
import org.trimatek.mozo.model.service.Service;
import org.trimatek.mozo.sockets.SocketClient;

public class LoadBytecode extends UserCommand implements Command {

	private static Logger logger = Logger.getLogger(LoadBytecode.class.getName());

	private LoadBytecode() {
	}

	public static Command buildInstance(UserCommand userCommand) {
		LoadBytecode instance = new LoadBytecode();
		instance.setVersion(userCommand.getVersion());
		instance.setTargetDir(userCommand.getTargetDir());
		instance.setReferences(userCommand.getReferences());
		return instance;
	}

	@Override
	public void execute(Service service) throws MozoException {
		Version version = ((DispatcherService)service).fetchDependencies(getReferences(), getVersion());
//		saveBytecode(version);
		
		/*
		UserCommand command = new UserCommand();
		command.setId("SaveBytecode");
		command.setVersion(version);
		command.setTargetDir(getTargetDir());
		
		Runnable client = new Runnable() {
			@Override
			public void run() {
				try {
					new SocketClient(command).startClient();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(client, "mozo-service-command").start();
		*/
	}
	
	private void saveBytecode(Version proxy){
		FileOutputStream fos = null;
		if (!new File(getTargetDir()).exists()) {
			new File(getTargetDir()).mkdirs();
		}
		String fileName = proxy.getArtifactId() + "-" + proxy.getVersion() + ".jar";
		try {
			fos = new FileOutputStream(getTargetDir() + fileName);
			fos.write(proxy.getJarProxy());
			fos.close();
		} catch (FileNotFoundException fe) {
			logger.log(Level.SEVERE, "MOZO -> Error while copying Jar proxy to localhost", fe);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "MOZO -> IO exception while copying Jar proxy to localhost", ioe);
		}
		logger.log(Level.INFO, "MOZO -> Jar proxy " + fileName + " copied to localhost");
	}

}
