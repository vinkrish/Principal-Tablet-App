package in.principal.activity;

import in.principal.util.AppGlobal;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareGlobal();
	}
	
	public void prepareGlobal(){
		AppGlobal.setActive(true);
		AppGlobal.setActivity(this);
		AppGlobal.setContext(getApplicationContext());
		AppGlobal.setSqlDbHelper(getApplicationContext());
		AppGlobal.setSqliteDatabase(getApplicationContext());
	}
	
	@Override
	protected void onResume(){
		prepareGlobal();
		super.onResume();
	}

	@Override
	protected void onStart(){
		super.onStart();
		prepareGlobal();
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		prepareGlobal();
	}

}
