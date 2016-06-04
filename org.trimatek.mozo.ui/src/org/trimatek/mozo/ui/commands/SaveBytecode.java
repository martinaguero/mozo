package org.trimatek.mozo.ui.commands;

import java.util.logging.Logger;

import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.Service;
import org.trimatek.mozo.model.service.UIService;

public class SaveBytecode extends UserCommand implements Command {

	private static Logger logger = Logger.getLogger(SaveBytecode.class.getName());

	private SaveBytecode() {
	}

	public static Command buildInstance(UserCommand userCommand) {
		SaveBytecode instance = new SaveBytecode();
		instance.setVersion(userCommand.getVersion());
		instance.setTargetDir(userCommand.getTargetDir());
		instance.setReferences(userCommand.getReferences());
		return instance;
	}

	@Override
	public void execute(Service service) throws MozoException {
		for (String ref : getReferences()) {
			((UIService) service).updateClasspath(getTargetDir() + "\\" + ref + ".jar");
		}
	}

}
