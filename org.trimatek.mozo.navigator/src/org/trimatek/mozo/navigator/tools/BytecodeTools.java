package org.trimatek.mozo.navigator.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Class;

public class BytecodeTools {

	public static List<String> findReferences(Set<Class> classes,
			String groupId, BytecodeService bytecodeService) throws Exception {
		List<String> references = new ArrayList<String>();
		for (Class clazz : classes) {
			references.addAll(bytecodeService.listReferences(clazz.getClassName(),
					clazz.getBytecode(), groupId));
		}
		return references;
	}

}
