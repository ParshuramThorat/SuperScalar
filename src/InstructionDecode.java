
public class InstructionDecode {
	short opcode;
	short dest;
	short src1;
	short src2;
	short imm;
	boolean immv;
	short pc;
	
	public InstructionDecode()
	{
		opcode = -1;
		dest = -1;
		src1 = -1;
		src2 = -1;
		imm = -1;
		pc = -1;
		immv = false;
	}
}
