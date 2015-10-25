
public class ALUUnit extends PipelineUnit {

	ReservationStation resn;
	Pipeline parent;
	int unitNo;
	
	public ALUUnit(Pipeline parent, int unitNo)
	{
		super(parent);
		this.parent = parent;
		resn = parent.resvnStns[unitNo];
		this.unitNo = unitNo;
	}
	
	@Override
	public void step(int cycleNo) {
		int index = getMinPcIndex();
		if(index!=-1){
			ReservationStationEntry ent = resn.entries[index];
			short data=-1;
			short tag;
			
			switch(ent.aluType){
			case 0:	//add
				data = (short) (ent.operand[0]+ent.operand[1]);
			case 1:
				data = (short) (ent.operand[0]-ent.operand[1]);
			case 2:
				data = (short) (ent.operand[0]*ent.operand[1]);
			}
			
			int maxResNo = parent.config.getInt("Max reservation station size");
			tag = (short) (unitNo*maxResNo+index);
			writeReordrBufr(data, tag, ent.pc);
			parent.cdb.Insert(tag, data);
			log(cycleNo, ent.pc);
			resn.Remove(ent);
		}
		else{
			log(cycleNo, (short) -1);
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
	
	private void log(int cycleNo, short pc)
	{
		if(pc==-1){
		//	parent.logWriter.write("ALU"+unitNo+"\t"+cycleNo+"\n");
		}
		else
			parent.logWriter.write("ALU"+unitNo+"\t"+cycleNo+"\t"+pc+"\n");
	}
}
