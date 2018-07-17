package com.example.accountingclerk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.string;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Interface_add_item extends Activity implements OnClickListener {

	final Database mdbHelper = new Database(this, "user.db3", null, 1);

	Button button_add, button_back;
	TextView textView;
	GridView gridView;
	List<Map<String, String>> list;
	SimpleAdapter simpleAdapter;
	String user_name;
	String payorincome="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interface_add_item);

		
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    
            setTranslucentStatus(true);    
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.blue500);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            
        }   
		
		
		Intent interface_add_out = getIntent();
		user_name = interface_add_out.getStringExtra("user_name");
		payorincome = interface_add_out.getStringExtra("payorincome");
		
		
		gridView = (GridView) findViewById(R.id.interface_add_item_gridView);
		button_add = (Button) findViewById(R.id.interface_add_item_button_add);
		button_back = (Button) findViewById(R.id.interface_add_item_button_back);
		button_add.setOnClickListener(this);
		button_back.setOnClickListener(this);
		textView=(TextView) findViewById(R.id.interface_add_item_textview);
		if (payorincome.equals("pay")) {
			textView.setText("支出项目\ntips:长按删除项目");
		}
		else {
			textView.setText("收入项目\ntips:长按删除项目");
		}
		
		resetdata();
		
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
					del(position);
				return false;
			}
		});
		
		
	}
	public void del(final int position){
		AlertDialog.Builder aBuilder=new AlertDialog.Builder(this);
		aBuilder.setTitle("确认删除吗?");
		aBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				list = getdata();
				String selectsString=list.get(position).get("item");
				String item_nameString=get_item_name();
				int i=item_nameString.indexOf(selectsString);
				String newitem_nameString=item_nameString.substring(0, i)+item_nameString.substring(i+selectsString.length()+1, item_nameString.length());
				if (payorincome.equals("pay")) {
					mdbHelper.getReadableDatabase().execSQL("UPDATE user SET item_name_pay=? WHERE user_name=?",new String[]{newitem_nameString,user_name});
				}
				else {
					mdbHelper.getReadableDatabase().execSQL("UPDATE user SET item_name_income=? WHERE user_name=?",new String[]{newitem_nameString,user_name});
				}
				
				Toast.makeText(getApplicationContext(), "删除成功!", Toast.LENGTH_LONG).show();
				resetdata();
			}
		});
		aBuilder.setNegativeButton("取消", null);
		aBuilder.create().show();
	}

	public void resetdata() {
		list = getdata();
		simpleAdapter = new SimpleAdapter(Interface_add_item.this, list,
				R.layout.interface_add_item_item, new String[] { "item" },
				new int[] { R.id.interface_add_item_item_textview });
		gridView.setAdapter(simpleAdapter);
	}

	public List<Map<String, String>> getdata() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String item_name_item = "";
		String item_name = "";
		item_name = get_item_name();
		for (int i = 0; i < item_name.length(); i++) {
			String nameString = item_name.substring(i, i + 1);
			if (nameString.equals(",")) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("item", item_name_item);
				list.add(map);
				item_name_item = "";
			} else {
				item_name_item += nameString;
			}
		}
		return list;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		resetdata();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.interface_add_item_button_add:
			add();
			break;
		case R.id.interface_add_item_button_back:
			finish();
			break;

		default:
			break;
		}
	}

	private void add() {
		final View view = View.inflate(this,
				R.layout.add_itemname, null);
		final EditText et = (EditText) view
				.findViewById(R.id.add_itemname);
		 String payorincomeString="";
		if (payorincome.equals("pay")) {
			payorincomeString ="item_name_pay";
		}
		else {
			payorincomeString ="item_name_income";
		}
		final String SQL_updata="UPDATE user SET "+payorincomeString+"=? WHERE user_name=?";
			AlertDialog AlertDialog_Monthly_budget = new AlertDialog.Builder(
					this)
					.setTitle("添加项目名")
					.setView(view)
					.setPositiveButton("添加",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									String itemnameString = et.getText()
											.toString();
									if (!itemnameString.isEmpty() && 
											get_item_name().indexOf(itemnameString)==-1 &&
											itemnameString.length()<=16
											
											) {
										
										mdbHelper
												.getReadableDatabase()
												.execSQL(
														SQL_updata,
														new String[] {
																get_item_name()
																		+ itemnameString
																		+ ",",
																user_name });

									}
									else {
										Toast.makeText(getApplicationContext(), "请勿键入相同值,限制字符16个.", Toast.LENGTH_SHORT).show();
									}
									resetdata();
								}
							}).setNegativeButton("取消", null)
					.setCancelable(false).show();
		

	}

	public String get_item_name() {
		String item_name = "";
		String SQL_select = "select * from user where user_name=?";
		Cursor cur = mdbHelper.getReadableDatabase().rawQuery(SQL_select,
				new String[] { user_name });
		if (cur.moveToFirst()) {
			if (payorincome.equals("pay")) {
				item_name = cur.getString(cur.getColumnIndex("item_name_pay"));
			}
			else {
				item_name = cur.getString(cur.getColumnIndex("item_name_income"));
			}
			
		}
		cur.close();
		return item_name;

	}
	
	@TargetApi(19)     
    private void setTranslucentStatus(boolean on) {    
        Window win = getWindow();    
        WindowManager.LayoutParams winParams = win.getAttributes();    
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;    
        if (on) {    
            winParams.flags |= bits;    
        } else {    
            winParams.flags &= ~bits;    
        }    
        win.setAttributes(winParams);    
    }  

}
