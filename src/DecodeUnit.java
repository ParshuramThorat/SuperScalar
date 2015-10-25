import java.util.ArrayList;

public class DecodeUnit extends PipelineUnit {
	int width;
	Pipeline parent;
	
	public DecodeUnit(Pipeline parent, int width)
	{
		super(parent);
		this.width = width;
		this.parent = parent;
	}

	@Override
	public void step(int cycleNo) {
		DecodeBufferEntry newInst;
		InstructionDecode decoded;
		boolean inserted;
		ArrayList<Short> pcs = new ArrayList<>();
		short i;
		
		if(parent.inStall){
			logFailure(cycleNo);
			return;
		}
		
		for(i=0; i<width; i++){
			newInst = (DecodeBufferEntry) parent.dcodeBufr.Get(i);
			
			if(newInst == null)
				break;
			else
				decoded = decode(newInst);
			
			inserted = parent.dspchBufr.Add(decoded);
			if(inserted==false)	break;
			pcs.add(decoded.pc);
			
			parent.dcodeBufr.Remove(newInst);
			if(decoded.opcode == 5){
				parent.PCjmp = (short) (decoded.pc + decoded.imm);
				parent.PCjmpv = true;
				parent.inStallNew = true;
				break;
			}
			else if(decoded.opcode == 6){
				parent.inStallNew = true;
				break;
			}
		}
		
		logSuccess(pcs, cycleNo);
	}
	
	private InstructionDecode decode(DecodeBufferEntry instruction) {
		InstructionDecode decoded = new InstructionDecode();
		String inStr = instruction.binInst;
		decoded.pc = instruction.pc;
		
		decoded.opcode = string2Short( instruction.binInst.substring(0,3)); 
		
		switch (decoded.opcode) {
		case 5:	//JMP
//			parent.PCnew = (short) (decoded.pc + string2cShort(inStr.substring(4, 12)));
//			parent.inStallNew = true;
			decoded.imm = (short) string2cShort(inStr.substring(4, 12));
			break;

		case 6:	//BEQZ
			//parent.inStallNew = true;
			decoded.immv = true;
			decoded.imm = string2cShort(inStr.substring(8, 16));
			decoded.src1 = string2Short(inStr.substring(4, 8));
			break;
			
		case 7:	//HLT
			break;

		default://Arithmetic or LD or SD
			decoded.dest = string2Short(inStr.substring(4, 8));
			decoded.src1 = string2Short(inStr.substring(8, 12));
			if (inStr.charAt(3) == '1'){	//immediate
				decoded.immv = true;
				decoded.imm = string2cShort(inStr.substring(12));
			}
			else{
				decoded.immv = false; 
				decoded.src2 = string2Short(inStr.substring(12));
			}
			break;
		}
		
//		log(cycleNo, "ID", insID, "Success");
//		insRD = insID;
//		IDv = true;
//		IRv = false;
////		printDebug(cycleNo, "ID", insID, ID.toString());
		return decoded;
	}
	
	private short string2cShort(String str) {
		short ans = 0;
		char sign = str.charAt(0);
		short magnitude;
		
		if(sign == '0')
			ans = string2Short(str);
		else{
			int magBits = str.length()-1;
			magnitude = string2Short(str.substring(1));
			magnitude = (short) (Math.pow(2, magBits) - magnitude);
			ans = (short) ((-1) * magnitude);
		}
		
		return ans;
	}

	public short string2Short(String str)
	{
		short ans=0;
		int j=0;
		
		for (int i=str.length()-1; i>=0; i--){
			ans += Short.parseShort(str.substring(i,i+1)) * Math.pow(2, j);
			j++;
		}
		
		return ans;
	}
	
	private void logSuccess(ArrayList<Short> pcs, int cycleNo)
	{
		String message="DECODE\t"+cycleNo+"\t";
		for(short i:pcs){
			message += i+" ";
		}
		
		parent.logWriter.write(message+"\n");
	}
	
	private void logFailure(int cycleNo)
	{
		parent.logWriter.write("DECODE\t"+cycleNo+"\tSTALL\n");
	}
}
