package com.logicaldoc.i18n;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Detects duplicated keys in the messages bundle
 * 
 * @author Marco Meschieri - LogicalDOC
 */
public class DetectDuplicates {

	public static void main(String[] args) throws FileNotFoundException {

		File file = new File("src/main/resources/i18n/messages.properties");
		Map<String, Integer> map = new HashMap<String, Integer>();

		// Open the file that is the first command line parameter
		FileInputStream fstream = new FileInputStream(file);

		BufferedReader br = null;
		try {	
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine.indexOf('=') < 0)
					continue;
				String key = strLine.substring(0, strLine.indexOf('='));
				key = key.trim();
				if (map.containsKey(key)) {
					int count = map.get(key);
					map.put(key, ++count);
				} else
					map.put(key, 1);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}

		System.out.println("Inspected a total of " + map.keySet().size() + " keys");
		System.out.println("Found duplicates: ");
		try {
			for (String key : map.keySet()) {
				if (map.get(key).intValue() > 1)
					System.out.println(key + "(" + map.get(key) + " times)");
			}
		} catch (Exception t) {
			t.printStackTrace();
		}
	}
}