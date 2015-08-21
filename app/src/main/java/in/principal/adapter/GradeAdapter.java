package in.principal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;

public class GradeAdapter extends ArrayAdapter<AdapterOverloaded>{
    private int resource;
    private ArrayList<AdapterOverloaded> data = new ArrayList<>();
    private LayoutInflater inflater = null;

    public GradeAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
        super(context, resource, listArray);
        this.resource = resource;
        this.data = listArray;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null){
            row = inflater.inflate(resource, parent, false);
            holder = new RecordHolder();
            holder.text1 = (TextView) row.findViewById(R.id.txt1);
            holder.text2 = (TextView) row.findViewById(R.id.txt2);
            holder.text3 = (TextView) row.findViewById(R.id.txt3);
            row.setTag(holder);
        }else{
            holder = (RecordHolder) row.getTag();
        }

        if(position % 2 == 0)
            row.setBackgroundResource(R.drawable.list_selector1);
        else
            row.setBackgroundResource(R.drawable.list_selector2);

        AdapterOverloaded listItem = data.get(position);
        holder.text1.setText(listItem.getText1());
        holder.text2.setText(listItem.getText2());
        holder.text3.setText(listItem.getText3());

        return row;
    }

    public static class RecordHolder{
        public TextView text1;
        public TextView text2;
        public TextView text3;
    }

}
