
public class FetchUnit extends PipelineUnit {
	int width;
	Pipeline parent;
	boolean flushed;
	
	public FetchUnit(Pipeline parent, int width)
	{
		super(parent);
		this.parent = parent;
		this.width = width;
		flushed = false;
	}

	@Override
	public void step(int cycleNo) {
		
		if(parent.inStall){
			//will come inside in next cycle only
			if(!flushed){
				//anyway decode happens before fetch
				parent.dcodeBufr.entries.clear();
				logFail(cycleNo, "FLUSH");
				flushed = true;
				
				if(parent.PCjmpv){	//an unconditional jump
					parent.PCnew = parent.PCjmp;
					parent.PCjmpv = false;
					parent.inStallNew = false;
					flushed = false;
				}
				return;
			}
			
			else{	//already flushed in old cycles
				//only conditional jump will come here
				logFail(cycleNo, "STALL");
				
				if(parent.PCcjpv){	//PC is available, use it in next cycle
					parent.PCnew = parent.PCcjp;
					parent.PCcjpv = false;
					parent.inStallNew = false;
					flushed = false;
				}
				
				return;
			}
		}
		
		String newInst;
		boolean inserted;
		short PC = parent.PC;
		short i;
		
		for(i=0; i<width; i++){
			if(PC >= Processor.I$.length)
				break;
			newInst = Processor.I$[PC];
			inserted = parent.dcodeBufr.Add(newInst, PC);
			if(inserted == false)	break;
			PC++;
		}
		
		parent.PCnew = PC;
		logFetch(PC-1, cycleNo);
	}
	
	private void logFail(int cycleNo, String message) {
		String str="FETCH\t"+cycleNo+"\t" + message + "\n";
		parent.logWriter.write(str);
		parent.logStr+=str;
	}

	private void logFetch(int lastInsNo, int cycleNo)
	{
		String message="FETCH\t"+cycleNo+"\t";
		boolean fetched = false;
		
		for(int i=parent.PC; i<=lastInsNo; i++){
			message += i+" ";
			fetched = true;
		}
		message+="\n";
		
		if(fetched == true){
			parent.logWriter.write(message);
			parent.logStr += message;
		}
	}
}
