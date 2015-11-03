
public class ReservationStation{

	int size;
	ReservationStationEntry[] entries;
	Pipeline parent;
	
	public ReservationStation(Pipeline parent, int numEntries) {
		entries = new ReservationStationEntry[numEntries];
		size = numEntries;
		this.parent = parent;
	}
	
	public int Add(ReservationStationEntry entry)
	{
		for(int i=0; i<size; i++){
			if(entries[i] == null){
				entries[i] = entry;
				return i;
			}
		}
		return -1;
	}
	
	public void Remove(ReservationStationEntry entry)
	{
		for(int i=0; i<size; i++){
			if(entries[i] == entry){
				entries[i] = null;
			}
		}
	}
	
	public boolean isFull()
	{
		for(int i=0; i<size; i++){
			if(entries[i]==null){
				return false;
			}
		}
		return true;
	}
	
	public void listenCDB()
	{
		short data;
		ReservationStationEntry ent;
		
		for (short cdbPC:parent.cdb.entries.keySet()){
			data = parent.cdb.entries.get(cdbPC);
			
			for(int i=0; i<size; i++){
				ent = entries[i];
				if(ent!=null){
					if(!ent.valid[0]){
						if(cdbPC==ent.operand[0]){	//operand bits contain pc when valid is 0
							ent.operand[0] = data;
							ent.valid[0] = true;
						}
					}
					if(!ent.valid[1]){
						if(cdbPC==ent.operand[1]){
							ent.operand[1] = data;
							ent.valid[1] = true;
						}
					}
					if(ent.valid[0] && ent.valid[1])
						ent.ready = true;
				}
			}
		}
	}

}
