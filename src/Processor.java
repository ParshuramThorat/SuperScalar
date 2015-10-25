import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Kaushik Garikipati, Parshuram Thorat
 *
 */
public class Processor {

	public static short[] D$;	//the data cache
	public static String[] I$;	//the instruction cache
	public static String[] asmInsts;
	public static Configuration config;
	static Pipeline pipeline;
	
	/*
	 * args[0]	instructions file
	 * args[1]	log file
	 * args[2]	asm file
	 */
	public static void main(String[] args) throws IOException {
		initialize(args);
		fillInstructionCache(args[0]);
		asmInsts = ASMConverter.convertAll(I$, config);
		pipeline.run();
		//TODO: GUIpost stuff
		
	}

	private static void initialize(String[] args) throws IOException
	{
		//TODO: GUIpre stuff
		config = new Configuration();
		pipeline = new Pipeline(config);
		D$ = new short[config.getInt("Data Cache size")];
		
	}
	
	private static void fillInstructionCache(String fileName) throws IOException
	{
		//TODO: fixed size instruction cache (if there is time)
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		ArrayList<String> allInst = new ArrayList<String>();
		
		for(String line; (line = br.readLine()) != null; ) {
	        if(line.isEmpty())	continue;
			String instruction = line.substring(0,16);	//avoid /r or /n
	        allInst.add(instruction);
	    }
		
		br.close();
		I$ =  (String[]) allInst.toArray(new String[allInst.size()]);
	}
}
