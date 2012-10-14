//役のチェック（HashMAPの方が簡単？）
//2週目にHoldがバグる
//シャッフルが微妙

package com.example.poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.poker.R;
import com.example.poker.Card;
import com.example.poker.Poker;
import com.example.poker.PokerView.PokerThread;

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
//	CardsMap cardMap;
	
	//カードの値(HashMapにした方がよさそう)
	int[] num52 = {
			0,1,2,3,4,5,6,7,8,9,10,11,12,13,
			14,15,16,17,18,19,20,21,22,23,24,25,26,
			27,28,29,30,31,32,33,34,35,36,37,38,39,
			40,41,42,43,44,45,46,47,48,49,50,51,
			};
	int[] rCard52 = {
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
//    int[][] iCard52 = {
//			{1,2,3,4,5,6,7,8,9,10,11,12,13},  {1,2,3,4,5,6,7,8,9,10,11,12,13},
//			{1,2,3,4,5,6,7,8,9,10,11,12,13},  {1,2,3,4,5,6,7,8,9,10,11,12,13},
//			};
    
	//private Integer j01 = R.drawable.j01;	//ジョーカー
	//private Integer uk0 = R.drawable.uk0;	//裏イメージ
	
	private int index52 = 0;	//カード52枚のカード番号
	private int iCard5[] = new int[5];	//表示する5枚のカードにカード番号を格納
	private int iFrontCard=0;	//カードの表の枚数
	
	private Drawable[] dCard52 = new Drawable[rCard52.length];	//表示する5枚のカード(52枚)？
	private Drawable dCard_ura;	//表示する裏カード
	private Drawable dStartBtn,dChangeBtn;		//ボタンイメージ
	
	private boolean[] hold = new boolean[5];		//カードのHold状態
	private boolean playing = false;//プレイ中の状態,ボタンの状態(スタート、チェンジ)
	
	private int score = 0;			//獲得点数
	Handler handler;	//スレッド用
	Resources resources;
//	Map<Integer,Integer> map;
	List<Card> list;
	String msg = null;	//役名
	boolean shuffle;	//シャッフルするかどうか
	int totalcount;		//合計プレイ回数
	
	public PokerView(Context context){
		super(context);
		this.init(context);
	}
	public PokerView(Context context, AttributeSet attrs){
		super(context, attrs);
		this.init(context);
		
		this.list = new ArrayList<Card>();
	    int i = 0;	//52回(i=0~51)
		for(int j=0; j<4; j++){	//4回(j=0~3)
			for(int k=0; k<13; k++){	//13回(k=0~12)
				this.list.add(new Card(i+1,j+1,k+1));
				i++;
			}
		}
		
		
		
		
		//Mapに登録
//		this.cardMap = new CardsMap(rCard52,iCard52);
//		this.map = this.cardMap.getCardsMap();
	}
	
	//初期化
	public void init(Context context){
		this.poker = (Poker)context;
		
		index52 = 0;
		iFrontCard=0;
		
		//リソースの取得
		resources = this.poker.getResources();
		//ボタンリソース登録
		this.dStartBtn = resources.getDrawable(R.drawable.start);
		this.dStartBtn.setBounds((int)btn_x, (int)btn_y, (int)(btn_x+btn_w), (int)(btn_y+btn_h));
		this.dChangeBtn = resources.getDrawable(R.drawable.change);
		this.dChangeBtn.setBounds((int)btn_x, (int)btn_y, (int)(btn_x+btn_w), (int)(btn_y+btn_h));
		
//		//ランダムな配置にする
//		for(int i=0; i<30000; i++){
//			Random r = new Random(new Date().getTime());
//			int a = r.nextInt(this.rCard52.length);
//			int b = r.nextInt(this.rCard52.length);
//			int val = this.rCard52[a];
//			this.rCard52[a] = this.rCard52[b];
//			this.rCard52[b] = val;
//		}
//		//裏カードリソース登録
//		this.dCard_ura = resources.getDrawable(R.drawable.uk0);
//		//表52枚カードリソース登録
//		for(int i=0; i<52; i++){
//			this.dCard52[i] = resources.getDrawable(rCard52[i]);
//		}
		
		//num52をランダムな配置にする
		for(int i=0; i<30000; i++){
			Random r = new Random(new Date().getTime());
			int a = r.nextInt(this.num52.length);
			int b = r.nextInt(this.num52.length);
			int val = this.num52[a];
			this.num52[a] = this.num52[b];
			this.num52[b] = val;
		}
		//裏カードリソース登録
		this.dCard_ura = resources.getDrawable(R.drawable.uk0);
		//表52枚カードリソース登録
		for(int i=0; i<52; i++){
			this.dCard52[i] = resources.getDrawable(rCard52[num52[i]]);
		}
		

		
	}
	

	
	//自動更新
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
		
		if(iFrontCard==5 && !playing && msg=="ロイヤルフラッシュ") p.setColor(Color.RED);
		canvas.drawText("ロイヤルフラッシュ", 30, 60, p);	canvas.drawText("5000", 350, 60, p);
		if(iFrontCard==5 && !playing && msg=="ロイヤルフラッシュ") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="ストレートフラッシュ") p.setColor(Color.RED);
		canvas.drawText("ストレートフラッシュ", 30, 110, p);canvas.drawText("1000", 350, 110, p);
		if(iFrontCard==5 && !playing && msg=="ストレートフラッシュ") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="4カード") p.setColor(Color.RED);
		canvas.drawText("4カード", 35, 180, p);canvas.drawText("400", 180, 180, p);
		if(iFrontCard==5 && !playing && msg=="4カード") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="フルハウス") p.setColor(Color.RED);
		canvas.drawText("フルハウス", 30, 230, p);canvas.drawText("100", 180, 230, p);
		if(iFrontCard==5 && !playing && msg=="フルハウス") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="フラッシュ") p.setColor(Color.RED);
		canvas.drawText("フラッシュ", 30, 280, p);canvas.drawText("70", 194, 280, p);
		if(iFrontCard==5 && !playing && msg=="フラッシュ") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="ストレート") p.setColor(Color.RED);
		canvas.drawText("ストレート", 30, 330, p);canvas.drawText("50", 194, 330, p);
		if(iFrontCard==5 && !playing && msg=="ストレート") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="3カード") p.setColor(Color.RED);
		canvas.drawText("3カード", 290, 180, p);canvas.drawText("30", 410, 180, p);
		if(iFrontCard==5 && !playing && msg=="3カード") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="2ペア") p.setColor(Color.RED);
		canvas.drawText("2ペア", 290, 230, p);canvas.drawText("20", 410, 230, p);
		if(iFrontCard==5 && !playing && msg=="2ペア") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg=="1ペア") p.setColor(Color.RED);
		canvas.drawText("1ペア", 290, 280, p);canvas.drawText("10", 410, 280, p);
		if(iFrontCard==5 && !playing && msg=="1ペア") p.setColor(Color.WHITE);
		
		if(iFrontCard==5 && !playing && msg==null) p.setColor(Color.RED);
		canvas.drawText("ノーペア", 290, 330, p);canvas.drawText("0", 424, 330, p);
		if(iFrontCard==5 && !playing && msg==null) p.setColor(Color.WHITE);
		
		//アンダーライン
		p.setStrokeWidth(1);
		canvas.drawLine(30, 65, 410, 65, p);
		canvas.drawLine(30, 115, 410, 115, p);
		canvas.drawLine(30, 185, 230, 185, p);canvas.drawLine(290, 185, 440, 185, p);
		canvas.drawLine(30, 235, 230, 235, p);canvas.drawLine(290, 235, 440, 235, p);
		canvas.drawLine(30, 285, 230, 285, p);canvas.drawLine(290, 285, 440, 285, p);
		canvas.drawLine(30, 335, 230, 335, p);canvas.drawLine(290, 335, 440, 335, p);
		
		p.setTextSize(26);
		if(shuffle){
			p.setColor(Color.RED);
			canvas.drawText("シャッフル", 340, 380, p);
			p.setColor(Color.WHITE);
		}else{
			if(this.index52 > 42){
				p.setColor(Color.RED);
				canvas.drawText(this.index52+" / 52", 380, 380, p);
				p.setColor(Color.WHITE);
			}else{
				canvas.drawText(this.index52+" / 52", 380, 380, p);
			}
		}
		p.setTextSize(36);
		canvas.drawText("ゲーム数 : "+this.totalcount, 50, 400, p);
		canvas.drawText("スコア　 : "+this.score, 50, 470, p);
		

		//表の表示(0~5枚)
		for(int i=0; i<iFrontCard; i++){
			//カードを残すかどうか？
			if(this.hold[i] && this.playing){
				//四角形の枠
				p.setStyle(Style.FILL_AND_STROKE);
				p.setColor(Color.YELLOW);
				canvas.drawRect(new Rect(i*92+10, 500, i*92+92, 600), p);
				//テキスト(Hold)表示
				canvas.drawText("Hold", i*92+20, 640, p);
			}
			//this.dCard52[iCard5[i]].setBounds(i*30+100, 100, i*30+124, 132);
			this.dCard52[iCard5[i]].setBounds(i*92+10, 500, i*92+92, 600);
			this.dCard52[iCard5[i]].draw(canvas);
		}

		
		//裏の表示(5~0枚)
		for(int i=iFrontCard; i<5; i++){
			if(this.hold[i]){
				this.dCard52[iCard5[i]].setBounds(i*92+10, 500, i*92+92, 600);
				this.dCard52[iCard5[i]].draw(canvas);
				//this.hold[i]=false;
			}else{
				//this.dCard_ura = resources.getDrawable(rCard52[0]);	//表52枚
				this.dCard_ura.setBounds(i*92+10, 500, i*92+92, 600);
				this.dCard_ura.draw(canvas);
				continue;
			}
		}
		if(iFrontCard<5) iFrontCard++;
		else{
			
		}
		if(totalcount>0 && this.iFrontCard<5){
		}else{
			//ボタンの表示
			if(this.playing){
				this.dChangeBtn.draw(canvas);	//チェンジ
			}else{
				this.dStartBtn.draw(canvas);	//スタート
			}
		}
		
	}

	
	//画面を操作したとき
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//アクション、座標
		int action = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(totalcount>0 && this.iFrontCard<5) break;
			
			//スタートorチェンジボタンを押したとき
			if(this.isIn(x,y,this.dStartBtn.getBounds())){
				
				//表の枚数の初期化
				iFrontCard=0;

				//次の5枚を準備
				for(int i=0; i<5; i++){
					if(this.hold[i] && this.playing){
						continue;
					}
					if(this.index52 < 52){
						this.iCard5[i] = index52;
					}else{
						this.index52 = 0;
						this.iCard5[i] = index52;
					}
					this.index52++;
				}

				
				
				
				//スタートを押したとき
				if(!this.playing){
					for(int i=0; i<5; i++){
						this.hold[i] = false;
					}
					this.totalcount++;	//プレイ回数
					this.shuffle=false;
					
				}
				//チェンジを押したとき
				if(this.playing){
					//残り10枚未満なら初期化(シャッフル)
					if(this.index52 > 42){
						this.init(poker);
						this.shuffle=true;
					}
				}
				
				//プレイ中（スタート、チェンジ）の切り替え
				this.playing = !this.playing;
				
				//スレッド実行
				handler = new Handler();
				PokerThread thread = new PokerThread();
		    	thread.start();
		    	
			}
			
			//ホールド
			for(int i=0; i<5; i++){
				//カードが押されたとき
				if(this.isIn(x, y, this.dCard52[i].getBounds()) && this.playing){
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
		msg = null;	//役名
		boolean straight = false;	//ストレート
		boolean flush = false;	//フラッシュ

		Card[] c = {list.get(num52[iCard5[0]]),
					list.get(num52[iCard5[1]]),
					list.get(num52[iCard5[2]]),
					list.get(num52[iCard5[3]]),
					list.get(num52[iCard5[4]]),};
		int[] n5 = {c[0].n,c[1].n,c[2].n,c[3].n,c[4].n,};
		Arrays.sort(n5);//ソート
		System.out.println(n5[0]+","+n5[1]+","+n5[2]+","+n5[3]+","+n5[4]);
		
		//ストレート
		if(n5[0]==1 && n5[1]==2 && n5[2]==3 && n5[3]==4 && n5[4]==5) straight=true;
		else if(n5[0]==2 && n5[1]==3 && n5[2]==4 && n5[3]==5 && n5[4]==6) straight=true;
		else if(n5[0]==3 && n5[1]==4 && n5[2]==5 && n5[3]==6 && n5[4]==7) straight=true;
		else if(n5[0]==4 && n5[1]==5 && n5[2]==6 && n5[3]==7 && n5[4]==8) straight=true;
		else if(n5[0]==5 && n5[1]==6 && n5[2]==7 && n5[3]==8 && n5[4]==9) straight=true;
		else if(n5[0]==6 && n5[1]==7 && n5[2]==8 && n5[3]==9 && n5[4]==10) straight=true;
		else if(n5[0]==7 && n5[1]==8 && n5[2]==9 && n5[3]==10 && n5[4]==11) straight=true;
		else if(n5[0]==8 && n5[1]==9 && n5[2]==10 && n5[3]==11 && n5[4]==12) straight=true;
		else if(n5[0]==9 && n5[1]==10 && n5[2]==11 && n5[3]==12 && n5[4]==13) straight=true;
		else if(n5[0]==1 && n5[1]==10 && n5[2]==11 && n5[3]==12 && n5[4]==13) straight=true;
		//フラッシュ
		if(c[0].s==c[1].s && c[0].s==c[2].s && c[0].s==c[3].s && c[0].s==c[4].s) flush=true;
		
		//ロイヤルフラッシュ
		if(straight && flush && n5[0]==1 && n5[4]==13) msg="ロイヤルフラッシュ";
		//ストレートフラッシュ
		else if(straight && flush) msg="ストレートフラッシュ";
		//4カード
		else if(c[0].n==c[1].n && c[0].n==c[2].n && c[0].n==c[3].n) msg="4カード";
		else if(c[0].n==c[1].n && c[0].n==c[2].n && c[0].n==c[4].n) msg="4カード";
		else if(c[0].n==c[1].n && c[0].n==c[3].n && c[0].n==c[4].n) msg="4カード";
		else if(c[0].n==c[2].n && c[0].n==c[3].n && c[0].n==c[4].n) msg="4カード";
		else if(c[1].n==c[2].n && c[1].n==c[3].n && c[1].n==c[4].n) msg="4カード";
		//フルハウス
		else if(c[0].n==c[1].n && c[0].n==c[2].n && c[3].n==c[4].n) msg="フルハウス";
		else if(c[0].n==c[1].n && c[0].n==c[3].n && c[2].n==c[4].n) msg="フルハウス";
		else if(c[0].n==c[1].n && c[0].n==c[4].n && c[2].n==c[3].n) msg="フルハウス";
		else if(c[0].n==c[2].n && c[0].n==c[3].n && c[1].n==c[4].n) msg="フルハウス";
		else if(c[0].n==c[2].n && c[0].n==c[4].n && c[2].n==c[3].n) msg="フルハウス";
		else if(c[0].n==c[3].n && c[0].n==c[4].n && c[1].n==c[2].n) msg="フルハウス";
		else if(c[1].n==c[2].n && c[1].n==c[3].n && c[0].n==c[4].n) msg="フルハウス";
		else if(c[1].n==c[2].n && c[1].n==c[4].n && c[0].n==c[3].n) msg="フルハウス";
		else if(c[1].n==c[3].n && c[1].n==c[4].n && c[0].n==c[2].n) msg="フルハウス";
		else if(c[2].n==c[3].n && c[2].n==c[4].n && c[0].n==c[1].n) msg="フルハウス";
		//フラッシュ
		else if(flush) msg="フラッシュ";
		//ストレート
		else if(straight) msg="ストレート";
		//3カード
		else if(c[0].n==c[1].n && c[0].n==c[2].n) msg="3カード";
		else if(c[0].n==c[1].n && c[0].n==c[3].n) msg="3カード";
		else if(c[0].n==c[1].n && c[0].n==c[4].n) msg="3カード";
		else if(c[0].n==c[2].n && c[0].n==c[3].n) msg="3カード";
		else if(c[0].n==c[2].n && c[0].n==c[4].n) msg="3カード";
		else if(c[0].n==c[3].n && c[0].n==c[4].n) msg="3カード";
		else if(c[1].n==c[2].n && c[1].n==c[3].n) msg="3カード";
		else if(c[1].n==c[2].n && c[1].n==c[4].n) msg="3カード";
		else if(c[1].n==c[3].n && c[1].n==c[4].n) msg="3カード";
		else if(c[2].n==c[3].n && c[2].n==c[4].n) msg="3カード";
		//2ペア
		else if(c[0].n==c[1].n && c[2].n==c[3].n) msg="2ペア";
		else if(c[0].n==c[1].n && c[2].n==c[4].n) msg="2ペア";
		else if(c[0].n==c[1].n && c[3].n==c[4].n) msg="2ペア";
		else if(c[0].n==c[2].n && c[3].n==c[4].n) msg="2ペア";
		else if(c[1].n==c[2].n && c[3].n==c[4].n) msg="2ペア";
		//1ペア
		else if(c[0].n==c[1].n) msg="1ペア";
		else if(c[0].n==c[2].n) msg="1ペア";
		else if(c[0].n==c[3].n) msg="1ペア";
		else if(c[0].n==c[4].n) msg="1ペア";
		else if(c[1].n==c[2].n) msg="1ペア";
		else if(c[1].n==c[3].n) msg="1ペア";
		else if(c[1].n==c[4].n) msg="1ペア";
		else if(c[2].n==c[3].n) msg="1ペア";
		else if(c[2].n==c[4].n) msg="1ペア";
		else if(c[3].n==c[4].n) msg="1ペア";
		
		if(msg!=null){
			Toast toast = Toast.makeText(poker, msg, Toast.LENGTH_SHORT);
			toast.show();
			
			if("ロイヤルフラッシュ".equals(msg)) score += 5000;
			else if("ストレートフラッシュ".equals(msg)) score += 1000;
			else if("4カード".equals(msg)) score += 400;
			else if("フルハウス".equals(msg)) score += 100;
			else if("フラッシュ".equals(msg)) score += 70;
			else if("ストレート".equals(msg)) score += 50;
			else if("3カード".equals(msg)) score += 30;
			else if("2ペア".equals(msg)) score += 20;
			else if("1ペア".equals(msg)) score += 10;
		}
		//msg = null;			//役名
		
		straight = false;	//ストレート
		flush = false;		//フラッシュ
	}
	
	
	//スレッド
	public class PokerThread extends Thread{
		public void run(){
			for(int i=0; i<6; i++){
	    		try{
	    			//ホールド個所のsleep時間を短縮(10にするとスレッドが競合するようで正常に動かない時がある)
	    			//カードがめくられる更新はi=1~5(i=0は全部裏の更新)
	    			if(i!=0 && hold[i-1]) Thread.sleep(30);
    				else Thread.sleep(300);
				}catch(Exception e){}

				//postメソッド
	    		handler.post(new Runnable(){
	    			public void run(){
						if(iFrontCard==5 && !playing){
	    					check();
	    				}
						invalidate();
	    			}
	    		});
			}
		}
	}
}