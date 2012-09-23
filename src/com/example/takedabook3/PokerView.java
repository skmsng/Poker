//役のチェック（HashMAPの方が簡単？）
//2週目にHoldがバグる
//シャッフルが微妙

package com.example.takedabook3;

import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class PokerView extends View{
//	private static final float XPERIA_W = 480;
//	private static final float XPERIA_H = 854;
	private float btn_x = 90;	//スタートボタンのx座標
	private float btn_y = 700;	//スタートボタンのy座標
	private float btn_w = 300;	//スタートボタンの横幅
	private float btn_h = 80;	//スタートボタンの縦幅
	
	private Poker poker;		//Pokerクラス
	
	private int index = 0;	//カードの位置
	private int count[] = new int[5];	//表示する5つのカードの位置
	
	//カードの値(HashMapにした方がよさそう)
	private int[] cardNum = {
			1,2,3,4,5,6,7,8,9,10,11,12,13,  1,2,3,4,5,6,7,8,9,10,11,12,13,
			1,2,3,4,5,6,7,8,9,10,11,12,13,  1,2,3,4,5,6,7,8,9,10,11,12,13,
			};
	private int[] CARDs = {
			R.drawable.d01, R.drawable.d02, R.drawable.d03, R.drawable.d04, R.drawable.d05,
			R.drawable.d06, R.drawable.d07, R.drawable.d08, R.drawable.d09, R.drawable.d10,
			R.drawable.d11, R.drawable.d12, R.drawable.d13,
			R.drawable.c01, R.drawable.c02, R.drawable.c03, R.drawable.c04, R.drawable.c05,
			R.drawable.c06, R.drawable.c07, R.drawable.c08, R.drawable.c09, R.drawable.c10,
			R.drawable.c11, R.drawable.c12, R.drawable.c13,
			R.drawable.h01, R.drawable.h02, R.drawable.h03, R.drawable.h04, R.drawable.h05,
			R.drawable.h06, R.drawable.h07, R.drawable.h08, R.drawable.h09, R.drawable.h10,
			R.drawable.h11, R.drawable.h12, R.drawable.h13,
			R.drawable.s01, R.drawable.s02, R.drawable.s03, R.drawable.s04, R.drawable.s05,
			R.drawable.s06, R.drawable.s07, R.drawable.s08, R.drawable.s09, R.drawable.s10,
			R.drawable.s11, R.drawable.s12, R.drawable.s13,
			};
	//private Integer j01 = R.drawable.j01;	//ジョーカー
	//private Integer uk0 = R.drawable.uk0;	//裏イメージ
	
//	private boolean first = true;(スレッドなしver)
	private Drawable[] cards = new Drawable[CARDs.length];	//表示する5枚のカード
	private Drawable[] ura = new Drawable[CARDs.length];	//表示する裏カード
	private Drawable btn1,btn2;		//ボタンイメージ
	
	private boolean[] hold = new boolean[CARDs.length];		//カードのHold状態
	private boolean playing = false;//プレイ中の状態,ボタンの状態(スタート、チェンジ)
	private int score = 0;			//獲得点数
	private int m=0;	//カードの表の枚数
	
	Handler handler;	//スレッド用
	
	
	public PokerView(Context context){
		super(context);
		this.init(context);
	}
	public PokerView(Context context, AttributeSet attrs){
		super(context, attrs);
		this.init(context);
	}
	
	//初期化
	public void init(Context context){
		this.poker = (Poker)context;
		
		//res（リソース）の操作
		Resources resources = this.poker.getResources();
		this.btn1 = resources.getDrawable(R.drawable.start);
		this.btn1.setBounds((int)btn_x, (int)btn_y, (int)(btn_x+btn_w), (int)(btn_y+btn_h));
		this.btn2 = resources.getDrawable(R.drawable.change);
		this.btn2.setBounds((int)btn_x, (int)btn_y, (int)(btn_x+btn_w), (int)(btn_y+btn_h));
		
		//ランダムな配置にする
		for(int i=0; i<30000; i++){
			Random r = new Random(new Date().getTime());
			int a = r.nextInt(this.CARDs.length);
			int b = r.nextInt(this.CARDs.length);
			int val = this.CARDs[a];
			this.CARDs[a] = this.CARDs[b];
			this.CARDs[b] = val;
		}
		for(int i=0; i<this.CARDs.length; i++){
			this.cards[i] = resources.getDrawable(CARDs[i]);
			
			this.ura[i] = resources.getDrawable(R.drawable.uk0);
			this.ura[i].setBounds(i*92+10, 450, i*92+92, 550);
		}
	}
	
	//サイズ調整
//	private void setPuzzleSize(){
//		float w = this.poker.disp_w;	//実機ウィンドウサイズ
//		float h = this.poker.disp_h;
//		float dw = w / this.XPERIA_W;	//エクスぺリアとの比率
//		float dh = h / this.XPERIA_H;
		
//		this.btn_x = this.btn_x * dw;
//		this.btn_y = this.btn_y * dh;
//		this.btn_w = this.btn_w * dw;
//		this.btn_h = this.btn_h * dh;
			
//		Bitmap img = BitmapFactory.decodeResource(resources, R.drawable.image1);
//		this.board = new PuzzleBoard(board_x, board_y, dw, dh, img);
//	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		//上部四角形
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		p.setColor(Color.WHITE);
		p.setStrokeWidth(2);
		canvas.drawRect(new Rect(20, 20, 460, 350), p);
		//上部テキスト
		p.setStyle(Style.FILL);
		p.setTextSize(26);
		canvas.drawText("ロイヤルフラッシュ", 30, 60, p);	canvas.drawText("5000", 350, 60, p);
		canvas.drawText("ストレートフラッシュ", 30, 110, p);canvas.drawText("1000", 350, 110, p);
		canvas.drawText("4カード", 35, 180, p);		canvas.drawText("3カード", 290, 180, p);
		canvas.drawText("400", 180, 180, p);		canvas.drawText("30", 410, 180, p);
		canvas.drawText("フルハウス", 30, 230, p);		canvas.drawText("2ペア", 290, 230, p);
		canvas.drawText("100", 180, 230, p);		canvas.drawText("20", 410, 230, p);
		canvas.drawText("フラッシュ", 30, 280, p);		canvas.drawText("1ペア", 290, 280, p);
		canvas.drawText("70", 194, 280, p);			canvas.drawText("10", 410, 280, p);
		canvas.drawText("ストレート", 30, 330, p);		canvas.drawText("ノーペア", 290, 330, p);
		canvas.drawText("50", 194, 330, p);			canvas.drawText("0", 424, 330, p);
		//アンダーライン
		p.setStrokeWidth(1);
		canvas.drawLine(30, 65, 410, 65, p);
		canvas.drawLine(30, 115, 410, 115, p);
		canvas.drawLine(30, 185, 230, 185, p);canvas.drawLine(290, 185, 440, 185, p);
		canvas.drawLine(30, 235, 230, 235, p);canvas.drawLine(290, 235, 440, 235, p);
		canvas.drawLine(30, 285, 230, 285, p);canvas.drawLine(290, 285, 440, 285, p);
		canvas.drawLine(30, 335, 230, 335, p);canvas.drawLine(290, 335, 440, 335, p);
		
		p.setTextSize(35);
		canvas.drawText("得点：　"+this.score, 100, 420, p);

		//5枚表示する(スレッドなしver)
//		for(int i=0; i<5; i++){
//			//カードを残すかどうか？
//			if(this.hold[i]){
//				//this.ura.setBounds(i*30+100, 100, i*30+124, 132);
//				//this.ura.draw(canvas);
//				
//				//四角形の枠
//				//p = new Paint();
//				p.setStyle(Style.FILL_AND_STROKE);
//				p.setColor(Color.YELLOW);
//				canvas.drawRect(new Rect(i*92+10, 450, i*92+92, 550), p);
//				//テキスト(Hold)表示
//				canvas.drawText("Hold", i*92+20, 590, p);
//			}
//			//this.cards[count[i]].setBounds(i*30+100, 100, i*30+124, 132);
//			this.cards[count[i]].setBounds(i*92+10, 450, i*92+92, 550);
//			this.cards[count[i]].draw(canvas);
//		}
		//5枚表示する
		for(int i=0; i<m; i++){
			//カードを残すかどうか？
			if(this.hold[i] && this.playing){
				//this.ura.setBounds(i*30+100, 100, i*30+124, 132);
				//this.ura.draw(canvas);
				
				//四角形の枠
				//p = new Paint();
				p.setStyle(Style.FILL_AND_STROKE);
				p.setColor(Color.YELLOW);
				canvas.drawRect(new Rect(i*92+10, 450, i*92+92, 550), p);
				//テキスト(Hold)表示
				canvas.drawText("Hold", i*92+20, 590, p);
			}
			//this.cards[count[i]].setBounds(i*30+100, 100, i*30+124, 132);
			this.cards[count[i]].setBounds(i*92+10, 450, i*92+92, 550);
			this.cards[count[i]].draw(canvas);
		}
//		//最初だけ裏返し(スレッドなしver)
//		for(int i=0; i<5; i++){
//			if(this.first){
//				this.ura[i].draw(canvas);
//				continue;
//			}
//		}
//		this.first = false;
		
		//裏返し
		for(int i=m; i<5; i++){
			if(this.hold[i]){
				this.cards[count[i]].setBounds(i*92+10, 450, i*92+92, 550);
				this.cards[count[i]].draw(canvas);
				//this.hold[i]=false;
			}else{
				this.ura[i].draw(canvas);
				continue;
			}
		}
		if(m<5) m++;
		
		//ボタンの表示
		if(this.playing){
			this.btn2.draw(canvas);	//チェンジ
		}else{
			this.btn1.draw(canvas);	//スタート
		}
	}

	
	//画面を操作したとき
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			
			//ボタンを押したとき
			if(this.isIn(x,y,this.btn1.getBounds())){

				//次の5枚を準備
				for(int i=0; i<this.count.length; i++){
					if(this.hold[i] && this.playing){
						//this.hold[i] = false;(スレッドなしver)
						continue;
					}
					if(this.index < this.cards.length){
						this.count[i] = index;
					}else{
						this.index = 0;
						this.count[i] = index;
					}
					this.index++;
				}

				//表の枚数の初期化
				m=0;
				
				
				//スレッド実行
				handler = new Handler();
				PokerThread thread = new PokerThread();
		    	thread.start();
				
				//スタートを押したとき
				if(!this.playing){
					for(int i=0; i<this.count.length; i++){
						this.hold[i] = false;
					}
				}
				//チェンジを押したとき
				if(this.playing){
//					this.check();
				}
				
				//プレイ中（スタート、チェンジ）の切り替え
				this.playing = !this.playing;
				
				
			}
			
			//ホールド
			for(int i=0; i<this.count.length; i++){
				//カードが押されたとき
				if(this.isIn(x, y, this.cards[i].getBounds()) && this.playing){
					this.hold[i] = !this.hold[i];
					this.invalidate();
				}
			}

//			Toast toast = Toast.makeText(this.poker, "スタート！"	, Toast.LENGTH_SHORT);
//			toast.show();
			break;
		}
		return true;
	}
	
	//Rect（画像ボタン）が押されたかの判定
	public boolean isIn(int x, int y, Rect rect){
		return (x > rect.left) && (x < rect.right) && (y > rect.top) && (y < rect.bottom);
	}
	
	
	//役チェック、点数加算
	public void check(){
		String message = null;	//役名
		if(true){
			Toast toast = Toast.makeText(this.poker, message, Toast.LENGTH_SHORT);
			toast.show();
		}
		this.score += 0;
	}
	
	
	//スレッド
	public class PokerThread extends Thread{
		public void run(){
			for(int i=0; i<6; i++){
	    		try{
					Thread.sleep(200);
				}catch(Exception e){}

				//postメソッド
	    		handler.post(new Runnable(){
	    			public void run(){
	    				invalidate();
	    			}
	    		});
			}
		}
	}
}
