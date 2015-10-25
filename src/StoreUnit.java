import java.util.ArrayList;

public class StoreUnit extends PipelineUnit {

	Pipeline parent;
	ReservationStation resn;
	int width;
	int numALUunits;
	
	public StoreUnit(Pipeline parent) {
		super(parent);
		this.parent = parent;
		numALUunits = parent.config.getInt("ALU units");
		resn = parent.resvnStns[numALUunits];
	}

	@Override
	public void step(int cycleNo) {
		ArrayList<Short> stored = new ArrayList<>();
		int minIndex = getMinPcIndex();
		if(minIndex==-1){
			log(new ArrayList<>(), cycleNo);
			return;
		}
		ReservationStationEntry ent = resn.entries[minIndex];
		short data; short address;
		int tag;
		
		if(ent.id==false){	//it is store
			if(ent.ready){
				address = ent.operand[0];
				data = ent.operand[1];
				int maxResNo = parent.config.getInt("Max reservation station size");
				tag = numALUunits*maxResNo + minIndex;
				writeReordrBufr(data, tag, ent.pc);
				//TODO: assumed store buffer size as global width
				parent.storeBufr.Add(new StoreBufferEntry(address, data, ent.pc));
				stored.add(ent.pc);
				resn.Remove(ent);
			}
		}
			
		log(stored, cycleNo);
	}
	
	private void writeReordrBufr(short data, int tag, short pc) {
		ReorderBufferEntry curr;
		
		for(BufferEntry ent:parent.reodrBufr.entries){
			curr = (ReorderBufferEntry)	ent;
			if(curr.pc == pc){
				curr.finished = true;
				curr.tag = -10;//tag;
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
	
	private void log(ArrayList<Short> stored, int cycleNo)
	{
		if(stored.size()==0)	return;
		
		String str = "STORE\t" + cycleNo + "\t";
		for (int num:stored){
			str += num + " ";
		}
		parent.logWriter.write(str+"\n");
	}

}
