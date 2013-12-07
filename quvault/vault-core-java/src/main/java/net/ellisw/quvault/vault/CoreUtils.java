package net.ellisw.quvault.vault;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
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
	
	public static String join(List<String> list, String joiner) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				s += joiner;
			s += list.get(i);
		}
		return s;
	}
}
