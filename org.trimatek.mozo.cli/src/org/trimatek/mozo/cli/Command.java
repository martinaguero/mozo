package org.trimatek.mozo.cli;

public interface Command {

	public Object exec(String arg) throws Exception;

}
