
public class BranchUnit extends PipelineUnit {

	ReservationStation resn;
	int numALUunits;
	Pipeline parent;
	
	public BranchUnit(Pipeline parent)
	{
		super(parent);
		this.parent = parent;
		numALUunits = parent.config.getInt("ALU units");
		resn = parent.resvnStns[numALUunits+1];
	}
	
	@Override
	public void step(int cycleNo) {
		int index = getMinPcIndex();
		short pc=-1;
		ReservationStationEntry ent;
		short regNo;
		short offset;
		
		if(index!=-1){
			ent = resn.entries[index];
			pc = ent.pc;
			regNo = ent.operand[0];
			offset = ent.operand[1];
			
			if(ent.ready){
				if(regNo==0){
					parent.PCcjp = (short) (pc+offset);
					parent.PCcjpv = true;
				}
				else{
					parent.PCcjp = (short) (pc+1);
					parent.PCcjpv = true;
				}
				
				writeReordrBufr((short) -1, pc);
				resn.Remove(ent);
			}
		}
		
		log(cycleNo, pc);
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
	
	private void writeReordrBufr(short data, short pc) {
		ReorderBufferEntry curr;
		
		for(BufferEntry ent:parent.reodrBufr.entries){
			curr = (ReorderBufferEntry)	ent;
			if(curr.pc == pc){
				curr.finished = true;
				curr.data = data;
				break;
			}
		}
	}

	private void log(int cycleNo, short pc)
	{
		if(pc==-1){
			//parent.logWriter.write("BRANCH\t"+cycleNo+"\n");
		}
		else{
			String message = "BRANCH\t"+cycleNo+"\t"+pc+"\n";
			parent.logWriter.write(message);
			parent.logStr+=message;
		}
	}
}
