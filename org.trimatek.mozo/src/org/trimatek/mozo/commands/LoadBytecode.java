package org.trimatek.mozo.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.trimatek.mozo.catalog.model.Class;
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
		Version version = ((DispatcherService) service).fetchDependencies(getReferences(), getVersion());
		setReferences(new HashSet<String>());

		try {
			saveBytecode(version);
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "MOZO -> Error while copying Jar proxy to localhost", ioe);
		}

		UserCommand command = new UserCommand();
		command.setId("SaveBytecode");
		command.setReferences(getReferences());
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

	}

	private void saveBytecode(Version version) throws IOException {

		Path path = Paths.get(getTargetDir() + version + ".jar");
		if (Files.exists(path)) {
			path = null;
			updateJar(version);
		} else {
			JarOutputStream os = new JarOutputStream(new FileOutputStream(new File(getTargetDir() + version + ".jar")));
			for (Class clazz : version.getClasses()) {
				String name = clazz.getClassName().replace(".", "/") + ".class";
				JarEntry entry = new JarEntry(name);
				os.putNextEntry(entry);
				os.write(clazz.getBytecode());
				os.closeEntry();
			}
			os.close();
			getReferences().add(version.toString());
		}
		for (Version dep : version.getDependencies()) {
			saveBytecode(dep);
		}

	}

	private void updateJar(Version version) throws IOException {
		Path tmp = Paths.get(getTargetDir() + version + ".tmp");
		Path proxyFile = Paths.get(getTargetDir() + version + ".jar");
		Files.move(proxyFile, tmp, StandardCopyOption.REPLACE_EXISTING);
		JarFile proxy = new JarFile(getTargetDir() + version + ".tmp");
		File jar = new File(getTargetDir() + version + ".jar");
		JarOutputStream os = new JarOutputStream(new FileOutputStream(jar), proxy.getManifest());
		Enumeration<JarEntry> entries = proxy.entries();
		List<String> updated = new ArrayList<String>();
		for (Class clazz : version.getClasses()) {
			String name = clazz.getClassName().replace(".", "/") + ".class";
			os.putNextEntry(new JarEntry(name));
			os.write(clazz.getBytecode());
			os.closeEntry();
			updated.add(name);
		}
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (!updated.contains(entry.getName()) && !entry.getName().equals("META-INF/MANIFEST.MF")) {
				InputStream is = proxy.getInputStream(entry);
				os.putNextEntry(entry);
				byte[] buffer = new byte[1024];
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.closeEntry();
			}
		}
		os.close();
		proxy.close();
		Files.delete(tmp);
	}
}
