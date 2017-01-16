
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Mozo {

	private final static String jarpath = "http://www.trimatek.org/repository/mozo/mozo-cli.jar";
	private final static String brand = "Mozo 0.1";
	private final static String header = "mozo> ";
	private final static String headererror = "Error: ";
	private final static String helpfind = "[listado de nombres de módulos separados por coma (ej: com.mod1,org.mod2)]";
	private final static String helpprint = "[variable con resultado (ej: var1)]";
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
			} else if (arg.equals("descargar") || arg.equals("dm")) {
				if (args.length < 2) {
					break;
				}
				downloadModules(args[1]);
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
				printModules(args[1]);
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
		try {
			Class c = loadClass("org.trimatek.mozo.cli.FindModules");
			Method m = c.getMethod("exec", String.class);
			String key = "res" + cont++;
			results.put(key, (String) m.invoke(c.newInstance(), target));
			printResult(key);
			System.out.println("resultado guardado en: " + key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void downloadModules(String key) {
		try {
			Class c = loadClass("org.trimatek.mozo.cli.Download");
			if (results.containsKey(key)) {
				for (String path : list(key, "path")) {
					Method m = c.getMethod("exec", String.class);
					System.out.println(m.invoke(c.newInstance(), path));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Class loadClass(String className) throws ClassNotFoundException, MalformedURLException {
		URL[] classLoaderUrls = new URL[] { new URL(jarpath) };
		URLClassLoader loader = new URLClassLoader(classLoaderUrls);
		return Class.forName(className, true, loader);
	}

	private static void printResult(String key) {
		System.out.println(results.get(key));
	}

	private static void printModules(String key) {
		if (results.containsKey(key)) {
			for (String module : list(key, "module")) {
				System.out.println(module);
			}
		}
	}
	//TODO Display version
	private static Set<String> list(String key, String field) {
		Set<String> elements = new HashSet<String>();
		for (String line : results.get(key).split("\n")) {
			if (line.contains("\"" + field + "\":")) {
				elements.add((line.replaceAll("\"", "").replaceAll(field + ":", "").replaceAll(",", "")).trim());
			}
		}
		return elements;
	}

	private static void printHelp() {
		System.out.println("Sintaxis:");
		System.out.println("\tbuscar-modulos " + helpfind);
		System.out.println("\tbm " + helpfind);
		System.out.println("\tdescargar-modulos " + helpprint);
		System.out.println("\tdm " + helpprint);
		System.out.println("\tmostrar " + helpprint);
		System.out.println("\tm " + helpprint);
		System.out.println("\tlistar-modulos " + helpprint);
		System.out.println("\tlm " + helpprint);
	}

}
