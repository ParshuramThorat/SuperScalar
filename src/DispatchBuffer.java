
public class DispatchBuffer extends PipelineBuffer {
	public DispatchBuffer(int width)
	{
		super(width);
	}
	
	public boolean Add(InstructionDecode decoded)
	{
		return super.Add(new DispatchBufferEntry(decoded));
	}
}
