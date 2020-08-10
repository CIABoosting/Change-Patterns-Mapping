package com.troy.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil4UI {
	public static void FileCopy(String oldFilePath, String newFilePath) {
		try {
			File in = new File(oldFilePath);
			File out = new File(newFilePath);
			FileInputStream inFile = new FileInputStream(in);
			FileOutputStream outFile = new FileOutputStream(out);
			byte[] buffer = new byte[1024];
			int i = 0;
			while ((i = inFile.read(buffer)) != -1) {
				outFile.write(buffer, 0, i);
				System.out.println(i);
			}
			inFile.close();
			outFile.close();
		}
		catch (Exception e) {
		}
	}
	
	 public void copy(File f,File f1) throws IOException{    //复制文件的方法！
	        if(!f1.exists()){
	            f1.mkdir();
	        }
	        if(!f1.exists()){//路径判断，是路径还是单个的文件
	            File[] cf = f.listFiles();
	            for(File fn : cf){
	                if(fn.isFile()){
	                    FileInputStream fis = new FileInputStream(fn);
	                    FileOutputStream fos = new FileOutputStream(f1 + "\\" +fn.getName());
	                    byte[] b = new byte[1024];
	                    int i = fis.read(b);
	                    while(i != -1){
	                        fos.write(b, 0, i);
	                        i = fis.read(b);
	                    }
	                    fis.close();
	                    fos.close();
	                }else{
	                    File fb = new File(f1 + "\\" + fn.getName());
	                    fb.mkdir();
	                    if(fn.listFiles() != null){//如果有子目录递归复制子目录！
	                        copy(fn,fb);
	                    }
	                }
	            }
	        }else{
	            FileInputStream fis = new FileInputStream(f);
	            FileOutputStream fos = new FileOutputStream(f1 + "\\" +f.getName());
	            byte[] b = new byte[1024];
	            int i = fis.read(b);
	            while(i != -1){
	                fos.write(b, 0, i);
	                i = fis.read(b);
	            }
	            fis.close();
	            fos.close();
	        }
	    }
		public static void OpenTxtFile(String path){//自动打开TXT文档
			try {
				Runtime run = Runtime.getRuntime();
				      String[] arg = new String[3];
				      arg[0] = "cmd";
				      arg[1] = "/c";
				      arg[2] = "Explorer.exe /n , /select,"+path;
				      run.exec(arg);
				    }
				    catch (IOException e) {
				      e.printStackTrace();
				    } 
		}
		
		public static double getDirSize(File file) {     
	        //判断文件是否存在     
	        if (file.exists()) {     
	            //如果是目录则递归计算其内容的总大小    
	            if (file.isDirectory()) {     
	                File[] children = file.listFiles();     
	                double size = 0;     
	                for (File f : children)     
	                    size += getDirSize(f);     
	                return size;     
	            } else {//如果是文件则直接返回其大小,以“kb”为单位   
	                double size = (double) file.length() / 1024;        
	                return size;     
	            }     
	        } else {     
	            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");     
	            return 0.0;     
	        }     
	    }     
	public static void main(String args[]) throws IOException{
		FileUtil4UI fileUtil = new FileUtil4UI();
		FileUtil4UI.FileCopy("E:/a","D:/a/LDA");
		File file = new File("E:/a/LDA");
		file.createNewFile();
		System.out.println("kkkk");
	}

}
