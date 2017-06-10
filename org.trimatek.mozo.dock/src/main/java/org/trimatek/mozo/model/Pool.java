package org.trimatek.mozo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pool {

	private Map<String, String> pathMap;
	private Map<String, List<String>> refsMap;

	public Pool() {
		pathMap = new HashMap<String, String>();
		refsMap = new HashMap<String, List<String>>();
	}

	public void put(String moduleName, String path) {
		pathMap.put(moduleName, path);
	}

	public void put(String moduleName, List<String> refs) {
		refsMap.put(moduleName, refs);
	}

	public void getPath(String moduleName) {
		pathMap.get(moduleName);
	}

	public List<String> getReferences(String moduleName) {
		return refsMap.get(moduleName);
	}
	
	public boolean containsPath(String path){
		return pathMap.containsValue(path);
	}
	
	public boolean containsRefs(String moduleName){
		return refsMap.containsKey(moduleName);
	}

}
