import java.util.ArrayList;

public class CompleteUnit extends PipelineUnit {
	int width;
	Pipeline parent;
	ReorderBuffer bufr;
	
	public CompleteUnit(Pipeline parent, int width)
	{
		super(parent);
		this.width = width;
		this.parent = parent;
	}

	@Override
	public void step(int cycleNo) {
		ArrayList<Short> committed = new ArrayList<>();
		bufr = parent.reodrBufr;
		
		for(int i=0; i<width; i++){
			ReorderBufferEntry r = getMinPcEntry();
			
			if(r==null)	break;
			
			if(r.finished==true){
				for(ARFEntry ent:parent.arf.entries){
					if(ent.busy==true && ent.tag == r.tag){
						ent.data = r.data;
						ent.busy = false;
					}
				}
				
				committed.add(r.pc);
				bufr.entries.remove(r);
			}
			else{
				break;
			}
		}
		
		log(committed, cycleNo);
	}
	
	private ReorderBufferEntry getMinPcEntry()
	{
		short minPC = Short.MAX_VALUE;
		ReorderBufferEntry i;
		ReorderBufferEntry m = null;
		
		for(BufferEntry r:bufr.entries){
			i = (ReorderBufferEntry) r;
			if(i.pc < minPC){
				minPC = i.pc;
				m = i;
			}
		}
		
		return m;
	}
	
	private void log(ArrayList<Short> committed, int cycleNo)
	{
		if(committed.size()==0)	return;
		
		String str = "COMPLETE\t" + cycleNo + "\t";
		for (int num:committed){
			str += num + " ";
		}
		parent.logWriter.write(str+"\n");
	}
}
