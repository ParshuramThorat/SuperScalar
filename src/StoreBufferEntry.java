
public class StoreBufferEntry extends BufferEntry{
	short address;
	short data;
	short pc;
	
	public StoreBufferEntry(short address, short data, short pc)
	{
		this.address = address;
		this.data = data;
	}
}
