
public abstract class PipelineUnit {
	protected Pipeline parent;
	
	public abstract void step(int cycleNo);
	
	public PipelineUnit(Pipeline parent)
	{
		this.parent = parent;
	}
}
