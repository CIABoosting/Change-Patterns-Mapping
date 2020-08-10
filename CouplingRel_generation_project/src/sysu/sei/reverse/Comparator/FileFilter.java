package sysu.sei.reverse.Comparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//还需要处理的问题：1，空格；2，空行；3，断行的区别

import com.troy.ui.util.TemporarySpace;


public class FileFilter {

	//遍历文件夹，将每个文件里面已//开始的注释行过滤掉，重新存储文件
	//原因：AST parser在包含//开始的文件中会跳过一些行数，怀疑是ast本身的bug，未求证
	
	public void filteringAnnotation4folder(String filePath){
		File file = new File(filePath);
		if(file.isDirectory()){

			File[] fileList = file.listFiles();
		    for(int i = 0; i < fileList.length; i++){
		    	if(fileList[i].isFile()){
		    		filteringAnnotation4file(fileList[i]);
		    	}
		    	else{
		    		filteringAnnotation4folder(fileList[i].getAbsolutePath());
		    	}
		    }
		}
	}
	
	//遍历文件里的每一行，将以//开始的注释行过滤
	public void filteringAnnotation4file(File file){
		BufferedReader File_pwd;
		try {
			File_pwd = new BufferedReader(new FileReader(file.getAbsolutePath()));
		
			List<String> list=new ArrayList<String>();
			   //声明读文件行的临时变量
			String temp;                                             
			try {   
			
				do{                                                      
			    //按行循环读取文件
				
					temp=File_pwd.readLine();
				
				   // System.out.println("读取到的原文件:"+temp);
				    list.add(temp);                                      
				    //把读取到的行存入数组变量
				   }while(temp!=null);
			  
			
			   File_pwd.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   //将内容写到新文件
			try {   
				BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(file.getAbsolutePath())));
				String s=new String();
				   //为注释行的标示
				int commentFlag = 0;
				for(int j=0;j<list.size()-1;j++){
				    //使用循环把行字符串取出来,并调用replaceall函数,对字符内容进行正则表达式替换
					s=list.get(j);
				    if (s.indexOf("//") >= 0){// || s.indexOf("*") >= 0) {
				    	s = s.substring(0, s.indexOf("//"));
				    	commentFlag = 1;
				    } 
				    else {
				    	if (!"".equals(s.trim())) {
				     	commentFlag = 0;
				    
				    	}
				    }
				    //如果前一行为注释行，该行为空行则删除
				    
				    if (commentFlag == 1) {
				    
				    	if (!"".equals(s.trim())) {
				      
				    		s.replace(" ", "    ");
				      
				    		File_bak.write(s+"\n");  
				     
				    	}
				    
				    }
				    else {
				     
				    	s.replace(" ", "    ");
				     
				    	File_bak.write(s+"\n");  
				    
				    }
				   
				}
				   //必须先刷新,才能用close关闭
				File_bak.flush();
				
					File_bak.close();
			//	System.out.println("file write success");
				TemporarySpace.setConsoleString("file write success");
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}	
	
	public static void main(String[] args) {
		FileFilter filter = new FileFilter();
		filter.filteringAnnotation4file(new File ("E:\\ImpactAnalysis\\Spring.java"));
		System.out.println("filter");
		
	}
}

	

