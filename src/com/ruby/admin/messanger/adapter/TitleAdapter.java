package com.ruby.admin.messanger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ruby.admin.messanger.R;
import com.ruby.admin.messanger.bean.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunil Vakotar on 3/18/14.
 */
public class TitleAdapter extends BaseAdapter {

    private Context context;

    private List<String> titleList;

    public TitleAdapter(Context context, List<String> titleList) {
        this.context = context;
        this.titleList = titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }

    public int getCount() {
        return titleList.size();
    }

    public Object getItem(int position) {
        return titleList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View row;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.title_list_detail, null);
        }else{
            row = view;
        }
        String title = titleList.get(position);
        TextView messageText = (TextView) row.findViewById(R.id.messageText);
        messageText.setText(title);

        return row;
    }

}
