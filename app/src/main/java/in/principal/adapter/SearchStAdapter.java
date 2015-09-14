package in.principal.adapter;

import in.principal.activity.R;
import in.principal.sqlite.AdapterOverloaded;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by vinkrish.
 */

public class SearchStAdapter extends ArrayAdapter<AdapterOverloaded> {
    private int resource;
    private ArrayList<AdapterOverloaded> data = new ArrayList<AdapterOverloaded>();
    private LayoutInflater inflater = null;

    public SearchStAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
        super(context, resource, listArray);
        this.resource = resource;
        this.data = listArray;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            row = inflater.inflate(resource, parent, false);
            holder = new RecordHolder();
            holder.int1 = (TextView) row.findViewById(R.id.li_txt1);
            holder.txt1 = (TextView) row.findViewById(R.id.li_txt2);
            holder.txt2 = (TextView) row.findViewById(R.id.li_txt3);
            holder.int2 = (TextView) row.findViewById(R.id.li_txt4);
            holder.int3 = (TextView) row.findViewById(R.id.li_txt5);
            row.setTag(holder);
        } else holder = (RecordHolder) row.getTag();

        if (position % 2 == 0)
            row.setBackgroundResource(R.drawable.list_selector1);
        else
            row.setBackgroundResource(R.drawable.list_selector2);

        AdapterOverloaded listItem = data.get(position);
        holder.int1.setText(listItem.getInt1() + "");
        holder.int2.setText(listItem.getInt2() + "");
        holder.int3.setText(listItem.getInt3() + "");
        holder.txt1.setText(listItem.getText1());
        holder.txt2.setText(listItem.getText2());

        return row;
    }

    public static class RecordHolder {
        public TextView int1;
        public TextView int2;
        public TextView int3;
        public TextView txt1;
        public TextView txt2;
    }

}
