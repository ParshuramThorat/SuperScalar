
public class DecodeBuffer extends PipelineBuffer {
	
	public DecodeBuffer(int width) {
		super(width);
//		size = width;
//		entries = new ArrayList<BufferEntry>();
//		additns = new ArrayList<BufferEntry>();
//		deletns = new ArrayList<BufferEntry>();
	}
	
	public boolean Add(String newInst, short pc)
	{
		return super.Add(new DecodeBufferEntry(newInst, pc));
	}

//	public DecodeBufferEntry GetHead()
//	{
//		return (DecodeBufferEntry) entries.get(0);
//	}
}
