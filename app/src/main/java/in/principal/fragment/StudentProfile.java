package in.principal.fragment;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import in.principal.activity.R;
import in.principal.searchfragment.SearchStudAtt;
import in.principal.searchfragment.SearchStudExam;
import in.principal.searchfragment.SearchStudST;
import in.principal.dao.TempDao;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

/**
 * Created by vinkrish on 18/12/15.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class StudentProfile extends Fragment{
    private TextView studTV, clasSecTV;
    private EditText rollNo, admissionNo, dob, fatherName, motherName, gender, mobile1, mobile2, address, pincode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_profile, container, false);

        initView(view);

        init();

        return view;
    }

    private void initView(View view) {
        studTV = (TextView) view.findViewById(R.id.studName);
        clasSecTV = (TextView) view.findViewById(R.id.studClasSec);

        dob = (EditText) view.findViewById(R.id.dob);
        rollNo = (EditText) view.findViewById(R.id.roll_no);
        admissionNo = (EditText) view.findViewById(R.id.admission_no);
        fatherName = (EditText) view.findViewById(R.id.father_name);
        motherName = (EditText) view.findViewById(R.id.mother_name);
        gender = (EditText) view.findViewById(R.id.gender);
        mobile1 = (EditText) view.findViewById(R.id.mobile1);
        mobile2 = (EditText) view.findViewById(R.id.mobile2);
        address = (EditText) view.findViewById(R.id.address);
        pincode = (EditText) view.findViewById(R.id.pincode);

        view.findViewById(R.id.slipSearch).setOnClickListener(searchSlipTest);
        view.findViewById(R.id.seSearch).setOnClickListener(searchExam);
        view.findViewById(R.id.attSearch).setOnClickListener(searchAttendance);
    }

    private void init(){
        SQLiteDatabase sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        long studentId = t.getStudentId();

        String studName = "";
        String clasName = "";
        String secName = "";
        Cursor c = sqliteDatabase.rawQuery("select A.Name, A.ClassId, A.SectionId, B.ClassName, C.SectionName from students A, class B, section C where" +
                " A.StudentId=" + studentId + " and A.ClassId=B.ClassId and A.SectionId=C.SectionId group by A.StudentId", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            studName = c.getString(c.getColumnIndex("Name"));
            clasName = c.getString(c.getColumnIndex("ClassName"));
            secName = c.getString(c.getColumnIndex("SectionName"));
            c.moveToNext();
        }
        c.close();
        studTV.setText(studName);
        clasSecTV.setText(clasName + " - " + secName);

        Cursor c2 = sqliteDatabase.rawQuery("select * from students where StudentId = " + studentId, null);
        c2.moveToFirst();
        while (!c2.isAfterLast()) {
            rollNo.setText(c2.getString(c2.getColumnIndex("RollNoInClass")));
            admissionNo.setText(c2.getString(c2.getColumnIndex("AdmissionNo")));
            fatherName.setText(c2.getString(c2.getColumnIndex("FatherName")));
            motherName.setText(c2.getString(c2.getColumnIndex("MotherName")));
            dob.setText(c2.getString(c2.getColumnIndex("DateOfBirth")));
            gender.setText(c2.getString(c2.getColumnIndex("Gender")));
            mobile1.setText(c2.getString(c2.getColumnIndex("Mobile1")));
            mobile2.setText(c2.getString(c2.getColumnIndex("Mobile2")));
            address.setText(c2.getString(c2.getColumnIndex("Address")));
            pincode.setText(c2.getString(c2.getColumnIndex("Pincode")));
            c2.moveToNext();
        }
        c2.close();

        rollNo.setKeyListener(null);
        admissionNo.setKeyListener(null);
        fatherName.setKeyListener(null);
        motherName.setKeyListener(null);
        dob.setKeyListener(null);
        gender.setKeyListener(null);
        mobile1.setKeyListener(null);
        mobile2.setKeyListener(null);
        address.setKeyListener(null);
        pincode.setKeyListener(null);

    }

    private View.OnClickListener searchSlipTest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudST(), getFragmentManager());
        }
    };

    private View.OnClickListener searchExam = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudExam(), getFragmentManager());
        }
    };

    private View.OnClickListener searchAttendance = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReplaceFragment.replace(new SearchStudAtt(), getFragmentManager());
        }
    };


}
