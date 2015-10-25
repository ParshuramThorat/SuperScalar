import java.util.ArrayList;

public class RetireUnit extends PipelineUnit {
	int width;
	Pipeline parent;
	StoreBuffer bufr;
	
	public RetireUnit(Pipeline parent, int width)
	{
		super(parent);
		this.width = width;
		this.parent = parent;
	}

	@Override
	public void step(int cycleNo) {
		StoreBufferEntry ent;
		bufr = parent.storeBufr;
		ArrayList<Short> retired = new ArrayList<>();
		ReorderBufferEntry r;
		
		if(parent.reodrBufr.entries.size()==1){
			parent.done = true;
			return;
		}
			
		
		for(int i=0; i<width; i++){
			ent = (StoreBufferEntry) bufr.Get(i);
			if(ent==null)	continue;
			Processor.D$[ent.address] = ent.data;
			retired.add((short) i);
		}
		
		for(int i=0; i<width; i++){
			ent = (StoreBufferEntry) bufr.Get(i);
			bufr.Remove(ent);
		}
		
		log(retired, cycleNo);
		
		
	}
	
	private void log(ArrayList<Short> retired, int cycleNo)
	{
		if(retired.size()==0)	return;
		
		String str = "RETIRE\t" + cycleNo + "\t";
		for (int num:retired){
			str += num + " ";
		}
		parent.logWriter.write(str+"\n");
	}


}
