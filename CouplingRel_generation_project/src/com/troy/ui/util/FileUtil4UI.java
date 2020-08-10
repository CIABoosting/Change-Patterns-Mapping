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
	
	 public void copy(File f,File f1) throws IOException{    //�����ļ��ķ�����
	        if(!f1.exists()){
	            f1.mkdir();
	        }
	        if(!f1.exists()){//·���жϣ���·�����ǵ������ļ�
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
	                    if(fn.listFiles() != null){//�������Ŀ¼�ݹ鸴����Ŀ¼��
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
		public static void OpenTxtFile(String path){//�Զ���TXT�ĵ�
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
	        //�ж��ļ��Ƿ����     
	        if (file.exists()) {     
	            //�����Ŀ¼��ݹ���������ݵ��ܴ�С    
	            if (file.isDirectory()) {     
	                File[] children = file.listFiles();     
	                double size = 0;     
	                for (File f : children)     
	                    size += getDirSize(f);     
	                return size;     
	            } else {//������ļ���ֱ�ӷ������С,�ԡ�kb��Ϊ��λ   
	                double size = (double) file.length() / 1024;        
	                return size;     
	            }     
	        } else {     
	            System.out.println("�ļ������ļ��в����ڣ�����·���Ƿ���ȷ��");     
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
