package com.example.accountingclerk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class Interface_add extends Activity implements OnClickListener {
	final Database mdbHelper = new Database(this, "user.db3", null, 1);
	Spinner spinner;
	List<String> list = new ArrayList<String>();
	ArrayAdapter<String> arrayAdapter;
	String user_name;
	Button button_item_add, button_add, button_back;
	EditText edittext_money, edittext_note;

	RadioGroup payorincomeGroup;
	RadioButton payButton,incomeButton;
	// 获取本月时间
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
	String string_now_date = formatter.format(curDate);

	@SuppressLint("ResourceAsColor") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interface_add);

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

		spinner = (Spinner) findViewById(R.id.interface_add_spinner);

		Intent main_interface_out = getIntent();
		user_name = main_interface_out.getStringExtra("user_name");

		payorincomeGroup = (RadioGroup) findViewById(R.id.payorincome);
		payButton=(RadioButton) findViewById(R.id.add_pay);
		incomeButton=(RadioButton) findViewById(R.id.add_income);
		
		edittext_money = (EditText) findViewById(R.id.interface_add_money);
		edittext_note = (EditText) findViewById(R.id.interface_add_note);
		button_item_add = (Button) findViewById(R.id.interface_add_button_item_add);
		button_add = (Button) findViewById(R.id.interface_add_button_add);
		button_back = (Button) findViewById(R.id.interface_add_button_back);
		button_add.setOnClickListener(this);
		button_back.setOnClickListener(this);
		button_item_add.setOnClickListener(this);

		payorincomeGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Resources resource = (Resources) getBaseContext().getResources();  
						ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.blue500);  
						switch (checkedId) {
						case R.id.add_pay:
							payButton.setTextColor(csl);
							incomeButton.setTextColor(Color.rgb(0, 0, 0));
							resetdata("pay");
							break;
						case R.id.add_income:
							payButton.setTextColor(Color.rgb(0, 0, 0));
							incomeButton.setTextColor(csl);
							resetdata("income");
							break;

						default:
							break;
						}
					}
				});
		resetdata("pay");

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (payorincomeGroup.getCheckedRadioButtonId()==R.id.add_pay) {
			resetdata("pay");
		}
		else {
			resetdata("income");
		}
		
		
	}

	public List<String> get_item_name(String payorincome) {
		List<String> list = new ArrayList<String>();
		String select_pai=null;
		if (payorincome.equals("pay")) {
			select_pai="item_name_pay";
		}
		else {
			select_pai="item_name_income";
		}
		
		String item_name = "";
		String item_name_item = "";
		String SQL_select_item_name = "select * from user where user_name=?";
		Cursor cur = mdbHelper.getReadableDatabase().rawQuery(
				SQL_select_item_name, new String[] { user_name });
		if (cur.moveToFirst()) {
			item_name = cur.getString(cur.getColumnIndex(select_pai));
		}
		for (int i = 0; i < item_name.length(); i++) {
			String nameString = item_name.substring(i, i + 1);
			if (nameString.equals(",")) {
				list.add(item_name_item);
				item_name_item = "";
			} else {
				item_name_item += nameString;
			}
		}
		cur.close();
		return list;
	}

	public void resetdata(String payorincome) {
		list = get_item_name(payorincome);
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arrayAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.interface_add_button_add:
			add();
			break;
		case R.id.interface_add_button_back:
			finish();
			break;
		case R.id.interface_add_button_item_add:
			Intent go_interface_add_item = new Intent();
			go_interface_add_item.putExtra("user_name", user_name);
			if (payorincomeGroup.getCheckedRadioButtonId()==R.id.add_pay){
				go_interface_add_item.putExtra("payorincome", "pay");
			}
			else {
				go_interface_add_item.putExtra("payorincome", "income");
			}
			
			go_interface_add_item.setClass(Interface_add.this,
					Interface_add_item.class);
			startActivity(go_interface_add_item);
			break;

		default:
			break;
		}
	}

	public void add() {
		String itemString = spinner.getSelectedItem().toString();
		String moneyString = edittext_money.getText().toString();
		String noteString = edittext_note.getText().toString();
		String payorincomeString=null;
		if (payorincomeGroup.getCheckedRadioButtonId()==R.id.add_pay) {
			payorincomeString="pay";
		}
		else {
			payorincomeString="income";
		}
		if (!moneyString.isEmpty()) {
			String SQL_insert = "insert into data values(?,?,?,?,?,?,?)";
			mdbHelper.getReadableDatabase().execSQL(
					SQL_insert,
					new String[] { null, user_name, string_now_date,
							moneyString, itemString, noteString ,payorincomeString});
			Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG)
					.show();
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "金额不能为空", Toast.LENGTH_LONG)
					.show();
		}
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
