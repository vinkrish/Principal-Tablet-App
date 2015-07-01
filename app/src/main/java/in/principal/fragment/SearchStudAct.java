package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.StudActAdapter;
import in.principal.dao.ActivitiDao;
import in.principal.dao.SubActivityDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.Activiti;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SearchStudAct extends Fragment {
	private Context context;
	private int studentId, sectionId, examId, subjectId;
	private String studentName, className, secName;
	private static SQLiteDatabase sqliteDatabase;
	private List<Integer> actIdList = new ArrayList<Integer>();
	private List<String> actNameList = new ArrayList<String>();
	private List<Integer> avgList1 = new ArrayList<Integer>();
	private List<Integer> avgList2 = new ArrayList<Integer>();
	private List<Activiti> activitiList = new ArrayList<Activiti>();
	private List<AdapterOverloaded> amrList = new ArrayList<AdapterOverloaded>();;
	private StudActAdapter adapter;
	private ListView lv;
	private ProgressDialog pDialog;
	private TextView studTV, clasSecTV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.search_se_act, container, false);
		context = AppGlobal.getContext();
		sqliteDatabase = AppGlobal.getSqliteDatabase();
		pDialog  = new ProgressDialog(this.getActivity());
		
		clearList();

		studTV = (TextView)view.findViewById(R.id.studName);
		clasSecTV = (TextView)view.findViewById(R.id.studClasSec);
		lv = (ListView)view.findViewById(R.id.list);
		
		adapter = new StudActAdapter(context, R.layout.search_act_list, amrList);
		lv.setAdapter(adapter);
		
		TextView slipTV = (TextView)view.findViewById(R.id.slipSearch);
		slipTV.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				ReplaceFragment.replace(new SearchStudST(), getFragmentManager());
			}
		});

		TextView attTV = (TextView)view.findViewById(R.id.attSearch);
		attTV.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
			}
		});
		
		Temp t = TempDao.selectTemp(sqliteDatabase);
		studentId = t.getStudentId();
		examId = t.getExamId();
		subjectId = t.getSubjectId();
		
		new CalledBackLoad().execute();
		
		return view;
	}
	
	class CalledBackLoad extends AsyncTask<String, String, String>{
		protected void onPreExecute(){
			super.onPreExecute();
			pDialog.setMessage("Preparing data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			Cursor c = sqliteDatabase.rawQuery("select A.Name, A.ClassId, A.SectionId, B.ClassName, C.SectionName from students A, class B, section C where"+
					" A.StudentId="+studentId+" and A.ClassId=B.ClassId and A.SectionId=C.SectionId group by A.StudentId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				studentName = c.getString(c.getColumnIndex("Name"));
				sectionId = c.getInt(c.getColumnIndex("SectionId"));
				className = c.getString(c.getColumnIndex("ClassName"));
				secName = c.getString(c.getColumnIndex("SectionName"));
				c.moveToNext();
			}
			c.close();
			
			activitiList = ActivitiDao.selectActiviti(examId,subjectId,sectionId,sqliteDatabase);
			for(Activiti act: activitiList){
				int cache = SubActivityDao.isThereSubAct(act.getActivityId(), sqliteDatabase);
				if(cache==1){
					avgList1.add(0);
				}else{
					avgList1.add(ActivitiDao.getStudActAvg(studentId	, act.getActivityId(), sqliteDatabase));
				}
			}
			for(Activiti at: activitiList){
				actNameList.add(at.getActivityName());
				actIdList.add(at.getActivityId());
				int i = (int)(((double)at.getActivityAvg()/(double)360)*100);
				avgList2.add(i);
			}

			for(int i=0; i<actIdList.size(); i++){
				try{
					amrList.add(new AdapterOverloaded(actNameList.get(i),avgList1.get(i),avgList2.get(i)));
				}catch(IndexOutOfBoundsException e){
					amrList.add(new AdapterOverloaded(actNameList.get(i),0,0));
				}
			}
			return null;
		}
		protected void onPostExecute(String s){
			super.onPostExecute(s);
			studTV.setText(studentName);
			clasSecTV.setText(className+" - "+secName);
			adapter.notifyDataSetChanged();
			pDialog.dismiss();
		}
	}

	private void clearList(){
		actIdList.clear();
		activitiList.clear();
		avgList1.clear();
		avgList2.clear();
		actNameList.clear();
		amrList.clear();
	}

}
