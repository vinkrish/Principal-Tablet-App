package in.principal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CceTopicPrimaryDao {
	
	public static String getTopicName(int topicId, SQLiteDatabase sqliteDatabase){
		String topicName = "";
		Cursor c = sqliteDatabase.rawQuery("select TopicName from ccetopicprimary where TopicId="+topicId, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			topicName = c.getString(c.getColumnIndex("TopicName"));
			c.moveToNext();
		}
		c.close();
		return topicName;
	}
}
