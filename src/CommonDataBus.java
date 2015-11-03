import java.util.HashMap;

public class CommonDataBus {
	HashMap<Short,Short>	entries;
	
	public CommonDataBus(){
		entries = new HashMap<>();
	}
	
	//assuming infinite capacity
	public void Insert(Short pc, short data)
	{
		entries.put(pc, data);
	}
	
	public HashMap<Short, Short> Get()
	{
		return entries;
	}
	
	public void Clear()
	{
		entries.clear();
	}
}
