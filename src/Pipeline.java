import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Pipeline {
	String logFilename;
	Configuration config;
	
	PipelineUnit[] units; //all stages, functional units
	FetchUnit fetch;
	DecodeUnit dcode;
	DispatchUnit dspch;
	PipelineUnit[] fnUnits;	//LD, SD, branch, alus
	LoadUnit load;
	StoreUnit store;
	BranchUnit branch;
	ALUUnit[] alus;
	CompleteUnit cmplet;
	RetireUnit retire;
	
	PipelineBuffer[] allBufrs;
	DecodeBuffer dcodeBufr;
	DispatchBuffer dspchBufr;
	ReorderBuffer reodrBufr;
	ArrayList<Short> allCompltd;
	StoreBuffer storeBufr;
	RetireBuffer retirBufr;
	
	CommonDataBus cdb;
	
	ReservationStation[] resvnStns;
	
	ArchitecturalRegisterFile arf;
	RenameRegisterFile rrf;
	
	short PC;
	short PCnew;
	short PCjmp;
	short PCcjp;
	boolean PCjmpv;	//valid for PCjmp
	boolean PCcjpv;
	boolean inStallNew;
	boolean inStall;
	
	boolean done;
	PrintWriter logWriter;
	String logStr;
	
	public void run() throws FileNotFoundException {
		String regs;
		
		int cycleNo;
		for(cycleNo=0; !done; cycleNo++){
			retire.step(cycleNo);
			cmplet.step(cycleNo);
			for (PipelineUnit fnUnit:fnUnits){
				fnUnit.step(cycleNo);
			}
			dspch.step(cycleNo);
			dcode.step(cycleNo);
			fetch.step(cycleNo);
			
			for(ReservationStation r:resvnStns){
				r.listenCDB();
			}
			cdb.Clear();
			
			for(PipelineBuffer buffer:allBufrs)
			{
				buffer.Update();
			}
			updatePipelineRegs();
			
			regs = getRegs();
			if(cycleNo==100)	break;
		}
		
		System.out.println(cycleNo-1);
		putRegs();
		logWriter.close();
	}
	
	private void updatePipelineRegs() {
		PC = PCnew;
		inStall = inStallNew;
	}
	
	private void putRegs() throws FileNotFoundException {
		String str="";
		for(int i=0; i<config.getInt("Number of architectural registers"); i++){
			str += i+"\t"+arf.entries[i].data+"\n";
		}
		
		String fname = config.getString("Register values");
		PrintWriter regFile = new PrintWriter(fname);
		
		regFile.write(str);
		regFile.close();
	}
	
	private String getRegs(){
		String str="";
		for(int i=0; i<config.getInt("Number of architectural registers"); i++){
			str += i+"\t"+arf.entries[i].data+"\n";
		}
		return str;
	}

	public Pipeline(Configuration config) throws FileNotFoundException
	{
		logFilename = config.getString("Log file");
		this.config = config;
		
		initResvnStns(config);
		initPipelineUnits(config);
		initBuffers(config);
		
		arf = new ArchitecturalRegisterFile(config);
		cdb = new CommonDataBus();
		initPipelineRegs();
		
		done = false;
		logWriter = new PrintWriter(logFilename);
		logStr = "";
	}

	/**
	 * @param config
	 */
	private void initPipelineUnits(Configuration config) {
		load = new LoadUnit(this);
		store = new StoreUnit(this);
		branch = new BranchUnit(this);
		
		int numALUUnits = config.getInt("ALU units");
		alus = new ALUUnit[numALUUnits];
		for(int i=0; i<numALUUnits; i++){
			alus[i] = new ALUUnit(this, i);
		}
		
		int numFnUnits = numALUUnits+3;	//1 LD, 1 SD, 1 branch
		fnUnits = new PipelineUnit[numFnUnits];
		System.arraycopy(alus, 0, fnUnits, 0, numALUUnits);
		fnUnits[numALUUnits] = load;	fnUnits[numALUUnits+1] = store;	fnUnits[numALUUnits+2] = branch;
		
		
		int width = config.getInt("Width");
		fetch = new FetchUnit(this, width);
		dcode = new DecodeUnit(this, width);
		dspch = new DispatchUnit(this, width);
		cmplet = new CompleteUnit(this, width);
		retire = new RetireUnit(this);
		
		int totalUnits = numFnUnits+5;	//5 stages
		units = new PipelineUnit[totalUnits];
		units[0] = fetch;	units[1] = dcode;	units[2] = dspch;
		System.arraycopy(fnUnits, 0, units, 3, numFnUnits);
		units[numFnUnits+3] = cmplet;	units[numFnUnits+4] = retire;
	}

	/**
	 * @param width: common width for all buffers
	 */
	private void initBuffers(Configuration config) {
		int width = config.getInt("Width");
		int reodrNum = config.getInt("Reorder buffer entries");
		dcodeBufr = new DecodeBuffer(width);
		dspchBufr = new DispatchBuffer(width);
		reodrBufr = new ReorderBuffer(reodrNum);
		allCompltd = new ArrayList<>();
		storeBufr = new StoreBuffer(width);
		retirBufr = new RetireBuffer();
		allBufrs = new PipelineBuffer[] {dcodeBufr, dspchBufr, reodrBufr, storeBufr, retirBufr};
	}

	/**
	 * @param config
	 */
	private void initResvnStns(Configuration config) {
		int numALUUnits = config.getInt("ALU units");
		resvnStns = new ReservationStation[numALUUnits + 2];	//one for branch, one for LD/SD
		
		for(int i=0; i<numALUUnits; i++){
			resvnStns[i] = new ReservationStation(this, config.getInt("ALU reservation station entries"));
		}
		
		resvnStns[numALUUnits] = new ReservationStation(this, config.getInt("LD/SD reservation station entries"));
		resvnStns[numALUUnits+1] = new ReservationStation(this, config.getInt("Branch reservation station entries"));
	}
	
	private void initPipelineRegs(){
		PC = 0;
		PCnew = 0;
		PCcjp = -1;
		PCjmp = -1;
		PCjmpv = false;
		PCcjpv = false;
		inStallNew = false;
		inStall = false;
	}
}
