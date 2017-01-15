
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class Mozo {
	
	private final static String brand = "Mozo 0.1";
	private final static String header = "mozo> ";
	private final static String headererror = "Error: ";
	private final static String helpfind = "[listado de nombres de módulos separados por coma (com.mod1,org.mod2)]";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println(brand);
		while (true) {
			System.out.print(header);
			parseInput(scanner.nextLine());
		}
	}

	private static void parseInput(String input) {
		String[] args = input.split(" ");
		for (String arg : args) {
			if(arg.equals("buscar-modulos")||arg.equals("bm")){
				if(args.length<2){
					break;
				}
				findModules(args[1]);
				return;
			}
		}
		System.out.println(headererror + "comando no interpretado");
		printHelp();
	}

	private static void findModules(String target) {
		Class c;
		try {
			URL[] classLoaderUrls = new URL[] { new URL("http://www.trimatek.org/repository/mozo-cli.jar") };
			URLClassLoader loader = new URLClassLoader(classLoaderUrls);

			c = Class.forName("org.trimatek.mozo.cli.FindModules", true, loader);

			Method m = c.getMethod("exec", String.class);

			m.invoke(c.newInstance(), target);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void printHelp(){
		System.out.println("Sintaxis:");
		System.out.println("\tbuscar-modulos " + helpfind);
		System.out.println("\tbm " + helpfind);
	}

}
