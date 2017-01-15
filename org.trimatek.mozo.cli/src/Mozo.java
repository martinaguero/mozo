

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Mozo {

	public static void main(String[] args) {

		Class c;
		try {
			URL[] classLoaderUrls = new URL[]{new URL("http://www.trimatek.org/repository/mozo-cli.jar")};
			URLClassLoader loader = new URLClassLoader(classLoaderUrls);
			
			c = Class.forName("org.trimatek.mozo.cli.FindModules", true, loader);
			
			Method m = c.getMethod("exec", null);
			
			m.invoke(c.newInstance(), null);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
