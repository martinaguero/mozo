
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

	private final static String jarpath = "http://www.trimatek.org/mozo/org.trimatek.mozo.cli.jar";
	private final static String brand = "Mozo 0.1";
	private final static String header = "mozo> ";
	private final static String headererror = "Error: ";
	private final static String helpfind = "[List of modules names separated by commas (e.g.: com.mod1,org.mod2)]";
	private final static String helpprint = "[Variable with result (e.g.: var1)]";
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
			if (arg.equals("find-modules") || arg.equals("fm")) {
				if (args.length < 2) {
					break;
				}
				findModules(args[1]);
				return;
			} else if (arg.equals("download-modules") || arg.equals("dm")) {
				if (args.length < 2) {
					break;
				}
				downloadModules(args[1]);
				return;
			} else if (arg.equals("print") || arg.equals("p")) {
				if (args.length < 2) {
					break;
				}
				printResult(args[1]);
				return;
			} else if (arg.equals("list-modules") || arg.equals("lm")) {
				if (args.length < 2) {
					break;
				}
				printModules(args[1]);
				return;
			} else if (arg.equals("help") || arg.equals("ayuda") || arg.equals("man")) {
				printHelp();
				return;
			} else if (arg.equals("quit") || arg.equals("exit") || arg.equals("salir")) {
				System.out.println("bye");
				online = false;
				return;
			}
		}
		System.out.println(headererror + "command not found");
		printHelp();
	}

	private static void findModules(String target) {
		try {
			Class c = loadClass("org.trimatek.mozo.cli.FindModules");
			Method m = c.getMethod("exec", String.class);
			String key = "res" + cont++;
			results.put(key, (String) m.invoke(c.newInstance(), target));
			printResult(key);
			System.out.println("result stored in: " + key);
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
		System.out.println("\tfind-modules " + helpfind);
		System.out.println("\tfm " + helpfind);
		System.out.println("\tdownload-modules " + helpprint);
		System.out.println("\tdm " + helpprint);
		System.out.println("\tprint " + helpprint);
		System.out.println("\tp " + helpprint);
		System.out.println("\tlist-modules " + helpprint);
		System.out.println("\tlm " + helpprint);
	}

}
