package org.trimatek.mozo.model;

public enum FileTypeEnum {
	mozo(new MozoReader()), pom(new PomReader());

	private DependenciesReader reader;

	private FileTypeEnum(DependenciesReader reader) {
		this.reader = reader;
	}

	public DependenciesReader getReader() {
		return reader;
	}

	public static boolean contains(String ext) {
		for (FileTypeEnum e : FileTypeEnum.values()) {
			if (e.name().equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

}
