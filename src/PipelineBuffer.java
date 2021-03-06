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
	
	public boolean Add(BufferEntry entry)
	{
		if(entries.size() + additns.size() - deletns.size() >= size){
			return false;
		}
		else{
			additns.add(entry);
			return true;
		}
	}
	
	public BufferEntry Get(int index)
	{
		try{
			return entries.get(index);
		}
		catch (IndexOutOfBoundsException e){
			return null;
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
	
	public boolean isFull()
	{
		if(size > entries.size())	return false;
		else						return true;
	}
}
