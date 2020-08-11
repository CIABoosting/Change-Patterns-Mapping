package sysu.sei.reverse.LDAinput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.troy.ui.util.TemporarySpace;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.Dictionary;

public class InputCopper {

	//定义需要过滤的关键字
	private static String[] keywordList = {
		"/r","/n",
		"public","private","static","protected",
		"com","net","author","revision","Revision",
		"is", "are","a",
		"it","It","the","The","that","That","This","this",
		"with","and","of","a","an","to","by","in","at","if","as",
		"void", "true","false","boolean",
		
		"String","null","int", "long", "float", "Boolean","object",
		"org","eclipse","emf","final",
		"ecore","impl","package","final","since","class","gen","model","plugin",
		"org.eclipse.emf.codegen.ecore.genmodel.impl.GenClassImpl","codegen","genmodel",
		"@link","@generated","@since",
		"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
		
	     };


	//定义需要过滤的符号

	private static String[] notationList = {
			"<!-- begin-user-doc -->","<!-- end-user-doc -->","<b>","<tr>","</td>","<td>","</tr>","<p>","</p>","<li>","<ul>","<pre>","</li>","</ul>","</pre>",
			"<table>","</table>","<tt>","</tt>","<tr","<tt","<td","< b>","<em>","</em>","<ul>","</ul>",
		    "*","@","&","$",".","/",":","?","{","}",",",")","(",";","#","@","^","+","_","<",">","/r","/n","=","/*"
		//"<",">",
	};	
	/*
private static String[] keywordList2 = {
	"public","private","static",
	"com","net","author","revision","Revision",
	"is", "are",
	"it","It","the","The","that","That","This","this",
	"with","and","of","a","an","to","by","in","at","if","as",
	"void", "true","false",
	"String","null","int", "long", "float", "Boolean",
	"org",
	"argouml"};	

//定义需要过滤的符号

private static String[] notationList2 = {
	"*","@","&","$",".","/",":","?","(",")","<",">","-","/n",
	"/r"
};
   */ 
	public void filterWords(String filePath){
		
		BufferedReader File_pwd;
		try {
			File_pwd = new BufferedReader(new FileReader(filePath));
		
			List<String> list=new ArrayList<String>();
			   //声明读文件行的临时变量
			String temp;                                             
			try {   
			
				do{                                                      
			    //按行循环读取文件
				
					temp=File_pwd.readLine();
					
				   System.out.println("读取到的原文件:"+temp);
				   TemporarySpace.setConsoleString("读取到的原文件:"+temp);
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
				
				BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(filePath)));
				String s=new String();
				 
				for(int j=0;j<list.size();j++){
				//	System.out.println(list.size());
					
				    //过滤关键字
					s=list.get(j);
					//System.out.println(s);
				    if(keywordList != null){
				    	for(int i =0; i < keywordList.length; i++){
				    		int indext = 0;
				    		while(s != null && s.substring(indext).indexOf(keywordList[i])>=0){
				    		//	System.out.println(i+ "  "+ indext);
				    			TemporarySpace.setConsoleString(i+ "  "+ indext);
				    			int subIndext = s.substring(indext).indexOf(keywordList[i]);
				    			indext = indext + subIndext;
				    			if(indext  == 0){
				    				if(s.length() == keywordList[i].length()||( s.length() > keywordList[i].length() &&(! Character.isLetter(s.charAt(indext+keywordList[i].length()))))){
				    					s = s.substring(keywordList[i].length());
				    				}
				    				else
			    						indext = indext + keywordList[i].length();
				    			}
				    			else{
				    				if(! Character.isLetter(s.charAt(indext-1))){
				    					if(s.length() == keywordList[i].length()+indext||( (s.length()-indext) > keywordList[i].length() &&(! Character.isLetter(s.charAt(indext+keywordList[i].length()))))){
				    						s = s.substring(0, indext-1)+" "+s.substring(indext +keywordList[i].length());
							    	      //  System.out.println("test");
				    					}
				    					else
				    						indext = indext + keywordList[i].length();
				    				}
				    				else{
				    					indext = indext + keywordList[i].length();
				    				//	  System.out.println("test2222");
				    				}
				    			}
				    		
				    		}
				    		
				    	}
				    }
				      //过滤特殊符号
				    if(notationList != null){
				    	for(int i =0; i < notationList.length; i++){
				    		while(s != null && s.indexOf(notationList[i])>=0){
				    	//		System.out.println(i);
				    			if(s.indexOf(notationList[i]) == 0)
				    				s = s.substring(notationList[i].length());
				    			else
				    				s = s.substring(0, s.indexOf(notationList[i]))+" "+s.substring(s.indexOf(notationList[i])+notationList[i].length());
				    		}
				    	}
				    }   
				    //做一些默认的处理
				    //word splite
				    if(s != null){
					    for(int i =0; i < s.length(); i++){
					    	if(Character.isUpperCase(s.charAt(i))){
					    		if(i !=0 && (Character.isLowerCase(s.charAt(i-1))||(s.length() >(i+1)  && Character.isLowerCase(s.charAt(i+1)) ))){
					    		//	System.out.println("i "+i + " j "+j+ "   "+s);
					    			
					    			s = s.substring(0,i)+" "+s.substring(i, i+1).toLowerCase()+s.substring(i+1);
					    			i = i +1;
					    	//		System.out.println(s);
					    		}
					    	}
					    }
				    }
				    if(s != null){
				    	s = s.toLowerCase();
				    	//将每个词转换为其原型
							String word = new String();
						    String wnhome = new String("C:/Program Files (x86)/WordNet/2.1");//System.getenv("WNHOME"); //获取WordNet根目录环境变量WNHOME

						    String path = wnhome + File.separator+ "dict";       

						    File wnDir=new File(path);

						    URL url=new URL("file", null, path);

						    IDictionary dict=new edu.mit.jwi.Dictionary(url);

						    dict.open();//打开词典
						  
							while(s.indexOf(" ")>=0){
								  while(s.indexOf(" ")==0){
										s = s.substring(1);
									}
								if(s.length()>0 && s.indexOf(" ")>=0){
								  word = word + " "+DesExtender.getWordLemma(dict, s.substring(0, s.indexOf(" ")));
								  s = s.substring(s.indexOf(" ")+1);
								}
							}
							if(s.length()>0)
								word = word + " "+DesExtender.getWordLemma(dict,s);
							s = word;
				    	File_bak.write(s+"\r\n");  
					    	
				    }
				     
				}
				   //必须先刷新,才能用close关闭
				File_bak.flush();
				
				File_bak.close();
				System.out.println("file write success");
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
	
	public void filterFiles(String[] fileList){
		if(fileList != null){
			for(int i =0; i < fileList.length; i++){
				this.filterWords(fileList[i]);
			}
		}
	}
	
	public void filterFolder (String folderPath){
		File file = new File(folderPath);
		if(file.isDirectory()){

			File[] fileList = file.listFiles();
		    for(int i = 0; i < fileList.length; i++){
		    	if(fileList[i].isFile()){
		    		this.filterWords(fileList[i].getAbsolutePath());
		    	}
		    }
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		InputCopper copper = new InputCopper();
	//	String[] fileList = new String[]{
		//		"D:/a/version(0vs1)Decision3.txt"};
	//	copper.filterFiles(fileList);
		
		copper.filterFolder("E:/a/test/test2");
		};

	}


