package com.example.accountingclerk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main_interface extends Activity implements
		android.view.View.OnClickListener {
	// 控件变量声明
	Button button_add, button_back, button_monthly_budget;
	ListView mainList;
	// 数据源和适配器
	List<Map<String, String>> data;
	SimpleAdapter listAdapter;
	// 用户名,总支出,总收入
	TextView textView_user_name, textview_Total_spending,textView_show_income;

	String user_name;

	double double_Monthly_budget, double_Total_spending,double_income;

	final Database mdbHelper = new Database(this, "user.db3", null, 1);
	// 获取本月时间
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
	String string_now_date = formatter.format(curDate);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		setContentView(R.layout.main_interface);
		// 控件变量赋值
		button_add = (Button) findViewById(R.id.interface_button_add);
		button_back = (Button) findViewById(R.id.interface_button_back);
		textView_user_name = (TextView) findViewById(R.id.interface_textview_user_name);
		textview_Total_spending = (TextView) findViewById(R.id.interface_textview_Total_spending);
		textView_show_income=(TextView) findViewById(R.id.interface_textview_showincome);
		mainList = (ListView) findViewById(R.id.interface_listview_main);
		button_monthly_budget = (Button) findViewById(R.id.interface_button_Monthly_budget);
		// 监听事件绑定
		button_add.setOnClickListener(this);
		button_back.setOnClickListener(this);
		button_monthly_budget.setOnClickListener(this);
		// 接收传递的用户名,并加载在title上
		Intent mainactivity_outstring = getIntent();
		user_name = mainactivity_outstring.getStringExtra("user_name");
		textView_user_name.setText("welcome," + user_name);
		// 用户第一次登录 启用月预算功能设置
		String SQL_select_isfirst = "select * from user where user_name=? and monthly_budget_isfirst=?";
		Cursor cur = mdbHelper.getReadableDatabase().rawQuery(
				SQL_select_isfirst, new String[] { user_name, "true" });
		if (cur.getCount() <= 0) {
			// 没有匹配的结果
		} else {
			// 有匹配的结果
			final View view = View.inflate(this,
					R.layout.monthly_budget_edittext, null);
			final EditText et = (EditText) view
					.findViewById(R.id.monthly_budget_edittext);
			final String SQL_updata_budgetisfirst = "UPDATE user SET monthly_budget_isfirst=? WHERE user_name=?";
			final String SQL_updata_budgeti = "UPDATE user SET Monthly_budget=? WHERE user_name=?";
			AlertDialog AlertDialog_Monthly_budget = new AlertDialog.Builder(
					this)
					.setTitle("首次登录,请输入月预算!")
					.setMessage("点击主界面的月预算也可以修改.")
					.setView(view)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									String string_Monthly_budget = et.getText()
											.toString();
									if (!string_Monthly_budget.isEmpty()) {

										mdbHelper
												.getReadableDatabase()
												.execSQL(
														SQL_updata_budgeti,
														new String[] {
																string_Monthly_budget,
																user_name });
									}
									mdbHelper.getReadableDatabase()
											.execSQL(
													SQL_updata_budgetisfirst,
													new String[] { "flase",
															user_name });

									reset_data();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mdbHelper.getReadableDatabase()
											.execSQL(
													SQL_updata_budgetisfirst,
													new String[] { "flase",
															user_name });
									mdbHelper.getReadableDatabase().execSQL(
											SQL_updata_budgeti,
											new String[] { "2000", user_name });
									reset_data();
								}
							}).setCancelable(false).show();
		}

		textview_Total_spending.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent =new Intent();
				intent.setClass(getApplicationContext(), Report.class);
				intent.putExtra("username", user_name);
				intent.putExtra("payorincome", "pay");
				intent.putExtra("sum", double_Total_spending);
				startActivity(intent);
				return false;
			}
		});
		textView_show_income.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent =new Intent();
				intent.setClass(getApplicationContext(), Report.class);
				intent.putExtra("username", user_name);
				intent.putExtra("payorincome", "income");
				intent.putExtra("sum", double_income);
				startActivity(intent);
				return false;
			}
		});

		mainList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showdata(position);
			}
		});

		reset_data();
	}

	public void showdata(final int position) {
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
		aBuilder.setTitle(data.get(position).get("item_name"));
		String payorincomeString;
		if (data.get(position).get("payorincome").equals("pay")) {
			 payorincomeString="支付";
		}
		else {
			 payorincomeString="收入";
		}
		
		aBuilder.setMessage("时间:" + data.get(position).get("hidedate")+ "\n" +"类型:"+payorincomeString + "\n"
				+ "金额:" + data.get(position).get("money") + "\n" + "备注:"
				+ data.get(position).get("note"));
		aBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				del(position);
			}
		});
		aBuilder.setNegativeButton("取消", null);
		aBuilder.create().show();
	}

	public void del(final int position) {
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
		aBuilder.setTitle("确定删除吗?");
		aBuilder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String dateString = data.get(position).get("hidedate");
				String itemString = data.get(position).get("item_name");
				String moneyString = data.get(position).get("money");
				String noteString = data.get(position).get("note");
				String idString = data.get(position).get("id");
				String payorincomeString=data.get(position).get("payorincome");
				String SQL_del = "DELETE FROM data WHERE user_name=? AND date=? AND money=? AND item_name=? AND note=? AND id=? AND payorincome=?";
				mdbHelper.getReadableDatabase().execSQL(
						SQL_del,
						new String[] { user_name, dateString, moneyString,
								itemString, noteString, idString ,payorincomeString});
				reset_data();
			}
		});
		aBuilder.setNegativeButton("取消", null).create().show();

	}

	public void reset_data() {
		// 根据用户名获取数据
		data = getdata(user_name);
		// 配置适配器
		listAdapter = new SimpleAdapter(this, data, R.layout.interface_item,
				new String[] { "date", "item_name", "money", "note" },
				new int[] { R.id.interface_item_date, R.id.interface_item_name,
						R.id.interface_item_money, R.id.interface_item_note });
		// 加载适配器
		mainList.setAdapter(listAdapter);
	}

	private List<Map<String, String>> getdata(String userString) {
		// 获取月预算的值
		double_Monthly_budget = Double.parseDouble(get_Monthly_budget());
		//
		double_Total_spending=0;
		double_income=0;
		//
		boolean begin_first_date = true;
		String date = null;
		boolean first_date = true;
		// 新建一个用于返回的数据
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		// 创建游标,根据用户名查询 单词
		// 游标内存储的是 查询好的数据库的记录,可以多条记录
		Cursor cursor = mdbHelper.getReadableDatabase().rawQuery(
				"select * from data where user_name=?",
				new String[] { userString });
		// 把游标设置 第一个查询结果
		if (cursor.moveToLast()) {
			// 循环读取
			for (int i = 0; i < cursor.getCount(); i++) {
				// 根据字段名 读取数据
				String dateString = cursor.getString(cursor
						.getColumnIndex("date"));
				String item_nameString = cursor.getString(cursor
						.getColumnIndex("item_name"));
				String moneyString = cursor.getString(cursor
						.getColumnIndex("money"));
				String noteString = cursor.getString(cursor
						.getColumnIndex("note"));
				
				String payorincomeString = cursor.getString(cursor
						.getColumnIndex("payorincome"));
				String idString = cursor.getString(cursor.getColumnIndex("id"));
				if (isnowdate(dateString)) {
					// 传出 每条记录 的支出 进行 月剩余预算和总支出 的计算
					if (payorincomeString.equals("pay")) {
						set_budgetspending(moneyString);
					}
					else {
						double_income+=Double.parseDouble(moneyString);
					}
				}

				// 添加到map中
				Map<String, String> map = new HashMap<String, String>();

				map.put("item_name", item_nameString);
				map.put("money", moneyString);
				map.put("note", noteString);
				map.put("id", idString);
				map.put("payorincome", payorincomeString);
				if (begin_first_date) {
					date = dateString;
					map.put("date", dateString);
					map.put("hidedate", dateString);
					begin_first_date = false;
				} else {
					if (date.equals(dateString)) {

						map.put("date", null);
						map.put("hidedate", dateString);
					} else {
						date = dateString;
						map.put("date", dateString);
						map.put("hidedate", dateString);

					}
				}

				// 添加到数据源中
				data.add(map);
				// 将游标定位在下一个
				cursor.moveToPrevious();
			}
		}
		// 关闭游标
		cursor.close();

		if (double_Monthly_budget<=0) {
			button_monthly_budget.setBackgroundResource(R.color.orange200_button_dw);
			button_monthly_budget.setText("本月超出:\n" + (double_Monthly_budget)*-1);
			textview_Total_spending.setText("本月消费\n:" + double_Total_spending);
		}
		else {
			button_monthly_budget.setBackgroundResource(R.color.blue200_button_dw);
			button_monthly_budget.setText("本月剩余:\n" + double_Monthly_budget);
			textview_Total_spending.setText("本月消费:\n" + double_Total_spending);
		}
		textView_show_income.setText("本月收入:\n"+double_income);
		return data;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		reset_data();
	}

	public void set_budgetspending(String money) {
		double every_money = Double.parseDouble(money);
		double_Total_spending += every_money;
		double_Monthly_budget -= every_money;
	}

	public String get_Monthly_budget() {
		String Monthly_budget = null;
		Cursor cursor = mdbHelper.getReadableDatabase().rawQuery(
				"select * from user where user_name=?",
				new String[] { user_name });
		// 把游标设置 第一个查询结果
		if (cursor.moveToFirst()) {
			Monthly_budget = cursor.getString(cursor
					.getColumnIndex("Monthly_budget"));
		}

		return Monthly_budget;
	}

	public boolean isnowdate(String lastdate) {

		if (string_now_date.equals(lastdate.substring(0, 7))) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.interface_button_add:
			Intent go_add = new Intent();
			go_add.setClass(Main_interface.this, Interface_add.class);
			go_add.putExtra("user_name", user_name);
			startActivity(go_add);
			break;
		case R.id.interface_button_back:
			finish();
			break;
		case R.id.interface_button_Monthly_budget:
			resetmonthlybudget();
			break;

		default:
			break;
		}
	}

	private void resetmonthlybudget() {
		AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
		final View view = View.inflate(this, R.layout.monthly_budget_edittext,
				null);
		final EditText et = (EditText) view
				.findViewById(R.id.monthly_budget_edittext);
		SpannableString s = new SpannableString("当前值:" + get_Monthly_budget());
		et.setHint(s);
		final String SQL_updata_budgeti = "UPDATE user SET Monthly_budget=? WHERE user_name=?";
		aBuilder.setTitle("请设置月预算");
		aBuilder.setView(view);
		aBuilder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String string = et.getText().toString();
				if (!string.isEmpty()) {
					mdbHelper.getReadableDatabase().execSQL(SQL_updata_budgeti,
							new String[] { string, user_name });
					reset_data();
				} else {

				}

			}
		});
		aBuilder.setNegativeButton("取消", null);
		aBuilder.create().show();
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
