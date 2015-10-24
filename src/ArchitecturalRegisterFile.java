
public class ArchitecturalRegisterFile {

	public class ARFEntry {
		boolean busy;
		int tag;
		short data;
		
		ARFEntry(short initialValue){
			busy = false;
			tag = 0;
			data = initialValue;
		}
	}
	
	ARFEntry[] entries;
	
	public ArchitecturalRegisterFile(Configuration config) {
		int numRegs = config.getInt("Number of architectural registers");
		Short initValue;
		entries = new ARFEntry[numRegs];
		for(int i=0; i<numRegs; i++){
			initValue = config.getShort("Register"+i);
			if(initValue == null)	initValue = 0;
			entries[i] = new ARFEntry(initValue);
		}
	}

}
