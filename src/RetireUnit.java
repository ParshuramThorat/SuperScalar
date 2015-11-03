import java.util.ArrayList;

public class RetireUnit extends PipelineUnit {
	int width;
	Pipeline parent;
	StoreBuffer bufr;
	
	public RetireUnit(Pipeline parent)
	{
		super(parent);
		this.width = parent.config.getInt("Retire width");
		this.parent = parent;
	}

	@Override
	public void step(int cycleNo) {
		StoreBufferEntry ent = null;
		bufr = parent.storeBufr;
		ArrayList<Short> retired = new ArrayList<>();
		ReorderBufferEntry r;
		int sbfrSize = Integer.MAX_VALUE;
		int retNo=0;
		
		for(int i=0; i<sbfrSize && retNo<=width; i++){
			try {
				ent = (StoreBufferEntry) bufr.Get(i);
			} catch (Exception e) {
			}
			
			if(parent.storeBufr.deletns.contains(ent))
				break;
			
			if(ent==null){
				sbfrSize = bufr.entries.size();
				continue;
			}
			
			if(!parent.allCompltd.contains(ent.pc))
				break;	//not yet completed
			
			Processor.D$[ent.address] = ent.data;
			retired.add(ent.pc);
			bufr.Remove(ent);
			i--;
			retNo++;
			sbfrSize = bufr.entries.size();
		}

		//look for scope of packing
		if(retNo<width && parent.reodrBufr.entries.size()==1){
			r = (ReorderBufferEntry) parent.reodrBufr.Get(0);
			if(r.pc == Processor.I$.length-1){
				parent.done = true;
				retired.add(r.pc);
			}
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
		str += "\n";
		parent.logWriter.write(str);
		parent.logStr+=str;
	}
}
