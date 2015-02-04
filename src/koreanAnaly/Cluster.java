package koreanAnaly;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private int sizeOfCluster = 0;
	private List<Integer> set = new ArrayList<Integer>();
	
	public void put(Integer i){
		set.add(i);
		sizeOfCluster++;
	}
	
	public void put(int i){
		Integer integer = Integer.valueOf(i);
		set.add(integer);
		sizeOfCluster++;
	}
	
	public Integer[] getElements(){
		Integer[] elements = new Integer[set.size()];
		Object[] elementsObject =  set.toArray();
		for(int cnt=0; cnt<elementsObject.length; cnt++){
			elements[cnt] = (Integer)elementsObject[cnt];
		}
		return elements;
	}
	
	public boolean isSubsetOf(Cluster c){
		Object[] thisSet = set.toArray();
		List<Integer> compareSet = c.set;
		
		int check = 0;
		for(Object o : thisSet){
			if(compareSet.contains(o)) check++;
		}
		if(sizeOfCluster == check){
			return true;
		}
		else{
			return false;
		}
	}
	
	public int size(){
		return sizeOfCluster;
	}
	
	public void print(){
		String print = "{";
		Object[] elements = set.toArray();
		for(int cnt=0; cnt<elements.length; cnt++){
			if(cnt==elements.length-1){
				print+=elements[cnt]+"}";
			}
			else{
				print+=elements[cnt]+",";
			}
		}
		System.out.println(print);
	}
}
