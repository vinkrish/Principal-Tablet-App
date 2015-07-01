package in.principal.util;

import in.principal.dao.SectionDao;
import in.principal.sqlite.SqlDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

public class CustomSort {
	static SqlDbHelper sqlHandler;
	
	public static List<Integer> sortSecName(List<Integer> sectionIdList, SQLiteDatabase sqliteDatabase){
		Map<String, Integer> m = new HashMap<String, Integer>();
		List<Integer> sortedSecList = new ArrayList<Integer>();
		List<String> secNameList = new ArrayList<String>();
		for(Integer id: sectionIdList){
			String s = SectionDao.getSecName(id, sqliteDatabase);
			secNameList.add(s);
			m.put(s, id);
		}
		Collections.sort(secNameList);
		for(String name: secNameList){
			sortedSecList.add(m.get(name));
		}
		return sortedSecList;
	}

}
