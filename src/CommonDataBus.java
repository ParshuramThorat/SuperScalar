import java.util.HashMap;

public class CommonDataBus {
	HashMap<Integer,Short>	entries;
	
	//assuming infinite capacity
	public void Insert(int tag, short data)
	{
		entries.put(tag, data);
	}
	
	public HashMap<Integer, Short> Get()
	{
		return entries;
	}
	
	public void Clear()
	{
		entries.clear();
	}
}
