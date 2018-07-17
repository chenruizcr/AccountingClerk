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
				+ "-������뱾������֧������\n"
				+ "-���²�������\n"
				+ "-�޸İ�ť������ʽ\n\n"
				+ "2016.12.7\n"
				+ "-ȥ��5.0���ϵİ�ť��ӰЧ��\n"
				+ "-��������ؼ��߶�\n\n"
				+ "2016.11.24\n"
				+ "-�޸�������ȿؼ�����ʾ����\n\n"
				+ "2016.11.23\n"
				+ "-������빦��\n\n"
				+ "2016.11.22\n"
				+ "-΢���ؼ�λ�ú���ɫ\n"
				+ "-�޸���Ŀ���Ч��\n\n"
				+ "2016.11.21\n"
				+ "-��ӳ���ʽ֪ͨ��\n\n"
				+ "2016.11.20\n"
				+ "-��Ӽ�ס����\n\n"
				
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
