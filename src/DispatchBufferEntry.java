
public class DispatchBufferEntry extends BufferEntry {
	InstructionDecode decoded;
	
	public DispatchBufferEntry(InstructionDecode decoded)
	{
		this.decoded = decoded;
	}
}
