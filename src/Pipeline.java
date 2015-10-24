
public class Pipeline {
	String logFilename;
	Configuration config;
	
	PipelineUnit[] units; //all stages, functional units, register files
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
	StoreBuffer storeBufr;
	
	CommonDataBus cdb;
	
	ReservationStation[] resvnStns;
	
	ArchitecturalRegisterFile arf;
	RenameRegisterFile rrf;
	
	public void run() {
		fetch.step();
		dcode.step();
		dspch.step();
		for (PipelineUnit fnUnit:fnUnits){
			fnUnit.step();
		}
		cmplet.step();
		retire.step();
	}
	
	public Pipeline(String logFilename, Configuration config)
	{
		this.logFilename = logFilename;
		this.config = config;
		
		initPipelineUnits(config);
		initBuffers(config);
		initResvnStns(config);
		
		arf = new ArchitecturalRegisterFile(config);
		cdb = new CommonDataBus();
	}

	/**
	 * @param config
	 */
	private void initPipelineUnits(Configuration config) {
		load = new LoadUnit();
		store = new StoreUnit();
		branch = new BranchUnit();
		
		int numALUUnits = config.getInt("ALU units");
		alus = new ALUUnit[numALUUnits];
		for(int i=0; i<numALUUnits; i++){
			alus[i] = new ALUUnit();
		}
		
		int numFnUnits = numALUUnits+3;	//1 LD, 1 SD, 1 branch
		fnUnits = new PipelineUnit[numFnUnits];
		System.arraycopy(alus, 0, fnUnits, 0, numALUUnits);
		fnUnits[numALUUnits] = load;	fnUnits[numALUUnits+1] = store;	fnUnits[numALUUnits+2] = branch;
		
		
		int width = config.getInt("Width");
		fetch = new FetchUnit(width);
		dcode = new DecodeUnit(width);
		dspch = new DispatchUnit(width);
		cmplet = new CompleteUnit(width);
		retire = new RetireUnit(width);
		
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
		dcodeBufr = new DecodeBuffer(width);
		dspchBufr = new DispatchBuffer(width);
		reodrBufr = new ReorderBuffer(width);
		storeBufr = new StoreBuffer(width);
		allBufrs = new PipelineBuffer[] {dcodeBufr, dspchBufr, reodrBufr, storeBufr};
	}

	/**
	 * @param config
	 */
	private void initResvnStns(Configuration config) {
		int numALUUnits = config.getInt("ALU units");
		resvnStns = new ReservationStation[numALUUnits + 2];	//one for branch, one for LD/SD
		
		for(int i=0; i<numALUUnits; i++){
			resvnStns[i] = new ReservationStation( config.getInt("ALU reservation station entries"));
		}
		
		resvnStns[numALUUnits] = new ReservationStation( config.getInt("LD/SD reservation station entries"));
		resvnStns[numALUUnits+1] = new ReservationStation( config.getInt("Branch reservation station entries"));
	}
	
}
