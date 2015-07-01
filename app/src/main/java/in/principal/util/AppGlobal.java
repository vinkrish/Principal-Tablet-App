package in.principal.util;

import in.principal.sqlite.SqlDbHelper;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AppGlobal {
	private static boolean mActive = false;
	private static Context mContext;
	private static Activity mActivity;
	private static SqlDbHelper mSqlDbHelper;
	private static SQLiteDatabase mSqliteDatabase;

	public static boolean isActive(){
		return mActive;
	}
	
	public static void setActive(boolean active){
		mActive = active;
	}
	
	public static Context getContext() {
		return mContext;
	}

	public static void setContext(Context context) {
		mContext = context;
	}
	
	public static void setActivity(Activity activity){
		mActivity = activity;
	}

	public static Activity getActivity() {
		return mActivity;
	}

	public static SqlDbHelper getSqlDbHelper() {
		return mSqlDbHelper;
	}

	public static void setSqlDbHelper(Context context) {
		mSqlDbHelper = SqlDbHelper.getInstance(context);
	}

	public static SQLiteDatabase getSqliteDatabase() {
		return mSqliteDatabase;
	}

	public static void setSqliteDatabase(Context context) {
		mSqlDbHelper = SqlDbHelper.getInstance(context);
		mSqliteDatabase = mSqlDbHelper.getWritableDatabase();
	}

}
