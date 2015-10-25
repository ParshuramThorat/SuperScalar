
public class ARFEntry extends BufferEntry{
	boolean busy;
	int tag;
	short data;
	
	ARFEntry(short initialValue){
		busy = false;
		tag = 0;
		data = initialValue;
	}
}
