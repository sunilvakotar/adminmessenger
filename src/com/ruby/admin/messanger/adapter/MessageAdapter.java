package com.ruby.admin.messanger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ruby.admin.messanger.R;
import com.ruby.admin.messanger.bean.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunil Vakotar on 2/25/14.
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;

    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    public void addMessage(Message message) {
        if(messageList == null){
            messageList = new ArrayList<Message>();
        }
        messageList.add(message);
    }

    public int getCount() {
        return messageList.size();
    }

    public Object getItem(int position) {
        return messageList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View row;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.message_list_detail, null);
        }else{
            row = view;
        }
        Message message = messageList.get(position);
        TextView messageText = (TextView) row.findViewById(R.id.messageText);
        messageText.setText(message.getMessage());

        TextView date = (TextView) row.findViewById(R.id.dateText);
        date.setText(message.getDate());

        return row;
    }
}
