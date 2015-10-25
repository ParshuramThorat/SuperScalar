import java.util.HashMap;

public class ArchitecturalRegisterFile {

	ARFEntry[] entries;
	Configuration config;
	
	public ArchitecturalRegisterFile(Configuration config) {
		int numRegs = config.getInt("Number of architectural registers");
		Short initValue;
		entries = new ARFEntry[numRegs];
		this.config = config;
		
		for(int i=0; i<numRegs; i++){
			initValue = config.getShort("Register"+i);
			if(initValue == null)	initValue = 0;
			entries[i] = new ARFEntry(initValue);
		}
	}
	
	public HashMap<Short, ARFEntry> access(InstructionDecode decoded)
	{
		HashMap<Short, ARFEntry> ans = new HashMap<>();
		if(decoded.dest>=0 && decoded.dest < config.getInt("Number of architectural registers"))
			ans.put(decoded.dest, entries[decoded.dest]);
		if(decoded.src1>=0 && decoded.src1 < config.getInt("Number of architectural registers"))
			ans.put(decoded.src1, entries[decoded.src1]);
		if(decoded.src2>=0 && decoded.src2 < config.getInt("Number of architectural registers"))
			ans.put(decoded.src2, entries[decoded.src2]);
		return ans;
	}

}
