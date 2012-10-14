package com.example.poker;

public class Card {
	public int i;		//カードID（プライマリーキー）
	//private int r_id;	//Rクラスの画像ID
	public int s;	//スート（マーク）
	public int n;		//数字
	
	public Card(int id, int suit, int num) {
		super();
		this.i = id;
		//this.r_id = r_id;
		this.s = suit;
		this.n = num;
	}
	
	
}
