
import java.util.ArrayList;
import java.util.HashMap;

public class DispatchUnit extends PipelineUnit {
	int width;
	
	public DispatchUnit(Pipeline parent, int width)
	{
		super(parent);
		this.width = width;
	}

	@Override
	public void step(int cycleNo)
	{
		DispatchBufferEntry entry;
		InstructionDecode decoded;
		HashMap<Short, ARFEntry> arfMap;
		ArrayList<Integer> dispatched = new ArrayList<>();
		ReorderBufferEntry reodrEnt;
		
		//check if dispatchable
		//allocate reorder buffer
		//access register file, update if required
		//dispatch to reservation station
		
		for(int i=0; i<width; i++){
			entry = (DispatchBufferEntry) parent.dspchBufr.Get(i);
			if(entry==null)	break;
			decoded = entry.decoded;
			
			//dispatch in order
			if(!dispatchable(decoded, cycleNo))
			{
				break;
			}
			
			reodrEnt = allocReodrBufr(decoded);
			arfMap = parent.arf.access(decoded);
			allocResvnStn(decoded, arfMap);
			dispatched.add((int) decoded.pc);
			
			parent.dspchBufr.Remove(entry);
		}

		log(cycleNo,dispatched);
	}

	private boolean dispatchable(InstructionDecode decoded, int cycleNo) {
		if(parent.reodrBufr.isFull())
		{
			return false;
		}
		
		int numALUunits = parent.config.getInt("ALU units");
		
		switch(decoded.opcode){
		case 0:	case 1:	case 2:	//alu
			for(int i=0; i<numALUunits; i++){
				if (!parent.resvnStns[i].isFull())
					return true;
			}
			break;
			
		case 3: case 4:	//LD/SD
			if(!parent.resvnStns[numALUunits].isFull())
				return true;
			break;
		case 5: case 6:	//branch
			if(!parent.resvnStns[numALUunits+1].isFull())
				return true;
			break;
		case 7:	//halt
			return true;
		}
		
		return false;
	}

	private ReorderBufferEntry allocReodrBufr(InstructionDecode decoded)
	{
		ReorderBufferEntry reodrEntry = new ReorderBufferEntry();
		if(decoded.pc==(Processor.I$.length-1))
			reodrEntry.finished = true;
		else
			reodrEntry.finished = false;
		reodrEntry.pc = decoded.pc;
		parent.reodrBufr.Add(reodrEntry);
		return reodrEntry;
	}
	
	private void allocResvnStn(InstructionDecode decoded, HashMap<Short, ARFEntry> arfMap)
	{
		int numALUunits = parent.config.getInt("ALU units");
		ReservationStationEntry newResEnt = new ReservationStationEntry();
		ARFEntry arfEntry;
		int position;
		newResEnt.pc = decoded.pc;
		
		switch(decoded.opcode)
		{
		case 0:	case 1:	case 2:
			putRegData(newResEnt, 1, arfMap, decoded, 0);
			putRegData(newResEnt, 2, arfMap, decoded, 1);
			
			if(newResEnt.valid[0] && newResEnt.valid[0])
				newResEnt.ready = true;
			newResEnt.busy = true;
			newResEnt.aluType = decoded.opcode;
			
			for(int i=0; i<numALUunits; i++){
				if (!parent.resvnStns[i].isFull()){
					//will definitely come inside atleast once
					position = parent.resvnStns[i].Add(newResEnt);
					arfEntry = arfMap.get(decoded.dest);
					arfEntry.pcTag = decoded.pc;
					arfEntry.busy = true;
					return;
				}
			}
			break;
			
		case 3:	//LD
			putRegData(newResEnt, 1, arfMap, decoded, 0);
			
			if(newResEnt.valid[0])
				newResEnt.ready = true;
			
			position = parent.resvnStns[numALUunits].Add(newResEnt);
			arfEntry = arfMap.get(decoded.dest);
			arfEntry.pcTag = decoded.pc;
			arfEntry.busy = true;
			newResEnt.id = true;
			return;
			
		case 4:	//SD
			putRegData(newResEnt, 0, arfMap, decoded, 0);
			putRegData(newResEnt, 1, arfMap, decoded, 1);
			
			if(newResEnt.valid[0] && newResEnt.valid[0])
				newResEnt.ready = true;
			
			position = parent.resvnStns[numALUunits].Add(newResEnt);
			newResEnt.id = false;
			return;
			
		case 5:	//JMP
			putRegData(newResEnt, 2, arfMap, decoded, 0);
			newResEnt.ready = true;
			
			position = parent.resvnStns[numALUunits+1].Add(newResEnt);
			newResEnt.id = true;
			return;
			
		case 6:	//BEQZ
			putRegData(newResEnt, 1, arfMap, decoded, 0);
			putRegData(newResEnt, 2, arfMap, decoded, 1);
			
			position = parent.resvnStns[numALUunits+1].Add(newResEnt);
			newResEnt.id = false;
			return;
			
		case 7:	//HLT
			return;
			
		}
		
		return;
	}
	
	private void putRegData(ReservationStationEntry resvEntry, int srcNo, HashMap<Short, ARFEntry> arfMap, InstructionDecode decoded, int pos)
	{
		ARFEntry arfEntry;
		
		if(srcNo ==0)
			arfEntry = arfMap.get(decoded.dest);
		else if(srcNo==1)
			arfEntry = arfMap.get(decoded.src1);
		else{
			if(decoded.immv && pos!=-1){
				resvEntry.operand[pos] = decoded.imm;
				resvEntry.valid[pos] = true;
				return;
			}
			else{
				arfEntry = arfMap.get(decoded.src2);
			}
		}
		
		if(arfEntry.busy){
			resvEntry.valid[pos] = false;
			resvEntry.operand[pos] = (short) arfEntry.pcTag;
		}
		else{
			resvEntry.valid[pos] = true;
			resvEntry.operand[pos] = arfEntry.data;
		}
	}
	
	private void log(int cycleNo, ArrayList<Integer> dispatched) {
		if(dispatched.size()==0)	return;
		
		String str = "DISPATCH\t"+cycleNo+"\t";
		for(int num:dispatched)
			str = str+num+" ";
		str+="\n";
		parent.logWriter.write(str);
		parent.logStr += str;
		
	}
}
