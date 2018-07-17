package com.example.accountingclerk;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity {

	String username, payorincome;
	Double sum;
	TextView textView_top;
	ListView listView;
	Button button_back;
	final Database mdbHelper = new Database(this, "user.db3", null, 1);
	// 数据源和适配器
	List<Map<String, String>> data;
	SimpleAdapter listAdapter;
	// 获取本月时间
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
	String string_now_date = formatter.format(curDate);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.blue500);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

		}
		setContentView(R.layout.report);
		
		button_back = (Button) findViewById(R.id.report_button_back);
		textView_top = (TextView) findViewById(R.id.report_textview_top);
		listView = (ListView) findViewById(R.id.report_listview_main);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		payorincome = intent.getStringExtra("payorincome");
		sum = intent.getDoubleExtra("sum", 0);
		
		button_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		reset_data();
		if (payorincome.equals("pay")) {
			textView_top.setText("本月支出:"+sum);
		}
		else {
			textView_top.setText("本月收入:"+sum);
		}
		
	}

	public void reset_data() {
		// 根据用户名获取数据
		data = getdata();
		// 配置适配器
		listAdapter = new SimpleAdapter(getApplicationContext(), data,
				R.layout.report_item, new String[] { "item_name", "percentage",
						"money" }, new int[] {
						R.id.report_item_textview_itemname,
						R.id.report_item_textview_percentage,
						R.id.report_item_textview_money });
		// 加载适配器
		listView.setAdapter(listAdapter);

	}

	private List<Map<String, String>> getdata() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Cursor cursor = mdbHelper.getReadableDatabase().rawQuery(
				"select * from data where user_name = ? and payorincome = ?",
				new String[] { username, payorincome });
		String itemString = get_item_name();
		Map<String, Double> itemnameMap = new HashMap<String, Double>();
		while (itemString.length() > 0) {
			String new_itemname = itemString.substring(0,
					itemString.indexOf(","));
			itemString = itemString.substring(new_itemname.length() + 1,
					itemString.length());
			itemnameMap.put(new_itemname, 0.0);
		}
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getCount(); i++) {

				String item_nameString = cursor.getString(cursor
						.getColumnIndex("item_name"));
				String moneyString = cursor.getString(cursor
						.getColumnIndex("money"));
				String dateString = cursor.getString(cursor
						.getColumnIndex("date"));
				if (string_now_date.equals(dateString.subSequence(0, 7))) {
				Double money_sum = itemnameMap.get(item_nameString)
						+ Double.parseDouble(moneyString);
				itemnameMap.put(item_nameString, money_sum);
				money_sum = 0.0;
				}
				
				
				cursor.moveToNext();
			}

		}
		cursor.close();
		//排序
		 List<Map.Entry<String, Double>> after_itemnamemap = new ArrayList<Map.Entry<String, Double>>(itemnameMap.entrySet());    
	        Collections.sort(after_itemnamemap, new Comparator<Map.Entry<String, Double>>()    
	          {     
	              public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)    
	              {    
	               if(o2.getValue()!=null&&o1.getValue()!=null&&o2.getValue().compareTo(o1.getValue())>0){    
	                return 1;    
	               }else{    
	                return -1;    
	               }    
	                    
	              }    
	          });    
	  
	 
		//遍历排序
	       for (Map.Entry<String, Double> entry : after_itemnamemap) {
	    	   Map<String, String> map = new HashMap<String, String>();
	    	   map.put("item_name", entry.getKey());
	    	   if (sum == 0) {
					map.put("percentage", "0%");
				} else {
					if (entry.getValue()!=0) {
						 DecimalFormat df = new DecimalFormat("#.0");  
							Double  percentagedoublDouble=entry.getValue()/sum*100;
							map.put("percentage", df.format(percentagedoublDouble) + "%");
					}
					else {
						map.put("percentage", "0%");
					}
					
				}
	    	   map.put("money", entry.getValue() + "");
	    	   data.add(map);
	       }
		
//		for (String k : itemnameMap.keySet()) {
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("item_name", k);
//			if (sum == 0) {
//				map.put("percentage", "0%");
//			} else {
//				map.put("percentage", itemnameMap.get(k) / sum*100 + "%");
//			}
//			map.put("money", itemnameMap.get(k) + "");
//			
//		}

		return data;
	}

	public String get_item_name() {
		String item_name = "";
		String SQL_select = "select * from user where user_name=?";
		Cursor cur = mdbHelper.getReadableDatabase().rawQuery(SQL_select,
				new String[] { username });
		if (cur.moveToFirst()) {
			if (payorincome.equals("pay")) {
				item_name = cur.getString(cur.getColumnIndex("item_name_pay"));
			} else {
				item_name = cur.getString(cur
						.getColumnIndex("item_name_income"));
			}

		}
		cur.close();
		return item_name;

	}

	@Override
	protected void onRestart() {
		// TODO 自动生成的方法存根
		super.onRestart();
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
