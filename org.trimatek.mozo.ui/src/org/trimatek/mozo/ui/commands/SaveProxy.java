package org.trimatek.mozo.ui.commands;

import java.util.logging.Logger;

import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.Service;
import org.trimatek.mozo.model.service.UIService;

public class SaveProxy extends UserCommand implements Command {

	private static Logger logger = Logger.getLogger(SaveProxy.class.getName());

	private SaveProxy() {
	}

	public static Command buildInstance(UserCommand userCommand) {
		SaveProxy instance = new SaveProxy();
		instance.setVersion(userCommand.getVersion());
		instance.setTargetDir(userCommand.getTargetDir());
		return instance;
	}

	@Override
	public void execute(Service service) throws MozoException {

		((UIService) service).updateClasspath(getTargetDir() + "\\" + getVersion() + ".jar");

	}

}
