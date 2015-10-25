import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ASMConverter {
	public static String[] convertAll(String[] I$, Configuration config) throws FileNotFoundException{
		String bin;
		String asmStr;
		String asmFile = config.getString("Assembly file");
		
		String[] asmInsts = new String[I$.length];
		PrintWriter asm = new PrintWriter(asmFile);
		
		for(int i = 0; i<I$.length; i++){
			bin = I$[i];
			asmStr = decode(bin);
			asm.println(i + "\t" + asmStr);
			asmInsts[i] = asmStr;
		}
		
		asm.close();
		return asmInsts;
	}

	private static String decode(String bin) {
		String asm = "";
		short opcode = string2Short( bin.substring(0,3));
		short immediate;
		short src1, src2, dest;
		
		switch(opcode){
		case 0:	asm += "ADD ";	break;
		case 1:	asm += "SUB ";	break;
		case 2:	asm += "MUL ";	break;
		case 3:	asm += "LD ";	break;
		case 4:	asm += "SD ";	break;
		case 5:	asm += "JMP ";	break;
		case 6:	asm += "BEQZ ";	break;
		case 7:	asm += "HLT ";	break;
		default:	asm+="INVALID";
		}
		
		switch (opcode) {
		case 5:	//JMP
			immediate = string2cShort(bin.substring(4, 12));	//verify
			asm += "#"+immediate;
			break;
		case 6:	//BEQZ
			immediate = string2cShort(bin.substring(8, 16));
			src1 = string2Short(bin.substring(4, 8));
			asm += ("R" + src1 + " " + immediate);
			break;
		case 7:	//HLT
			break;

		case 3:	//LD
			dest = string2Short(bin.substring(4, 8));
			src1 = string2Short(bin.substring(8, 12));
			asm += "R" + dest + " [R" + src1 + "]";
			break;
		case 4:	//SD
			dest = string2Short(bin.substring(4, 8));
			src1 = string2Short(bin.substring(8, 12));
			asm += "[R" + dest + "] R" + src1;
			break;
			
		default://Arithmetic or LD or SD
			dest = string2Short(bin.substring(4, 8));
			src1 = string2Short(bin.substring(8, 12));
			if (bin.charAt(3) == '1'){	//immediate
				immediate = string2cShort(bin.substring(12));
				asm += "R" + dest + " R" + src1 + " #" + immediate;
			}
			else{
				src2 = string2Short(bin.substring(12));
				asm += "R" + dest + " R" + src1 + " R" + src2;
			}
			break;
		}
		
		return asm;
	}
	
	private static short string2cShort(String str) {
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

	private static short string2Short(String str)
	{
		short ans=0;
		int j=0;
		
		for (int i=str.length()-1; i>=0; i--){
			ans += Short.parseShort(str.substring(i,i+1)) * Math.pow(2, j);
			j++;
		}
		
		return ans;
	}
}
