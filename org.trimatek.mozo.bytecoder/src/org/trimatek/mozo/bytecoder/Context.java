package org.trimatek.mozo.bytecoder;

public class Context {

	public String OUTPUT_DIR;
	public String TEMP_DIR;
	public String jarName;

	public Context(String jarName) {
		OUTPUT_DIR = Config.OUTPUT_DIR + jarName + "/";
		TEMP_DIR = Config.TEMP_DIR + jarName + "/";
//		OUTPUT_DIR = Config.OUTPUT_DIR + jarName + "\\";
//		TEMP_DIR = Config.TEMP_DIR + jarName + "\\";
		this.jarName = jarName;
	}

}
