package net.ellisw.quvault.core;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CoreUtils {
	
	public static Map<String, String> parseUriQuery(String query) {
		Map<String, String> searchparms = new HashMap<String, String>();
		if (query != null) {
			StringTokenizer st1 = new StringTokenizer(query, "&");
			while(st1.hasMoreTokens()) {
				StringTokenizer st2 = new StringTokenizer(st1.nextToken(), "=");
				int nTokens = st2.countTokens(); 
				String key = st2.nextToken();
				if (nTokens == 2) {
					String value = st2.nextToken();
					try {
						value = URLDecoder.decode(value, "UTF-8");
						searchparms.put(key, value);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				else {
					searchparms.put(key, null);
				}
			}
		}
		return searchparms;
	}
}
