package org.trimatek.mozo.commands;

import org.trimatek.mozo.catalog.model.Version;
import org.trimatek.mozo.model.command.Command;
import org.trimatek.mozo.model.command.UserCommand;
import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;

public class LoadProxy extends UserCommand implements Command {

	private LoadProxy() {
	}

	public static Command buildInstance(UserCommand userCommand) {
		LoadProxy instance = new LoadProxy();
		instance.setVersion(userCommand.getVersion());
		instance.setTargetDir(userCommand.getTargetDir());
		return instance;
	}

	@Override
	public void execute(DispatcherService dispatcherService) throws MozoException  {
		Version proxy = dispatcherService.loadJarProxy(getVersion());
	}

}
