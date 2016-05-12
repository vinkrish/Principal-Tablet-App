package in.principal.activity;

import in.principal.sync.FirstTimeDownload;
import in.principal.util.NetworkUtils;
import in.principal.util.SharedPreferenceUtil;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by vinkrish.
 */
public class ServerBlock extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_block);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button butResolve = (Button) findViewById(R.id.butResolve);
        resolveClicked(butResolve);
    }

    public void resolveClicked(View view) {
        SharedPreferenceUtil.updateFirstSync(this, 1);
        if (NetworkUtils.isNetworkConnected(ServerBlock.this)) {
            new FirstTimeDownload().callFirstTimeSync();
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
