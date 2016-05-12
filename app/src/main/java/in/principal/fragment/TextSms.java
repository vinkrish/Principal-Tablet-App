package in.principal.fragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

import in.principal.activity.R;
import in.principal.dao.ClasDao;
import in.principal.dao.SchoolDao;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.model.TransferModel;
import in.principal.sqlite.Clas;
import in.principal.sqlite.School;
import in.principal.sqlite.Section;
import in.principal.sqlite.Students;
import in.principal.sqlite.Temp;
import in.principal.sync.RequestResponseHandler;
import in.principal.sync.StringConstant;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.Constants;
import in.principal.util.PKGenerator;
import in.principal.util.ReplaceFragment;
import in.principal.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * I would write this class a better way if i've to start over again, optimize it if you can.
 */
@SuppressWarnings("deprecation")
@SuppressLint("ClickableViewAccessibility")
public class TextSms extends Fragment implements StringConstant {
    private Activity act;
    private SQLiteDatabase sqliteDatabase;
    private Button allStudentsBtn, allTeachersBtn, classBtn, sectionBtn, studentBtn, submitBtn, allClassTeachersBtn;
    private Button allMaleStudBtn, allFemaleStudBtn, allMaleTeacherBtn, allFemaleTeacherBtn;
    private FrameLayout studentTeacherFrame;
    private LinearLayout selectionFrame;
    private EditText classSpinner, sectionSpinner, studentSpinner, textSms;
    private TextView studentTeacherContext;
    private int classId, sectionId, principalId, schoolId;
    private ArrayList<Clas> clasList;
    private ArrayList<Section> secList;
    private ArrayList<Integer> classIdList;
    private ArrayList<Integer> secIdList;
    private ArrayList<Long> studIdList;
    private ArrayList<String> classNameList;
    private ArrayList<String> secNameList;
    private ArrayList<String> studNameList;
    private ArrayList<Long> idList = new ArrayList<>();

    protected boolean[] classSelections;
    protected boolean[] sectionSelections;
    protected boolean[] studentSelections;

    private String ids, zipName, deviceId;
    private int target;
    private ProgressDialog progressBar;

    private Context appContext;
    private boolean uploadComplete, exception;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_sms, container, false);

        initializeList();
        act = AppGlobal.getActivity();
        appContext = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        studentTeacherContext = (TextView) view.findViewById(R.id.stud_teacher_context);
        allStudentsBtn = (Button) view.findViewById(R.id.allStudents);
        allMaleStudBtn = (Button) view.findViewById(R.id.all_male_students);
        allFemaleStudBtn = (Button) view.findViewById(R.id.all_female_students);
        allTeachersBtn = (Button) view.findViewById(R.id.allTeachers);
        allMaleTeacherBtn = (Button) view.findViewById(R.id.all_male_teachers);
        allFemaleTeacherBtn = (Button) view.findViewById(R.id.all_female_teachers);
        classBtn = (Button) view.findViewById(R.id.clas);
        sectionBtn = (Button) view.findViewById(R.id.sec);
        studentBtn = (Button) view.findViewById(R.id.stud);
        submitBtn = (Button) view.findViewById(R.id.submit);
        allClassTeachersBtn = (Button) view.findViewById(R.id.classTeachers);

        studentTeacherFrame = (FrameLayout) view.findViewById(R.id.studentTeacherFrame);
        selectionFrame = (LinearLayout) view.findViewById(R.id.selectionFrame);

        classSpinner = (EditText) view.findViewById(R.id.classSpinner);
        sectionSpinner = (EditText) view.findViewById(R.id.secSpinner);
        studentSpinner = (EditText) view.findViewById(R.id.studSpinner);
        textSms = (EditText) view.findViewById(R.id.textSms);

        classSpinner.setOnTouchListener(classTouch);
        sectionSpinner.setOnTouchListener(sectionTouch);
        studentSpinner.setOnTouchListener(studentTouch);

        clasList = ClasDao.selectClas(sqliteDatabase);
        for (Clas c : clasList) {
            classIdList.add(c.getClassId());
            classNameList.add(c.getClassName());
        }
        classSelections = new boolean[clasList.size()];

        ArrayList<School> auth = SchoolDao.selectSchool(sqliteDatabase);
        for (School school : auth) {
            principalId = school.getPrincipalTeacherId();
        }

        allStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_students_mes));
                target = 0;
            }
        });

        allMaleStudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_male_stud_mes));
                target = 6;
            }
        });

        allFemaleStudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_female_stud_mes));
                target = 7;
            }
        });

        allTeachersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_teachers_mes));
                target = 1;
            }
        });

        allMaleTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_male_teachers_mes));
                target = 8;
            }
        });

        allFemaleTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_female_teachers_mes));
                target = 9;
            }
        });

        allClassTeachersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                submitBtn.setEnabled(true);
                studentTeacherFrame.setVisibility(View.VISIBLE);
                studentTeacherContext.setText(getResources().getText(R.string.all_class_teacher_mes));
                target = 5;
            }
        });

        classBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.INVISIBLE);
                studentSpinner.setVisibility(View.INVISIBLE);
                classSpinner.setText("");
                target = 2;
            }
        });

        sectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.VISIBLE);
                studentSpinner.setVisibility(View.INVISIBLE);
                classSpinner.setText("");
                sectionSpinner.setText("");
                for (int i = 0; i < classSelections.length; i++) {
                    classSelections[i] = false;
                }
                secList.clear();
                secIdList.clear();
                secNameList.clear();
                sectionSelections = new boolean[secIdList.size()];
                target = 3;
            }
        });

        studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deActivate();
                v.setActivated(true);
                selectionFrame.setVisibility(View.VISIBLE);
                sectionSpinner.setVisibility(View.VISIBLE);
                studentSpinner.setVisibility(View.VISIBLE);
                classSpinner.setText("");
                sectionSpinner.setText("");
                studentSpinner.setText("");
                for (int i = 0; i < classSelections.length; i++) {
                    classSelections[i] = false;
                }
                secList.clear();
                secIdList.clear();
                secNameList.clear();
                sectionSelections = new boolean[secIdList.size()];
                studIdList.clear();
                studNameList.clear();
                studentSelections = new boolean[studIdList.size()];
                target = 4;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                idList.clear();
                if (textSms.getText().toString().equals("")) {
                    CommonDialogUtils.displayAlertWhiteDialog(TextSms.this.getActivity(), "Please enter message to deliver");
                } else {
                    new CalledFTPSync().execute();
                }
            }
        });

        /*classSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ClassDialog classDialog = new ClassDialog();
                classDialog.show(ft, "dialog");
            }
        });
        */
        return view;
    }

    private void initializeList() {
        clasList = new ArrayList<>();
        secList = new ArrayList<>();
        classIdList = new ArrayList<>();
        secIdList = new ArrayList<>();
        studIdList = new ArrayList<>();
        classNameList = new ArrayList<>();
        secNameList = new ArrayList<>();
        studNameList = new ArrayList<>();
    }

    private void hideKeyboard() {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private OnTouchListener classTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showClassDialog();
            }
            int inType = classSpinner.getInputType();
            classSpinner.setInputType(InputType.TYPE_NULL);
            classSpinner.onTouchEvent(event);
            classSpinner.setInputType(inType);
            return false;
        }
    };

    private OnTouchListener sectionTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showSectionDialog();
            }
            int inType = sectionSpinner.getInputType();
            sectionSpinner.setInputType(InputType.TYPE_NULL);
            sectionSpinner.onTouchEvent(event);
            sectionSpinner.setInputType(inType);
            return false;
        }
    };


    private OnTouchListener studentTouch = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showStudentDialog();
            }
            int inType = studentSpinner.getInputType();
            studentSpinner.setInputType(InputType.TYPE_NULL);
            studentSpinner.onTouchEvent(event);
            studentSpinner.setInputType(inType);
            return false;
        }
    };

    private void deActivate() {
        allStudentsBtn.setActivated(false);
        allMaleStudBtn.setActivated(false);
        allFemaleStudBtn.setActivated(false);
        allTeachersBtn.setActivated(false);
        allMaleTeacherBtn.setActivated(false);
        allFemaleTeacherBtn.setActivated(false);
        allClassTeachersBtn.setActivated(false);
        classBtn.setActivated(false);
        sectionBtn.setActivated(false);
        studentBtn.setActivated(false);
        submitBtn.setEnabled(false);
        studentTeacherFrame.setVisibility(View.GONE);
        selectionFrame.setVisibility(View.GONE);
    }

    public void showClassDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Classes")
                .setCancelable(false)
                .setMultiChoiceItems(classNameList.toArray(new CharSequence[classIdList.size()]), classSelections, new ClassSelectionClickHandler())
                .setPositiveButton("OK", new ClassButtonClickHandler())
                .show();
    }

    public class ClassSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            boolean b = false;
            if (selected) b = true;
            if (sectionSpinner.isShown()) {
                for (int i = 0; i < classSelections.length; i++) {
                    classSelections[i] = false;
                }
                dialog.dismiss();
                classSelections[clicked] = b;
                showClassDialog();
            } else {
                if (selected) classSelections[clicked] = true;
                else classSelections[clicked] = false;
            }
        }
    }

    public class ClassButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    classSpinner.clearFocus();
                    selectedClass();
                    break;
            }
        }
    }

    protected void selectedClass() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb2 = new StringBuilder();
        if (!sectionSpinner.isShown() && !studentSpinner.isShown()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classIdList.size(); i++) {
                if (classSelections[i]) {
                    isSelected = true;
                    sb.append(classIdList.get(i) + ",");
                    sb2.append(classNameList.get(i) + ", ");
                }
            }
            if (isSelected) {
                classSpinner.setText(sb2.substring(0, sb2.length() - 2));
                ids = sb.substring(0, sb.length() - 1);
                submitBtn.setEnabled(true);
            } else {
                classSpinner.setText("");
            }
        } else if (sectionSpinner.isShown()) {
            for (int i = 0; i < classIdList.size(); i++) {
                if (classSelections[i]) {
                    isSelected = true;
                    classId = classIdList.get(i);
                    sb2.append(classNameList.get(i));
                    break;
                }
            }
            if (!isSelected) {
                classId = 0;
                classSpinner.setText("");
            } else {
                classSpinner.setText(sb2.toString());
            }
            sectionSpinner.setText("");
            secList.clear();
            secIdList.clear();
            secNameList.clear();
            secList = SectionDao.selectSection(classId, sqliteDatabase);
            for (Section s : secList) {
                secIdList.add(s.getSectionId());
                secNameList.add(s.getSectionName());
            }
            sectionSelections = new boolean[secIdList.size()];

            studIdList.clear();
            studNameList.clear();
            studentSelections = new boolean[studIdList.size()];
        }
    }

    public void showSectionDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Sections")
                .setCancelable(false)
                .setMultiChoiceItems(secNameList.toArray(new CharSequence[secIdList.size()]), sectionSelections, new SectionSelectionClickHandler())
                .setPositiveButton("OK", new SectionButtonClickHandler())
                .show();
    }

    public class SectionSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            boolean b = false;
            if (selected) b = true;
            if (studentSpinner.isShown()) {
                for (int i = 0; i < sectionSelections.length; i++) {
                    sectionSelections[i] = false;
                }
                dialog.dismiss();
                sectionSelections[clicked] = b;
                showSectionDialog();
            } else {
                if (selected) sectionSelections[clicked] = true;
                else sectionSelections[clicked] = false;
            }
        }
    }

    public class SectionButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    sectionSpinner.clearFocus();
                    selectedSection();
                    break;
            }
        }
    }

    protected void selectedSection() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb2 = new StringBuilder();
        if (!studentSpinner.isShown()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < secIdList.size(); i++) {
                if (sectionSelections[i]) {
                    isSelected = true;
                    sb.append(secIdList.get(i) + ",");
                    sb2.append(secNameList.get(i) + ", ");
                }
            }
            if (isSelected) {
                sectionSpinner.setText(sb2.substring(0, sb2.length() - 2));
                ids = sb.substring(0, sb.length() - 1);
                submitBtn.setEnabled(true);
            } else {
                sectionSpinner.setText("");
            }
        } else {
            for (int i = 0; i < secIdList.size(); i++) {
                if (sectionSelections[i]) {
                    isSelected = true;
                    sectionId = secIdList.get(i);
                    sb2.append(secNameList.get(i));
                    break;
                }
            }
            if (!isSelected) {
                sectionId = 0;
                sectionSpinner.setText("");
            } else {
                sectionSpinner.setText(sb2.toString());
            }
            studentSpinner.setText("");
            studIdList.clear();
            studNameList.clear();
            List<Students> studentList = StudentsDao.selectStudents(sectionId, sqliteDatabase);
            for (Students s : studentList) {
                studIdList.add(s.getStudentId());
                studNameList.add(s.getName());
            }
            studentSelections = new boolean[studIdList.size()];
        }
    }

    public void showStudentDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Students")
                .setCancelable(false)
                .setMultiChoiceItems(studNameList.toArray(new CharSequence[studIdList.size()]), studentSelections, new StudentSelectionClickHandler())
                .setPositiveButton("OK", new StudentButtonClickHandler())
                .show();
    }

    public class StudentSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            if (selected) studentSelections[clicked] = true;
            else studentSelections[clicked] = false;
        }
    }

    public class StudentButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    studentSpinner.clearFocus();
                    selectedStudent();
                    break;
            }
        }
    }

    protected void selectedStudent() {
        submitBtn.setEnabled(false);
        boolean isSelected = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < studIdList.size(); i++) {
            if (studentSelections[i]) {
                isSelected = true;
                sb.append(studIdList.get(i) + ",");
                sb2.append(studNameList.get(i) + ", ");
            }
        }
        if (isSelected) {
            studentSpinner.setText(sb2.substring(0, sb2.length() - 2));
            ids = sb.substring(0, sb.length() - 1);
            submitBtn.setEnabled(true);
        } else {
            studentSpinner.setText("");
        }
    }

    class CalledFTPSync extends AsyncTask<String, Integer, String> {
        private JSONObject jsonReceived;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(TextSms.this.getActivity());
            progressBar.setCancelable(false);
            progressBar.setMessage("Sending SMS...");
            //	progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //	progressBar.setProgress(0);
            //	progressBar.setMax(100);
            progressBar.show();
        }

		/*@Override
        protected void onProgressUpdate(Integer... progress) {
			progressBar.setProgress(progress[0]);
		}*/

        protected String doInBackground(String... arg0) {
            TransferManager mTransferManager = new TransferManager(Util.getCredProvider(appContext));
            uploadComplete = false;
            exception = false;
            prepareIds();
            createUploadFile();

            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Upload");

            File file = new File(dir, zipName);
            UploadModel model = new UploadModel(appContext, zipName, mTransferManager);
            model.upload();

            while (!uploadComplete) {
                Log.d("upload", "...");
            }

            if (!exception) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("school", schoolId);
                    jsonObject.put("tab_id", deviceId);
                    jsonObject.put("file_name", zipName.substring(0, zipName.length() - 3) + "sql");
                    jsonReceived = new JSONObject(RequestResponseHandler.reachServer(acknowledge_uploaded_file, jsonObject));
                    if (jsonReceived.getInt(TAG_SUCCESS) == 1) {
                        file.delete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               /* try {
                    sqliteDatabase.execSQL("insert into textsms(Message, Date, MessageTo, Ids) values('" + textSms.getText().toString().replaceAll("\n", "-") + "','" +
                            getToday() + "','" + messageTo + "','" + ids + "')");
                } catch (SQLException e) {
                }*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.dismiss();
            ReplaceFragment.clearBackStack(getFragmentManager());
            ReplaceFragment.replace(new Dashbord(), getFragmentManager());
        }
    }

    private void prepareIds() {
        Cursor c;
        switch(target) {
            case 0:
                c = sqliteDatabase.rawQuery("select Mobile1 from students", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 1:
                c = sqliteDatabase.rawQuery("select Mobile from teacher", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 2:
                c = sqliteDatabase.rawQuery("select Mobile1 from students where ClassId in (" + ids + ")", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 3:
                c = sqliteDatabase.rawQuery("select Mobile1 from students where SectionId in (" + ids + ")", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 4:
                c = sqliteDatabase.rawQuery("select Mobile1 from students where StudentId in (" + ids + ")", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 5:
                c = sqliteDatabase.rawQuery("select Mobile from teacher where TeacherId in (select ClassTeacherId from section)", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 6:
                c = sqliteDatabase.rawQuery("select Mobile1 from students where Gender = 'M'", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 7:
                c = sqliteDatabase.rawQuery("select Mobile1 from students where Gender = 'F'", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile1")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 8:
                c = sqliteDatabase.rawQuery("select Mobile from teacher where Gender = 'M'", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile")));
                    c.moveToNext();
                }
                c.close();
                break;
            case 9:
                c = sqliteDatabase.rawQuery("select Mobile from teacher where Gender = 'F'", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    idList.add(c.getLong(c.getColumnIndex("Mobile")));
                    c.moveToNext();
                }
                c.close();
                break;
            default:
                break;
        }
    }

    private void createUploadFile() {
        Temp t = TempDao.selectTemp(sqliteDatabase);
        deviceId = t.getDeviceId();
        schoolId = t.getSchoolId();
        long timeStamp = PKGenerator.getPrimaryKey();
        zipName = timeStamp + "_" + deviceId + "_" + schoolId + ".zip";
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Upload");
        dir.mkdirs();
        File file = new File(dir, timeStamp + "_" + deviceId + "_" + schoolId + ".sql");
        file.delete();
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            for (Long id : idList) {
                writer.write("insert into queue_transaction(SchoolId, Phone, Message, UserId, Role) values(" +
                        schoolId + ",'" + id + "','" + textSms.getText().toString().replaceAll("\n", "-").replace("'", "\\'").replace("\"", "\\\"") +
                        "'," + principalId + ", 'Principal');");
                writer.newLine();
            }
            writer.close();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, zipName));
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, bytesRead);
            }
            fileInputStream.close();
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileOutputStream.close();
            file.delete();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    public class UploadModel extends TransferModel {
        private String fileNam;
        private Upload mUpload;
        private ProgressListener mListener;
        private Status mStatus;

        public UploadModel(Context context, String key, TransferManager manager) {
            super(context, Uri.parse(key), manager);
            fileNam = key;
            mStatus = Status.IN_PROGRESS;
            mListener = new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent event) {
                    if (event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                        mStatus = Status.COMPLETED;
                        Log.d("upload", "complete");
                        uploadComplete = true;
                    } else if (event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
                        exception = true;
                        uploadComplete = true;
                    } else if (event.getEventCode() == ProgressEvent.CANCELED_EVENT_CODE) {
                        exception = true;
                        uploadComplete = true;
                    }
                }
            };
        }

        @Override
        public Status getStatus() {
            return mStatus;
        }

        @Override
        public Transfer getTransfer() {
            return mUpload;
        }

        public void upload() {
            try {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/Upload");
                File file = new File(dir, fileNam);
                mUpload = getTransferManager().upload(
                        Constants.BUCKET_NAME.toLowerCase(Locale.US), "upload/zipped_folder/" + fileNam,
                        file);
                mUpload.addProgressListener(mListener);
            } catch (Exception e) {
            }
        }

        @Override
        public void abort() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }
    }

}
