
public class ReservationStationEntry extends BufferEntry {
	boolean busy;
	short[] operand;
	boolean[] valid;
	short pc;
	boolean ready;
	boolean id;	//id is true for load and conditional jump
	
	public ReservationStationEntry()
	{
		busy = false;
		operand = new short[2];
		valid = new boolean[2];
		ready = false;
	}
}
