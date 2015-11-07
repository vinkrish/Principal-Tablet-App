package in.principal.activity;

import in.principal.activity.R;
import in.principal.sync.FirstTimeSync;
import in.principal.util.NetworkUtils;
import in.principal.util.SharedPreferenceUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by vinkrish.
 */
public class ServerBlock extends BaseActivity {
    private Button butResolve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_block);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        butResolve = (Button) findViewById(R.id.butResolve);
        resolveClicked(butResolve);
    }

    public void resolveClicked(View view) {
        SharedPreferenceUtil.updateFirstSync(this, 1);
        if (NetworkUtils.isNetworkConnected(ServerBlock.this)) {
            new FirstTimeSync().callFirstTimeSync();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceUtil.updateFirstSync(this, 0);
    }

    @Override
    public void onBackPressed() {
    }
}
