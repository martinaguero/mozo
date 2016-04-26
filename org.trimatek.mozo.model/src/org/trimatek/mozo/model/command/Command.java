package org.trimatek.mozo.model.command;

import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.DispatcherService;

public interface Command {

	public void execute(DispatcherService dispatcherService) throws MozoException;

}
