package com.ruby.admin.messanger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.ruby.admin.messanger.bean.Message;
import com.ruby.admin.messanger.constant.Constant;

import java.util.ArrayList;
import java.util.List;

public class MessageDataSource {
	private SQLiteDatabase database;
	private MyDatabaseHelper databaseHelper;
	private String[] messageColumns = { Constant.ID, Constant.MESSAGE, Constant.TITLE, Constant.DATE, Constant.USER_NAME};
    private String[] titleColumn = { Constant.TITLE};

	public MessageDataSource(Context context){
		databaseHelper = new MyDatabaseHelper(context);
	}
	
	public void open()throws SQLException {
        database = databaseHelper.getReadableDatabase();
	}
	
	public void close() {
		databaseHelper.close();
	}

    public void saveMessage(Message message){
        ContentValues messageValues = new ContentValues();
        messageValues.put(Constant.MESSAGE, message.getMessage());
        messageValues.put(Constant.TITLE, message.getTitle());
        messageValues.put(Constant.DATE, message.getDate());
        messageValues.put(Constant.USER_NAME, message.getUsername());
        database.insert(Constant.TABLE_MESSAGE, null, messageValues);
    }

    public List<String> getAllTitle(){
        List<String> titles = new ArrayList<String>();
        Cursor cursor = database.query(true, Constant.TABLE_MESSAGE, titleColumn, null, null, Constant.TITLE, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            titles.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return titles;
    }
	
	public List<Message> getAllMessages(){
        List<Message> messages = new ArrayList<Message>();
        Cursor cursor = database.query(Constant.TABLE_MESSAGE, messageColumns, null, null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        return messages;
    }

    public List<Message> getAllMessagesByUser(String username){
        List<Message> messages = new ArrayList<Message>();
        Cursor cursor = database.query(Constant.TABLE_MESSAGE, messageColumns, Constant.USER_NAME + " = '" + username+"'", null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        return messages;
    }

    private Message cursorToMessage(Cursor cursor) {
        Message message = new Message();
        message.setId(cursor.getInt(0));
        message.setMessage(cursor.getString(1));
        message.setTitle(cursor.getString(2));
        message.setDate(cursor.getString(3));
        message.setUsername(cursor.getString(4));
        return message;
    }
	
	public void truncateTable(){
        database.execSQL("delete from "+Constant.TABLE_MESSAGE);
    }
}
