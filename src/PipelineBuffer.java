import java.util.ArrayList;

public abstract class PipelineBuffer {
	int size;
	ArrayList<BufferEntry> entries;
	ArrayList<BufferEntry> additns;
	ArrayList<BufferEntry> deletns;
	
	public PipelineBuffer(int width)
	{
		size = width;
		entries = new ArrayList<BufferEntry>();
		additns = new ArrayList<BufferEntry>();
		deletns = new ArrayList<BufferEntry>();
	}
	
	public int Add(BufferEntry entry)
	{
		if(entries.size() + additns.size() - deletns.size() >= size){
			return 0;
		}
		else{
			additns.add(entry);
			return 1;
		}
	}
	
	public void Remove(BufferEntry entry)
	{
		deletns.add(entry);
	}
	
	public void Update()
	{
		entries.removeAll(deletns);
		entries.addAll(additns);
		additns.clear();
		deletns.clear();
	}
}
