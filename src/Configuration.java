import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Configuration {
	HashMap<String, String> dictionary;
	
	public Configuration() throws IOException
	{
		dictionary = new HashMap<String, String>();
		BufferedReader file = new BufferedReader(new FileReader("configuration.txt"));
		
		String line;
		String[] pair;
		while((line = file.readLine()) != null){
			if(line.isEmpty())	continue;
			pair = line.split("\t");
			dictionary.put(pair[0], pair[1]);
		}
		file.close();
	}
	
	public int getInt(String param){
		String str = dictionary.get(param);
		return Integer.parseInt(str); 
	}

	public Short getShort(String param) {
		if(dictionary.containsKey(param)){
			String str = dictionary.get(param);
			return Short.parseShort(str);
		}
		else{
			return null;
		}
	}
	
	public String getString(String param)
	{
		if(dictionary.containsKey(param)){
			String str = dictionary.get(param);
			return str;
		}
		else{
			return null;
		}
	}
}
