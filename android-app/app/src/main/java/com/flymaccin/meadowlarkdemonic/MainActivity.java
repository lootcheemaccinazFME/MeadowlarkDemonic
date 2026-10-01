package com.flymaccin.meadowlarkdemonic;

import android.app.*;import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;

public class MainActivity extends Activity{
 int gold=Color.rgb(214,179,90),panel=Color.rgb(24,24,31),white=Color.rgb(240,240,244),muted=Color.rgb(145,145,155);
 public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(Color.BLACK);showHome();}
 TextView t(String s,int z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setPadding(18,14,18,14);return v;}
 Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextColor(white);b.setBackgroundColor(panel);b.setOnClickListener(v->Toast.makeText(this,s+" workspace",Toast.LENGTH_SHORT).show());return b;}
 void showHome(){ScrollView sc=new ScrollView(this);LinearLayout p=new LinearLayout(this);p.setOrientation(LinearLayout.VERTICAL);p.setPadding(24,18,24,18);p.setBackgroundColor(Color.rgb(9,9,12));p.addView(t("Meadowlark Demonic",28,gold));p.addView(t("STANDALONE ANDROID EDITION",12,muted));p.addView(t("Meadowlark-inspired standalone Demonic recording and production concept",15,white));p.addView(t("RECORD · COMPOSE · EDIT · MIX · MASTER",13,gold));String[] xs="RECORD · COMPOSE · EDIT · MIX · MASTER".split(" · ");for(String x:xs)p.addView(btn(x));p.addView(t("Native APK foundation. Desktop-only engines remain separate until their DSP/model runtimes are ported or bridged.",12,muted));sc.addView(p);setContentView(sc);}
}
