
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
			
			if(regNo==0){
				parent.PCcjp = (short) (pc+offset);
				parent.PCcjpv = true;
			}
			else{
				parent.PCcjp = pc;
				parent.PCcjpv = true;
			}
			resn.Remove(ent);
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

	private void log(int cycleNo, short pc)
	{
		if(pc==-1){
			//parent.logWriter.write("BRANCH\t"+cycleNo+"\n");
		}
		else
			parent.logWriter.write("BRANCH\t"+cycleNo+"\t"+pc+"\n");
	}
}
