package org.trimatek.mozo.model;

import java.util.ArrayList;
import java.util.List;

public class Module {

	private String module;
	private String version;
	private String path;
	private String from;
	private List<Module> requires;

	public Module() {
	}

	public Module(String name) {
		module = name;
	}

	public Module(String name, String version) {
		module = name;
		this.version = version;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<Module> getRequires() {
		return requires;
	}

	public void setRequires(List<Module> requires) {
		this.requires = requires;
	}

	public void addModule(Module module) {
		if (requires == null) {
			requires = new ArrayList<Module>();
		}
		requires.add(module);
	}

	public String toString() {
		if (module != null && version != null && path != null) {
			return module + "@" + version + "=" + path;
		} else if (module != null && version != null) {
			return module + "@" + version;
		} else if (module != null && path != null) {
			return module + "=" + path;
		} else if (module != null) {
			return module;
		}
		return null;
	}
}
