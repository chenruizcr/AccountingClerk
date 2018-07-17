package com.example.accountingclerk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{

	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_users = "create table user(_id integer primary key autoincrement,user_name varchar,user_pass varchar,Monthly_budget varchar,monthly_budget_isfirst varchar,item_name_pay varchar,item_name_income varcher)"; 
		String add_data="create table data(id integer primary key autoincrement,user_name varchar,date varchar,money varchar,item_name varchar,note varchar,payorincome varchar)";
		String setting="create table setting(setting_name varchar,is_remember_userandpw varchar,remember_userandpw varchar)";
		db.execSQL(CREATE_users);
		db.execSQL(add_data);
		db.execSQL(setting);
		


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	

}
