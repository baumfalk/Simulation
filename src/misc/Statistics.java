package misc;

import java.util.ArrayList;
import java.util.HashMap;

public class Statistics {
	private HashMap<String,Tuple<Float,Integer,String>> stats;
	
	public Statistics() {
		stats = new HashMap<String, Tuple<Float,Integer,String>>();
		
	}
	
	public void addStatistic(String s, Float f,String type)
	{
		addStatistic(s,f,0,type);
	}
	
	public void addStatistic(String s, Float f, int i, String type)
	{
		Tuple<Float,Integer,String> pair = new Tuple<Float,Integer,String>(f,i,type);
		
		stats.put(s, pair);
	}
	
	public void addToStatistic(String s, float f) {
		
		float newAverage = f;
		String type = "";
		if(stats.get(s) != null) {
			newAverage += getStatistic(s).first; 
			type = getStatistic(s).third;
		}
		
		updateStatistic(s, newAverage,0,type);
	}
	
	public void updateStatistic(String s, Float f, int i, String type)
	{
		addStatistic(s,f,i, type);
	}
	
	public void updateAverage(String s, int f) {
		// weighted average
		float newAverage = (getStatistic(s).first*getStatistic(s).second+f)/(getStatistic(s).second+1);
		updateStatistic(s, newAverage,getStatistic(s).second+1,getStatistic(s).third);
	}
	
	public Tuple<Float,Integer,String> getStatistic(String s) {
		return stats.get(s);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("The following statistics have been gathered\r\n");
		for(String s : stats.keySet())
		{
			sb.append("\t");
			sb.append(s);
			sb.append(":\r\n\t\t");
			sb.append(stats.get(s).first);
			sb.append(" (").append(stats.get(s).third).append(")\r\n");
		}
		return sb.toString();
	}

	public ArrayList<String> getStatisticList() {
		ArrayList<String> list = new ArrayList<String>();
		for(String s : stats.keySet())
		{
			list.add(s+":\t"+stats.get(s).first+" ("+stats.get(s).third+")\r\n");
		}
		return list;
	}
}
