import java.util.ArrayList;

public class LoadUnit extends PipelineUnit {

	Pipeline parent;
	ReservationStation resn;
	int width;
	int numALUunits;
	
	public LoadUnit(Pipeline parent) {
		super(parent);
		this.parent = parent;
		numALUunits = parent.config.getInt("ALU units");
		resn = parent.resvnStns[numALUunits];
	}

	@Override
	public void step(int cycleNo) {
		ArrayList<Short> loaded = new ArrayList<>();
		int minIndex = getMinPcIndex();
		if(minIndex==-1){
			log(new ArrayList<>(), cycleNo);
			return;
		}
		ReservationStationEntry ent = resn.entries[minIndex];
		short data; short address;
		int outtag;
		int intag;
		
		if(ent.id){	//it is load
			if(ent.ready){
				address = ent.operand[0];
				data = Processor.D$[address];
				int maxResNo = parent.config.getInt("Max reservation station size");
				outtag = numALUunits*maxResNo + minIndex;
				writeReordrBufr(data, outtag, ent.pc);
				parent.cdb.Insert(outtag, data);
				loaded.add(ent.pc);
				resn.Remove(ent);
			}
			else{
				//TODO:load forwarding
			}
		}
			
		log(loaded, cycleNo);
	}
	
	private void writeReordrBufr(short data, int tag, short pc) {
		ReorderBufferEntry curr;
		
		for(BufferEntry ent:parent.reodrBufr.entries){
			curr = (ReorderBufferEntry)	ent;
			if(curr.pc == pc){
				curr.finished = true;
				curr.tag = tag;
				curr.data = data;
				break;
			}
		}
	}

	private int getMinPcIndex()
	{
		short minPC = Short.MAX_VALUE;
		int minIndex = -1;
		
		for(int i=0; i<resn.size; i++){
			if(resn.entries[i]!=null && resn.entries[i].ready){
				if(resn.entries[i].pc < minPC){
					minPC = resn.entries[i].pc;
					minIndex = i;
				}
			}
		}
		
		return minIndex;
	}
	
	private void log(ArrayList<Short> loaded, int cycleNo)
	{
		if(loaded.size()==0)	return;
		
		String str = "LOAD\t" + cycleNo + "\t";
		for (int num:loaded){
			str += num + " ";
		}
		parent.logWriter.write(str+"\n");
	}
}
