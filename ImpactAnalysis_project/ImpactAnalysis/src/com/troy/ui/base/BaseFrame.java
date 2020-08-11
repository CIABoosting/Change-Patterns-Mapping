package com.troy.ui.base;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class BaseFrame extends JFrame{
	private static final long serialVersionUID =123457898776l;
	public BaseFrame(String name){
		super(name);
	}
	public static void main(String[] args){
		
	}
	
	public void SetLogo(){
		Toolkit   toolkit=this.getToolkit(); 		
		Image   topicon=toolkit.getImage("imag\\logo.jpg");
		this.setIconImage(topicon);
	}
	
	public void showMe() {
		this.setBounds(300,80,900,600);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setResizable(false);
	}

}
