package com.ruby.admin.messanger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ruby.admin.messanger.constant.Constant;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String MESSAGE_TABLE_CREATION = "create table "+ Constant.TABLE_MESSAGE+
            " ("+ Constant.ID+ " integer primary key AUTOINCREMENT,"+
            Constant.MESSAGE + " text not null, " +
            Constant.USER_NAME + " text not null, " +
            Constant.DATE + " text not null);";
	

	private Context myContext;
	public MyDatabaseHelper(Context context) {
		super(context, Constant.DB_NAME, null, Constant.DB_VERSION);
		myContext = context;
	}
	
	public boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = Constant.DB_PATH + Constant.DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
    		Log.w(MyDatabaseHelper.class.getName(), "database does't exist yet." ); 
    	}
    	if(checkDB != null){	 
    		checkDB.close();
    	}
    	return checkDB != null ? true : false;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.w(MyDatabaseHelper.class.getName(), MESSAGE_TABLE_CREATION);
		db.execSQL(MESSAGE_TABLE_CREATION);
        //db.execSQL("insert into "+Constant.TABLE_MESSAGE+" (message, date) values ('Hello This is first message form Admin.','25-FEB-2014 07:22 AM')");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MyDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS" + Constant.TABLE_MESSAGE);
		onCreate(db);
	}

}
