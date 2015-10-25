
public class ReorderBufferEntry extends BufferEntry {
	boolean issued;
	boolean finished;
	int tag;
	short pc;
	short data;
}
