package in.principal.fragment;

import in.principal.activity.R;
import in.principal.adapter.Alert;
import in.principal.dao.ClasDao;
import in.principal.dao.SectionDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Clas;
import in.principal.sqlite.Section;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StReport extends Fragment {
	private Context context;
	private Activity act;
	private int classId, index;
	private String fromDate, toDate ;
	private TextView dateTV1, dateTV2, clasTV, secTV;
	private String[] items, items2;
	private int secId;
	private int clasId;
	private AlertDialog alertDialog;
	private SQLiteDatabase sqliteDatabase;
	private List<Section> secList = new ArrayList<>();
	private List<Integer> secIdList = new ArrayList<>();
	private List<String> secNameList = new ArrayList<>();
	private List<Clas> clasList = new ArrayList<>();
	private List<Integer> classIdList = new ArrayList<>();
	private List<String> classNameList = new ArrayList<>();
	private List<Long> slipIdList = new ArrayList<>();
	private List<String> slipCSList = new ArrayList<>();
	private List<String> slipSubList = new ArrayList<>();
	private List<String> slipPorList = new ArrayList<>();
	private List<Integer> slipProgList = new ArrayList<>();
	private List<String> csDate = new ArrayList<>();
	private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
	private StDash stDashAdapter;
	private RadioGroup evaluationType;
	private boolean createFlag = true;
	private LinearLayout ll1, ll2;
	private boolean datePicker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.st_report, container, false);
		act = AppGlobal.getActivity();
		context = AppGlobal.getContext();
		sqliteDatabase = AppGlobal.getSqliteDatabase();
		ListView lv = (ListView)view.findViewById(R.id.list);

		evaluationType = (RadioGroup)view.findViewById(R.id.evaluationType);
		stDashAdapter = new StDash(context, R.layout.st_dash_list, amrList);
		lv.setAdapter(stDashAdapter);

		ll1 = (LinearLayout)view.findViewById(R.id.datePicker1);
		ll1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datePicker = true;
				secId = 0;
				clasTV.setText("Class");
				secTV.setText("Section");
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});
		
		ll2 = (LinearLayout)view.findViewById(R.id.datePicker2);
		ll2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				datePicker = false;
				secId = 0;
				clasTV.setText("Class");
				secTV.setText("Section");
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

		fromDate = getToday();
		toDate = getToday();

		dateTV1 = (TextView) view.findViewById(R.id.datetxt1);
		dateTV2 = (TextView) view.findViewById(R.id.datetxt2);
		secTV = (TextView) view.findViewById(R.id.sectxt);
		clasTV = (TextView) view.findViewById(R.id.clastxt);
		dateTV1.setText(fromDate);
		dateTV2.setText(toDate);

		secList = SectionDao.selectSection(classId, sqliteDatabase);
		for(Section s: secList){
			secIdList.add(s.getSectionId());
			secNameList.add(s.getSectionName());
		}

		clasList = ClasDao.selectClas(sqliteDatabase);
		for(Clas c: clasList){
			classIdList.add(c.getClassId());
			classNameList.add(c.getClassName());
		}

		items = classNameList.toArray(new String[classNameList.size()]);
		LinearLayout selecClass = (LinearLayout)view.findViewById(R.id.classPicker);
		selecClass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(classIdList.size()>0){
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle("Select class");
					builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							index = which;
							alertDialog.dismiss();
							secId = 0;
							secTV.setText("Section");
							clasId = classIdList.get(index);
							clasTV.setText(classNameList.get(index));
							secList.clear();secIdList.clear();secNameList.clear();
							secList = SectionDao.selectSection(classIdList.get(index), sqliteDatabase);
							for(Section s: secList){
								secIdList.add(s.getSectionId());
								secNameList.add(s.getSectionName());
							}
							items2 = secNameList.toArray(new String[secNameList.size()]);
							stDashAdapter.clear();
							updateSelectedLis();
						}

					});
					alertDialog = builder.create();
					alertDialog.show();	
				}else{
					Alert alert = new Alert(act);
					alert.showAlert("No sliptest for this class on this day.");
				}
			}
		});

		items2 = secNameList.toArray(new String[secNameList.size()]);
		LinearLayout selecSec = (LinearLayout)view.findViewById(R.id.sectionPicker);
		selecSec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(secIdList.size()>0 && !clasTV.getText().equals("Class")){
					AlertDialog.Builder builder = new AlertDialog.Builder(act);
					builder.setTitle("Select section");
					builder.setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							index = which;
							alertDialog.dismiss();
							secId = secIdList.get(index);
							if(secIdList.size()>0){
								secTV.setText(secNameList.get(index));
								updateSelectedList();
							}
						}

					});
					alertDialog = builder.create();
					alertDialog.show();
				}else if(clasTV.getText().equals("Class")){
					Alert alert = new Alert(act);
					alert.showAlert("Please select class first.");
				}else{
					Alert alert = new Alert(act);
					alert.showAlert("No sliptest for this section on this day.");
				}
			}
		});

		updateFirstList();
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				TempDao.updateSlipId(slipIdList.get(pos), sqliteDatabase);
				ReplaceFragment.replace(new PerfST(), getFragmentManager());
			}
		});
		
		evaluationType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.creation:
					createFlag = true;
					secId = 0;
					clasTV.setText("Class");
					secTV.setText("Section");
					if(secId!=0){
						updateSelectedList();
					}else{
						updateFirstList();
					}
					break;
				case R.id.submission:
                	createFlag = false;
                	secId = 0;
                	clasTV.setText("Class");
                	secTV.setText("Section");
                	if(secId!=0){
        				updateSelectedList();
        			}else{
        				updateFirstList();
        			}
					break;
				}
			}
		});
		return view;
	}

	private void clearFirstList(){
		amrList.clear();
		slipIdList.clear();
		slipCSList.clear();
		slipSubList.clear();
		slipPorList.clear();
		slipProgList.clear();
		csDate.clear();
	}
	
	private String getToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date today = new Date();
		return dateFormat.format(today);
	}

	private void updateFirstList(){
		clearFirstList();
		if(createFlag){
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.SubmissionDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.TestDate>='"+fromDate+"' and A.TestDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("SubmissionDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}else{
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.TestDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.SubmissionDate>='"+fromDate+"' and A.SubmissionDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("TestDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}

		for(int i=0,j=slipIdList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(slipCSList.get(i),slipSubList.get(i),slipPorList.get(i),csDate.get(i),slipProgList.get(i)));
		}
		stDashAdapter.notifyDataSetChanged();
	}

	private void updateSelectedList(){
		clearFirstList();
		if(createFlag){
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.SubmissionDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.TestDate>='"+fromDate+"' and A.TestDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId and A.ClassId="+clasId+" and A.SectionId="+secId+" group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("SubmissionDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}else{
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.TestDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.SubmissionDate>='"+fromDate+"' and A.SubmissionDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId and A.ClassId="+clasId+" and A.SectionId="+secId+" group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("TestDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}
		
		for(int i=0,j=slipIdList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(slipCSList.get(i),slipSubList.get(i),slipPorList.get(i),csDate.get(i),slipProgList.get(i)));
		}
		stDashAdapter.notifyDataSetChanged();
	}
	
	private void updateSelectedLis(){
		clearFirstList();
		if(createFlag){
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.SubmissionDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.TestDate>='"+fromDate+"' and A.TestDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId and A.ClassId="+clasId+" group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("SubmissionDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}else{
			Cursor c = sqliteDatabase.rawQuery("select A.SlipTestId,A.TestDate,A.ClassId,A.SectionId,A.SubjectId,A.PortionName, (A.AverageMark/A.MaximumMark)*100 as avg, B.ClassName,"+
					"C.SectionName,D.SubjectName from sliptest A, Class B, Section C, subjects D where A.SubmissionDate>='"+fromDate+"' and A.SubmissionDate<='"+toDate+"' and A.ClassId=B.ClassId and A.ClassId=C.ClassId and "+
					"A.SectionId=C.SectionId and A.SubjectId=D.SubjectId and A.ClassId="+clasId+" group by A.SlipTestId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				slipIdList.add(c.getLong(c.getColumnIndex("SlipTestId")));
				csDate.add(c.getString(c.getColumnIndex("TestDate")));
				slipCSList.add(c.getString(c.getColumnIndex("ClassName"))+"  "+c.getString(c.getColumnIndex("SectionName")));
				slipSubList.add(c.getString(c.getColumnIndex("SubjectName")));
				slipPorList.add(c.getString(c.getColumnIndex("PortionName")));
				slipProgList.add(c.getInt(c.getColumnIndex("avg")));
				c.moveToNext();
			}
			c.close();
		}
		
		for(int i=0,j=slipIdList.size(); i<j; i++){
			amrList.add(new AdapterOverloaded(slipCSList.get(i),slipSubList.get(i),slipPorList.get(i),csDate.get(i),slipProgList.get(i)));
		}
		stDashAdapter.notifyDataSetChanged();
	}

	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {        
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			
			if(view.isShown()){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(year,month,day);
				Date d = cal.getTime();

				if(GregorianCalendar.getInstance().get(Calendar.YEAR)<cal.get(Calendar.YEAR)){
					Alert alert = new Alert(act);
					alert.showAlert("Selected future date !");
				}else if(GregorianCalendar.getInstance().get(Calendar.MONTH)<cal.get(Calendar.MONTH) && GregorianCalendar.getInstance().get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
					Alert alert = new Alert(act);
					alert.showAlert("Selected future date !");
				}else if(GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)<cal.get(Calendar.DAY_OF_MONTH) && 
						GregorianCalendar.getInstance().get(Calendar.MONTH)<=cal.get(Calendar.MONTH) && GregorianCalendar.getInstance().get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
					Alert alert = new Alert(act);
					alert.showAlert("Selected future date !");
				}else if(Calendar.SUNDAY==cal.get(Calendar.DAY_OF_WEEK)){
					Alert alert = new Alert(act);
					alert.showAlert("Sundays are not working days.");
				}else{
					if(datePicker){
						fromDate = dateFormat.format(d);
						dateTV1.setText(fromDate);
					}else{
						toDate = dateFormat.format(d);
						dateTV2.setText(toDate);
					}
				}
				if(secId!=0){
					updateSelectedList();
				}else{
					updateFirstList();
				}
			}			
		}
	}
	
	public class StDash extends ArrayAdapter<AdapterOverloaded> {
		int resource;
		Context context;
		ArrayList<AdapterOverloaded> data = new ArrayList<>();

		public StDash(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
			super(context, resource, listArray);
			this.context = context;
			this.resource = resource;
			this.data = listArray;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RecordHolder holder;

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(resource, parent, false);

				holder = new RecordHolder();
				holder.tv1 = (TextView)row.findViewById(R.id.li_txt1);
				holder.tv2 = (TextView) row.findViewById(R.id.li_txt2);
				holder.tv3 = (TextView) row.findViewById(R.id.li_txt3);
				holder.tv4 = (TextView) row.findViewById(R.id.li_txt4);
				holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
				holder.percentage = (TextView) row.findViewById(R.id.percent);
				row.setTag(holder);
			}else
				holder = (RecordHolder) row.getTag();

			if(position % 2 == 0)
				row.setBackgroundResource(R.drawable.list_selector1);
			else
				row.setBackgroundResource(R.drawable.list_selector2);

			AdapterOverloaded listItem = data.get(position);
			holder.tv1.setText(listItem.getText1());
			holder.tv2.setText(listItem.getText2());
			holder.tv3.setText(listItem.getText3());
			holder.tv4.setText(listItem.getText4());

			if(listItem.getInt1()>=75){
				holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
			}else if(listItem.getInt1()>=50){
				holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
			}else{
				holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
			}
			holder.pb.setProgress(listItem.getInt1());
			holder.percentage.setText(String.valueOf(listItem.getInt1()+"%"));

			return row;
		}

		class RecordHolder{
			TextView tv1;
			TextView tv2;
			TextView tv4;
			TextView tv3;
			ProgressBar pb;
			TextView percentage;
		}

	}

}
