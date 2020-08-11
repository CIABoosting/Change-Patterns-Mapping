package com.troy.ui.base;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class BaseJDialog extends JDialog{
	private static final long serialVersionUID =123457898776l;
	public BaseJDialog(Frame f,boolean b){
		super(f, b);
	}
	public BaseJDialog(Frame f,String b){
		super(f, b);
	}
	
	public void setLogo(){
		Toolkit   toolkit=this.getToolkit(); 		
		Image   topicon=toolkit.getImage("imag\\logo.jpg");
		this.setIconImage(topicon);
	}
	
	public void showMe() {
		this.setBounds(540,150,400,360);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
