
public class LoadUnit extends PipelineUnit {

	Pipeline parent;
	
	public LoadUnit(Pipeline parent) {
		super(parent);
		this.parent = parent;
	}

	@Override
	public void step(int cycleNo) {
		// TODO Auto-generated method stub
		//currently just clearing
		for (ReservationStation r:parent.resvnStns){
			ReservationStationEntry[] arr = r.entries;
			for (int i=0; i< arr.length; i++)
				arr[i]=null;
		}
		
//		parent.dspchBufr.entries.clear();
	}
	
}
