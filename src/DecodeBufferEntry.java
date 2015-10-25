
public class DecodeBufferEntry extends BufferEntry{
	String binInst;
	short pc;
	
	public DecodeBufferEntry(String inst, short pc)
	{
		binInst = inst;
		this.pc = pc;
	}
}
