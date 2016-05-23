package org.trimatek.mozo.model.command;

import org.trimatek.mozo.model.exception.MozoException;
import org.trimatek.mozo.model.service.Service;

public interface Command {

	public void execute(Service service) throws MozoException;

}
