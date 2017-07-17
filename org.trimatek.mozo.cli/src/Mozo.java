
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Mozo {

	private final static String jarpath = "http://www.trimatek.org/mozo/org.trimatek.mozo.cli.jar";
	private final static String brand = "Mozo 0.3";
	private final static String header = "mozo> ";
	private final static String headererror = "Error: ";
	private final static String helpfind = "[List of modules names separated by commas (e.g.: com.mod1,org.mod2)]";
	private final static String helpprint = "[Variable with result (e.g.: res0)]";
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
		long startTime = System.nanoTime();
		String[] args = input.split(" ");
		for (String arg : args) {
			if (arg.equals("find-modules") || arg.equals("fm")) {
				if (args.length < 2) {
					break;
				}
				findModules(args[1]);
				printTotalTime(startTime);
				return;
			} else if (arg.equals("download-modules") || arg.equals("dm")) {
				if (args.length < 2) {
					break;
				}
				downloadModules(args[1]);
				printTotalTime(startTime);
				return;
			} else if (arg.equals("print") || arg.equals("p")) {
				if (args.length < 2) {
					break;
				}
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
			Method m = c.getMethod("setup", String.class);
			String key = "res" + cont++;
			results.put(key, (String) m.invoke(c.newInstance(), target));
			printResult(key);
			System.out.println("Result stored in: " + key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void downloadModules(String key) {
		try {
			ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
			Class c = loadClass("org.trimatek.mozo.cli.Download");
			if (results.containsKey(key)) {
				for (String path : list(key, "path")) {
					Method m = c.getMethod("setup", String.class);
					Object d = c.newInstance();
					m.invoke(d, path);
					exec.submit((Callable)d);
				}
			}
			while (exec.getActiveCount() > 0) {
			}
			System.out.println("Total downloaded: " + exec.getCompletedTaskCount());
			exec.shutdown();
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
		int c = 0;
		if (results.containsKey(key)) {
			for (String module : list(key, "module")) {
				System.out.println(module);
				c++;
			}
		}
		System.out.println("Total: " + c);
	}

	// TODO Display version
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

	private static void printTotalTime(long startTime) {
		System.out.println("Elapsed time: "
				+ TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) / 1000.0
				+ " seconds");
	}

}
