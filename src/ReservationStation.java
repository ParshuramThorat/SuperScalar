
public class ReservationStation{

	int size;
	ReservationStationEntry[] entries; 
	public ReservationStation(int numEntries) {
		entries = new ReservationStationEntry[numEntries];
		size = numEntries;
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
	
//	public BufferEntry Get(int index)
//	{
//		try{
//			return entries.get(index);
//		}
//		catch (IndexOutOfBoundsException e){
//			return null;
//		}
//	}
	
	public void Remove(ReservationStationEntry entry)
	{
		for(int i=0; i<size; i++){
			if(entries[i] == entry){
				entries[i] = null;
			}
		}
	}
	
//	public void Update()
//	{
//		entries.removeAll(deletns);
//		entries.addAll(additns);
//		additns.clear();
//		deletns.clear();
//	}
	
	public boolean isFull()
	{
		for(int i=0; i<size; i++){
			if(entries[i]==null){
				return false;
			}
		}
		return true;
	}

}
