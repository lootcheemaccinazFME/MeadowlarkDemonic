package com.flymaccin.meadowlarkdemonic;
public final class HyphyEngine{
 private double swing=.58,micro=.0; private int roll=2; private boolean bassSlides=true;
 public double swing(){return swing;} public void swing(double v){swing=Math.max(.5,Math.min(.75,v));}
 public double microtiming(){return micro;} public void microtiming(double v){micro=Math.max(-.25,Math.min(.25,v));}
 public int roll(){return roll;} public void roll(int n){roll=Math.max(1,Math.min(16,n));}
 public boolean bassSlides(){return bassSlides;} public void bassSlides(boolean v){bassSlides=v;}
}
