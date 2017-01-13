package org.trimatek.mozo.model;

import java.util.ArrayList;
import java.util.List;

public class Module {

	private String name;
	private String version;
	private String path;
	private List<Module> requires;

	public Module(String name) {
		this.name = name;
	}

	public Module(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		if (name != null && version != null && path != null) {
			return name + "@" + version + "=" + path;
		} else if (name != null && version != null) {
			return name + "@" + version;
		} else if (name != null && path != null) {
			return name + "=" + path;
		} else if (name != null) {
			return name;
		}
		return null;
	}
}
