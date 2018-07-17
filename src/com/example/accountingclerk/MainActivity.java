package com.example.accountingclerk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements
		android.view.View.OnClickListener {
	// 控件变量声明
	Button button_login, button_registered;
	EditText edittext_user, edittext_password;
	CheckBox remuserpw;
	TextView updatalogTextView;
	// 初始化数据库
	final Database mdbHelper = new Database(this, "user.db3", null, 1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    
            setTranslucentStatus(true);    
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.blue500);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            
        }   
		

		// 控件变量赋值
		button_login = (Button) findViewById(R.id.mainactivity_button_login);
		button_registered = (Button) findViewById(R.id.mainactivity_button_registered);
		edittext_user = (EditText) findViewById(R.id.mainactivity_edittext_user);
		edittext_password = (EditText) findViewById(R.id.mainactivity_edittext_password);
		remuserpw=(CheckBox) findViewById(R.id.mainactivity_check_remuserpw);
		updatalogTextView=(TextView) findViewById(R.id.updatalogtextview);
		updatalogTextView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, Updatalog.class);
				startActivity(intent);
				return false;
			}
		});
		// 单击事件绑定
		button_login.setOnClickListener(this);
		button_registered.setOnClickListener(this);
		setuserpw();
		
	}
	public void setuserpw(){
		Cursor cursor=mdbHelper.getReadableDatabase().rawQuery("select * from setting where setting_name=?", new String[]{"rememberuser"});
		if (cursor.getCount()<=0) {
			mdbHelper.getReadableDatabase().execSQL("insert into setting values(?,?,?)",new String[]{"rememberuser","false",null});
		}
		else {
			if (cursor.moveToFirst()) {
				String is_remember_userandpw = cursor.getString(cursor
						.getColumnIndex("is_remember_userandpw"));
				String remember_userandpw = cursor.getString(cursor
						.getColumnIndex("remember_userandpw"));
				if (is_remember_userandpw.equals("true")) {
					remuserpw.setChecked(true);
					int i=remember_userandpw.indexOf(",");
					edittext_user.setText(remember_userandpw.substring(0, i));
					edittext_password.setText(remember_userandpw.substring(i+1, remember_userandpw.length()));
				}
			}
		}
		cursor.close();

	}
	// 按钮点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mainactivity_button_login:
			set_remuserpw();
			login();
			break;
		case R.id.mainactivity_button_registered:
			Intent intent_go_registered=new Intent();
			intent_go_registered.setClass(MainActivity.this, Registered.class);
			startActivity(intent_go_registered);
			break;
		default:
			break;
		}
	}
	public void set_remuserpw(){
		String string_username = edittext_user.getText().toString();
		String string_password = edittext_password.getText().toString();
		String SQL_updata="UPDATE setting SET is_remember_userandpw=? , remember_userandpw=? WHERE setting_name=?";
		if (remuserpw.isChecked()) {
			mdbHelper.getReadableDatabase().execSQL(SQL_updata,new String[]{"true",string_username+","+string_password,"rememberuser"});
		}
		else {
			mdbHelper.getReadableDatabase().execSQL(SQL_updata,new String[]{"false",null,"rememberuser"});
		}
		
	}
	// 登录按钮点击方法
	public void login() {
		String string_username = edittext_user.getText().toString();
		String string_password = edittext_password.getText().toString();
		if (!(string_username.isEmpty() && string_password.isEmpty())) {
			String SQL_select_login = "select * from user where user_name=? and user_pass=?";
			// 判断内容是否匹配数据库
			Cursor cur = mdbHelper.getReadableDatabase().rawQuery(
					SQL_select_login,
					new String[] { string_username, string_password });
			// 指针小于0或者等于0 就是没有匹配结果
			if (cur.getCount() <= 0) {
				// 没有匹配的结果
				Toast.makeText(getApplicationContext(), "用户名不存在或密码错误",
						Toast.LENGTH_SHORT).show();
			} else {
				// 有匹配的结果
				 Toast.makeText(getApplicationContext(), "登陆成功",
				 Toast.LENGTH_SHORT).show();
				 Intent intent=new Intent();
				 intent.putExtra("user_name", string_username);
				 intent.setClass(getApplicationContext(), Main_interface.class);
				 startActivity(intent);
			}
			cur.close();
		} else {
			Toast.makeText(getApplicationContext(), "用户名或者密码不能为空",
					Toast.LENGTH_SHORT).show();
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
