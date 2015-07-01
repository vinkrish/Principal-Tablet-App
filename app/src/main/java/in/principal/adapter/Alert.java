package in.principal.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

public class Alert {
	Activity vinActivity;
	Context context;

	public Alert(Activity act){
		this.vinActivity = act;
	}

	public void showAlert(String message){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(vinActivity);
		alertDialog.setCancelable(true);
		alertDialog.setTitle("Notification");
		alertDialog.setMessage(message);
		/*alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});*/
		alertDialog.show();
	}

}
