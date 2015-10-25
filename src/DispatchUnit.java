
public class DispatchUnit extends PipelineUnit {
int width;
	
	public DispatchUnit(Pipeline parent, int width)
	{
		super(parent);
		this.width = width;
	}

	@Override
	public void step(int cycleNo) {
		// TODO Auto-generated method stub
		parent.dspchBufr.entries.clear();
	}
}
