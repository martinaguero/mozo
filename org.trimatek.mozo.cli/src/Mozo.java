
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
 
public class Mozo {

	private final static String brand = "Mozo 0.1 alfa";
	private final static String header = "mozo> ";
	private final static String headererror = "Error: ";
	private final static String helpfind = "[listado de nombres de módulos separados por coma (com.mod1,org.mod2)]";
	private final static String helpprint = "[variable con resultado (var1)]";
	private static Map<String, String> results = new HashMap<String, String>();
	private static int cont;
	private static boolean online = true;

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println(brand);
		while (online) {
			System.out.print(header);
			parseInput(scanner.nextLine());
		}
		scanner.close();
	}

	private static void parseInput(String input) {
		String[] args = input.split(" ");
		for (String arg : args) {
			if (arg.equals("buscar-modulos") || arg.equals("bm")) {
				if (args.length < 2) {
					break;
				}
				findModules(args[1]);
				return;
			} else if (arg.equals("mostrar") || arg.equals("m")) {
				if (args.length < 2) {
					break;
				}
				printResult(args[1]);
				return;
			} else if (arg.equals("listar-modulos") || arg.equals("lm")) {
				if (args.length < 2) {
					break;
				}
				listModules(args[1]);
				return;
			} else if (arg.equals("help") || arg.equals("ayuda") || arg.equals("man")) {
				printHelp();
				return;
			} else if (arg.equals("quit") || arg.equals("exit") || arg.equals("salir")) {
				System.out.println("chau");
				online = false;
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
			String key = "res" + cont++;
			results.put(key, (String) m.invoke(c.newInstance(), target));
			printResult(key);
			System.out.println("resultado guardado en: " + key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void printResult(String key) {
		System.out.println(results.get(key));
	}

	private static void listModules(String key) {
		Set<String> modules = new HashSet<String>();
		for (String line : results.get(key).split("\n")) {
			if (line.contains("\"module\":")) {
				modules.add((line.replaceAll("\"", "").replaceAll("module:", "").replaceAll(",", "")).trim());
			}
		}
		for (String module : modules) {
			System.out.println(module);
		}
	}

	private static void printHelp() {
		System.out.println("Sintaxis:");
		System.out.println("\tbuscar-modulos " + helpfind);
		System.out.println("\tbm " + helpfind);
		System.out.println("\tmostrar " + helpprint);
		System.out.println("\tm " + helpprint);
		System.out.println("\tlistar-modulos " + helpprint);
		System.out.println("\tlm " + helpprint);
	}

}
