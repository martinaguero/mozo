package org.trimatek.mozo.scanner.test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.DefaultModelReader;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.io.ModelReader;


public class TestPom {

	public static void main(String[] args) throws ModelParseException, IOException {
		
		
		ModelReader md = new DefaultModelReader();
		Map<String, ?> params = null;
		Model model = md.read(new File("f:\\Temp\\simLifeApplication-0.0.5.pom"), params);
		for(Dependency d : model.getDependencies()){
			System.out.println(d.getGroupId());
		}
		
	}

}
