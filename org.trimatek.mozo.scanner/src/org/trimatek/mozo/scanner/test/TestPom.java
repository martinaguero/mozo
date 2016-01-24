package org.trimatek.mozo.scanner.test;

import java.io.IOException;

import org.apache.maven.model.io.ModelParseException;
import org.trimatek.mozo.catalog.model.RepoEntity;
import org.trimatek.mozo.scanner.utils.MavenUtils;


public class TestPom {

	public static void main(String[] args) throws ModelParseException, IOException {
		
		RepoEntity e = MavenUtils.processPom("https://repo1.maven.org/maven2/activemq/activemq-itest-ejb/1.1-G1M3/activemq-itest-ejb-1.1-G1M3.pom", 1);
		System.out.println(e.getArtifactId());
//		ModelReader md = new DefaultModelReader();
//		Map<String, ?> params = null;
//		Model model = md.read(new File("f:\\Temp\\simLifeApplication-0.0.5.pom"), params);
//		for(Dependency d : model.getDependencies()){
//			System.out.println(d.getGroupId());
//		}
		
	}

}
