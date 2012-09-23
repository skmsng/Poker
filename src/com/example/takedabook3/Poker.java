package com.example.takedabook3;

import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Poker extends Activity {
	public float disp_w,disp_h;	//実機ディスプレイサイズ

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //タイトルバー、情報バーを消して全画面表示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //ディスプレイサイズの取得
        WindowManager manager = window.getWindowManager();
        Display disp = manager.getDefaultDisplay();
        this.disp_w = disp.getWidth();
        this.disp_h = disp.getHeight();
        
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
