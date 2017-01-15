package org.trimatek.mozo.cli;

import static org.trimatek.mozo.cli.Config.mozopath;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindModules implements Command {

	@Override
	public Object exec(String arg) throws Exception {
		URL url = new URL(mozopath + "find?modules=" + arg);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		int responseCode = con.getResponseCode();
		System.out.println("Sending get request : " + url);
		System.out.println("Response code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String output;
		StringBuffer response = new StringBuffer();

		while ((output = in.readLine()) != null) {
			response.append(output + "\n");
		}
		in.close();
		System.out.println(response.toString());

		return null;
	}

}
