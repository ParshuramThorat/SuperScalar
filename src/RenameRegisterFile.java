
public class RenameRegisterFile {
	public class RRFEntry{
		boolean busy;
		boolean valid;
		short data;
	}
	
	RRFEntry[]	entries;
	int size;
	
	public RenameRegisterFile(Configuration config)
	{
		size = config.getInt("Rename registers");
		entries = new RRFEntry[size];
		for(int i=0; i<size; i++){
			entries[i] = new RRFEntry();
		}
	}
	
	
}
