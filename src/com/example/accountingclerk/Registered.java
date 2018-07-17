package com.example.accountingclerk;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registered extends Activity implements OnClickListener{
	// 控件变量声明
	EditText edittext_user, edittext_password;
	Button button_Registered, button_back;
	// 初始化数据库
	final Database mdbHelper = new Database(this, "user.db3", null, 1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registered);
		
		
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    
            setTranslucentStatus(true);    
            SystemBarTintManager tintManager = new SystemBarTintManager(this);    
            tintManager.setStatusBarTintEnabled(true);    
            tintManager.setStatusBarTintResource(R.color.orange500);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            
        }   
		
		
		//控件变量赋值
		edittext_user=(EditText) findViewById(R.id.Registered_edittext_user);
		edittext_password=(EditText) findViewById(R.id.Registered_edittext_password);
		button_Registered=(Button) findViewById(R.id.Registered_button_registered);
		button_back=(Button) findViewById(R.id.Registered_button_back);
		//绑定按钮监听事件
		button_Registered.setOnClickListener(this);
		button_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Registered_button_registered:
			registered();
			break;
		case R.id.Registered_button_back:
			finish();
			break;

		default:
			break;
		}
	}
	public void registered(){
		String string_user=edittext_user.getText().toString();
		String string_password=edittext_password.getText().toString();
		String SQL_select_user="select * from user where user_name=?";
		String SQL_insert_userpw="insert into user values(?,?,?,?,?,?,?)";
		if (!(string_user.isEmpty() && string_password.isEmpty())) {
			//判断内容是否匹配数据库
			Cursor cur = mdbHelper.getReadableDatabase().rawQuery(SQL_select_user,new String[]{string_user});
			if(cur.getCount()<=0){
				//没有匹配的结果
				//执行语句
				mdbHelper.getReadableDatabase().execSQL(SQL_insert_userpw,new String[]{null,string_user,string_password,"2000","true","吃饭,交通,购物,水果,娱乐,服装,水电费,","工资,红包,兼职,退款,"});
				Toast.makeText(getApplicationContext(), "注册成功,请登录!", Toast.LENGTH_SHORT).show();
				if (string_user.equals("11")) {
					testdata(string_user);
				}
				else {
					finish();
				}
				
				}else{

				//有匹配的结果
				Toast.makeText(getApplicationContext(), "账户已存在,请登陆!", Toast.LENGTH_SHORT).show();
				}
		}
		else {
			Toast.makeText(getApplicationContext(), "帐号密码不能为空!", Toast.LENGTH_SHORT).show();
		}
	}
	public void testdata(final String username){
		// 获取本月时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		final String string_now_date = formatter.format(curDate);
		
		AlertDialog.Builder aBuilder=new AlertDialog.Builder(this);
		aBuilder.setTitle("已注册测试账户,是否添加测试数据");
		aBuilder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,"2016_10_12","70","交通","去上海车费","pay"});
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,"2016_10_20","10","吃饭","食堂吃饭","pay"});
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,"2016_11_19","30","吃饭","弗雷德吃饭","pay"});
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,"2016_11_20","45","购物","买了日用品","pay"});
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,string_now_date+"20","50","吃饭","和朋友一起吃饭","pay"});
				mdbHelper.getReadableDatabase().execSQL("insert into data values(?,?,?,?,?,?,?)",new String[]{null,username,string_now_date+"20","50","工资","和朋友一起吃饭","income"});
				
				finish();
			}
		});
		aBuilder.setNegativeButton("取消", null).create().show();
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
