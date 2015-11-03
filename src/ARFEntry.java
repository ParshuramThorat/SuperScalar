
public class ARFEntry extends BufferEntry{
	boolean busy;
	int pcTag;
	short data;
	
	ARFEntry(short initialValue){
		busy = false;
		pcTag = -1;
		data = initialValue;
	}
}
