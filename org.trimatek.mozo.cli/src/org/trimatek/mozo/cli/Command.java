package org.trimatek.mozo.cli;

import java.util.concurrent.Callable;

public interface Command extends Callable {

	public Object setup(String arg) throws Exception;

}
