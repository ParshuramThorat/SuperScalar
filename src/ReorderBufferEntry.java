
public class ReorderBufferEntry extends BufferEntry {
	boolean issued;
	boolean finished;
	short pc;
	short data;
}
