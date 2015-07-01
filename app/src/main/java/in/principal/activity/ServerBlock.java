package in.principal.activity;

import in.principal.activity.R;
import in.principal.sync.FirstTimeSync;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class ServerBlock extends BaseActivity {
	private Button butResolve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_block);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		butResolve = (Button)findViewById(R.id.butResolve);		
		resolveClicked(butResolve);
	}
	
	public void resolveClicked(View view){
		SharedPreferences sharedPref = this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("first_sync", 1);
		editor.apply();
		
		new FirstTimeSync().callFirstTimeSync();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		SharedPreferences sp = this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("first_sync", 0);
		editor.apply();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.server_block, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){}
}
