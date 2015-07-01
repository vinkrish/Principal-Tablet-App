package in.principal.adapter;

import in.principal.sqlite.AdapterOverloaded;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PerformerSort implements Comparator<AdapterOverloaded> {
	@Override
	public int compare(AdapterOverloaded lhs, AdapterOverloaded rhs) {
		return lhs.getInt2()-rhs.getInt2();
	}
	
	public static Comparator<AdapterOverloaded> progressComparator = new Comparator<AdapterOverloaded>(){
		@Override
		public int compare(AdapterOverloaded amr1, AdapterOverloaded amr2){
			return amr1.getInt2()-amr2.getInt2();
		}
	};
	
	public static List<AdapterOverloaded> sortThis(List<AdapterOverloaded> amrList){
		List<AdapterOverloaded> sortedList = new ArrayList<AdapterOverloaded>();
		for(int i=0,j=amrList.size()-1; i<j; i++){
			AdapterOverloaded s = null;
			for(int k=amrList.size()-i-1; k>0; k--){
				int idx = i+1;
				AdapterOverloaded s1 = amrList.get(i);
				AdapterOverloaded s2 = amrList.get(idx);
				if(s2.getInt2()<s1.getInt2()){
					AdapterOverloaded temp = s2;
					s = s2;
					amrList.set(i, s2);
					amrList.set(idx, temp);
				}else{
					s = s1;
				}
				idx+=1;
			}
			sortedList.add(s);
		}
		return sortedList;
	}
}
