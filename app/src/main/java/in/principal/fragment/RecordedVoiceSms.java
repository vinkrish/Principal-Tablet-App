package in.principal.fragment;

import in.principal.activity.R;
import in.principal.util.ReplaceFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RecordedVoiceSms extends Fragment {	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.recorded_voice_sms, container, false);
		
		Button voiceSmsBtn = (Button)view.findViewById(R.id.voiceSms);
		voiceSmsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ReplaceFragment.replace(new VoiceSms(), getFragmentManager());
			}
		});
		
		return view;
		
	}

}
