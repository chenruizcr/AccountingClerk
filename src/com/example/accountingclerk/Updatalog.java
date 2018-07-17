package com.example.accountingclerk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Updatalog extends Activity {
	TextView textView;
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
		setContentView(R.layout.updatalog);
		textView=(TextView) findViewById(R.id.updatalog);
		String updatalog_string=""
				+ "2016.12.30\n"
				+ "-添加收入本月收入支出报表\n"
				+ "-更新测试数据\n"
				+ "-修改按钮按下样式\n\n"
				+ "2016.12.7\n"
				+ "-去掉5.0以上的按钮阴影效果\n"
				+ "-调整输入控件高度\n\n"
				+ "2016.11.24\n"
				+ "-修改月收入等控件的显示上限\n\n"
				+ "2016.11.23\n"
				+ "-添加收入功能\n\n"
				+ "2016.11.22\n"
				+ "-微调控件位置和颜色\n"
				+ "-修改项目点击效果\n\n"
				+ "2016.11.21\n"
				+ "-添加沉浸式通知栏\n\n"
				+ "2016.11.20\n"
				+ "-添加记住密码\n\n"
				
				;
		textView.setText(updatalog_string);
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
