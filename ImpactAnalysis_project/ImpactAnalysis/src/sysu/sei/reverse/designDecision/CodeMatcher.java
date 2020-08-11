package sysu.sei.reverse.designDecision;

import java.util.ArrayList;

public class CodeMatcher {
	public static boolean ifWordMatched(String methodBody, String name){
		boolean tag = false;
		if(methodBody!= null && (methodBody.indexOf(name)==0||(methodBody.indexOf(name)>0)&& methodBody.substring(methodBody.indexOf(name)-1,methodBody.indexOf(name)).matches("^[a-zA-Z]*")==false)
				&&	(methodBody.indexOf(name)+name.length()==methodBody.length()||(methodBody.indexOf(name)+name.length()<methodBody.length())&& methodBody.substring(methodBody.indexOf(name)+name.length(),methodBody.indexOf(name)+name.length()+1).matches("^[a-zA-Z]*")==false)){					
			tag = true;
		}
		return tag;
	}
	
	public static boolean ifWMinstanceof(String sm, String className){
		boolean tag = false;
		if(sm!= null && className != null && sm.indexOf("instanceof")>=0 &&sm.indexOf(className)>=0 && ifWMsequencewords(sm, "instanceof", className)){
			tag = true;
		}
		return tag;
	}
	
	public static boolean ifChangeoftype(String sm, String className){

		boolean tag = false;
		int index = 0;
		if(sm.indexOf(className)>=0 && ifWMsequencewords(sm, className,")")&& ifWMsequencewords(sm, "(",className)){
			sm = sm.substring(sequencewordsIndex(sm, className,")"));
			index  = sm.indexOf(")")+1;
			while(sm.substring(index, index+1).equals("(")){
				index++;
			}
			if(sm.substring(index, index+1).matches("^[a-zA-Z]*")){
				tag = true;
			}
		}
			
		return tag;
	}
	public static boolean ifWMuseclassdef(String sm, String className){
		
		boolean tag = false;
		if(sm!= null && className != null && sm.indexOf(className)>=0 && ifWMsequencewords(sm,className,".class")){//ifWMsequencewords(sm,className,".")&& ifWMsequencewords(sm,".","class")){
			tag = true;
		}
		return tag;
	}
	
	public static boolean ifWMsequencewords(String sm, String word1, String word2){
		String statement = new String(sm);
		boolean tag = false;
		while(tag== false && statement!= null && word1 != null && word2 != null 
				&& statement.indexOf(word1)>=0 &&statement.substring(statement.indexOf(word1)).indexOf(word2)>=0 ){
			//statement.indexOf(word1)+word1.length()<= statement.indexOf(word2)
			tag = true;
			if(statement.indexOf(word1)+ word1.length() < statement.indexOf(word2)){			
				//System.out.println(tag);
				for(int i = statement.indexOf(word1)+word1.length(); i< statement.indexOf(word2); i++){
					if(!statement.subSequence(i, i+1).equals(" ")){
						tag = false;
					//	System.out.println(statement.subSequence(i, i+1));
					//	System.out.println(tag);
					}
				}
				if(tag == false){
					statement = statement.substring(statement.indexOf(word1)+1);
				}
			}
				
			else{
				if(statement.indexOf(word1)+word1.length()== statement.indexOf(word2)){
					statement = null;
				}
				else{
					statement = statement.substring(statement.indexOf(word1));
					//System.out.println(statement);
					tag = false;
				}
			}
			
		}
		return tag;
	}
	
	public static int sequencewordsIndex(String sm, String word1, String word2){
		String statement = new String(sm);
		int tag = -1;
		while(tag== -1 && statement!= null && word1 != null && word2 != null 
				&& statement.indexOf(word1)>=0 &&statement.substring(statement.indexOf(word1)).indexOf(word2)>=0 ){
			//statement.indexOf(word1)+word1.length()<= statement.indexOf(word2)
			tag = statement.indexOf(word1);
			if(statement.indexOf(word1)+ word1.length() < statement.indexOf(word2)){
				
				//System.out.println(tag);
				for(int i = statement.indexOf(word1)+word1.length(); i< statement.indexOf(word2); i++){
					if(!statement.subSequence(i, i+1).equals(" ")){
						tag = -1;
					//	System.out.println(statement.subSequence(i, i+1));
					//	System.out.println(tag);
					}
				}
				if(tag == -1){
					statement = statement.substring(statement.indexOf(word1)+1);
				}
			}
				
			else{
				if(statement.indexOf(word1)+word1.length()== statement.indexOf(word2)){
					statement = null;
				}
				else{
					statement = statement.substring(statement.indexOf(word1));
					tag = -1;
				}
			}
		}
		return tag;
	}
	public static String deleteBlank(String sm){
		for(int i = 0; i < sm.length(); i++){
			if(sm.substring(i, i+1).equals(" ")){
				if(i+1 == sm.length()){
					sm = sm.substring(0,i);
				}
				else
				sm = sm.substring(0,i)+sm.substring(i+1);
			}
		}
		return sm;
	}
	
	public static String useofMethod(String sm, String mName, int paraSize){
		String objectName = null;
		mName = mName +"(";
	//	System.out.println(ifWMsequencewords(sm, ".", mName));
		if(sm.indexOf(mName) >0&& ifWMsequencewords(sm, ".", mName)){
			int index = sm.indexOf(mName)-1;
			int endIndex = 0;
			while(index >=0 &&(sm.substring(index, index+1).equals(" ")|| sm.substring(index, index+1).equals(".")|| sm.substring(index, index+1).equals(")"))){
				index --;
			}
			endIndex = index+1;
			index --;
			while(index >= 0 && (!sm.substring(index, index+1).equals(")"))&&(!sm.substring(index, index+1).equals(" "))&& (!sm.substring(index, index+1).equals("="))&& (!sm.substring(index, index+1).equals("("))){//matches("^[a-zA-Z]*")){
				index --;
			}
			index ++;
			//System.out.println(index+" "+endIndex);
			objectName = sm.substring(index ,endIndex);
			index = sm.indexOf(mName)+ mName.length()-2;
		//	System.out.println(objectName);
			endIndex = sm.indexOf(mName)+mName.length();
			
			while(endIndex < sm.length()&& sm.substring(endIndex).indexOf("(")<sm.substring(endIndex).indexOf(")")&&sm.substring(endIndex).indexOf("(")>=0&& sm.substring(endIndex).indexOf(")")>=0){
			//	System.out.println("sm  "+(sm.substring(endIndex)));
				
				
			//	System.out.println(endIndex+sm.substring(0,endIndex)+sm.substring(sm.substring(endIndex).indexOf(")")+endIndex+1));
				sm = sm.substring(0,endIndex+sm.substring(endIndex).indexOf(")"))+(sm.substring(sm.substring(endIndex).indexOf(")")+endIndex+1));
			//	System.out.println(sm);
				endIndex =sm.substring(endIndex).indexOf(")")+1;
			//	System.out.println("sm  "+sm);
			}
			
			//System.out.println(objectName);
			if(paraSize >1){
				for(int i=0; i < paraSize-1; i++){				
					if(index >=0){
						sm = sm.substring(index+1);					
						if(sm.indexOf(",")<0){
							objectName = null;
							//System.out.println("test1"+i+sm);
							i = paraSize;
						}		
						else
							index = sm.indexOf(",");				
					}
				
				
					else {
					
						objectName =null;
						i = paraSize;
					//	System.out.println("test2");
					}
				}
			}
			else{
				if(paraSize == 1){
					sm = sm.substring(index);
					if(ifWMsequencewords(sm, "(",")")||(sm.indexOf(",")>0 && sm.indexOf(",")<sm.indexOf(")"))){
						objectName = null;
					//	System.out.println("test1"+(sm.indexOf(",")>0)+"  "+sm.indexOf(")")+" "+ifWMsequencewords(sm, "(",")"));
					}
				}
				if(paraSize == 0){
					
					sm = sm.substring(index);
					if(!ifWMsequencewords(sm, "(",")")){
						objectName = null;
					}
				}
			}
		}
		return objectName;
	}
	
	public static String useofMethod(String sm, String mName){
		String objectName = null;
		mName = mName +"(";
		if(sm.indexOf(mName) >0&& ifWMsequencewords(sm, ".", mName)){
			int index = sm.indexOf(mName)-1;
			int endIndex = 0;
			while(index >=0 &&(sm.substring(index, index+1).equals(" ")|| sm.substring(index, index+1).equals("."))){
				index --;
			}
			endIndex = index+1;
			index --;
			while(index >= 0 && (!sm.substring(index, index+1).equals(" "))&& (!sm.substring(index, index+1).equals("="))){//matches("^[a-zA-Z]*")){
				index --;
			}
			index ++;
			//System.out.println(index+" "+endIndex);
			objectName = sm.substring(index ,endIndex);
			index = sm.indexOf(mName)+ mName.length();
			
			/*if(paraSize >1){
				for(int i=0; i < paraSize; i++){				
					sm = sm.substring(index);
					if(sm.indexOf(",")<0){
						objectName = null;
					}		
					else
						index = sm.indexOf(",")+1;				
				}
			}
			else{
				if(paraSize == 1){
					sm = sm.substring(index);
					if(ifWMsequencewords(sm, "(",")")||sm.indexOf(",")>sm.indexOf(")")){
						objectName = null;
					}
				}
				if(paraSize == 0){
					sm = sm.substring(index);
					if(!ifWMsequencewords(sm, "(",")")){
						objectName = null;
					}
				}
			}*/
		}
		return objectName;
	}
	
	public static boolean methodListInclude(ArrayList<String> list, String mName){
		boolean tag  = false;
		if(list.size() >0){
			for(int i =0; i < list.size(); i++){
				//if(list.get(i).equals(mName))
				//由于调用方法的对象名字提取存在一些问题，用包含关系来确定对象的使用	
				if(mName.equals(list.get(i))){
				
					tag = true;
				//	System.out.println("compared name  "+mName+"    "+ list.get(i));	
				}
			}
		}
		
		return tag; 
	}
	public static String ifnewPara2(String sm, String cName ){
		String pName = null;		
		//boolean tag = false;
		int endIndex = 0;
		if(sm.indexOf(cName+" ")==0||sm.indexOf(" "+cName+" ")>=0){
			int beginIndex = sm.indexOf(cName)+cName.length();
			if(sm.indexOf("=")>0&& sm.indexOf("=")>beginIndex){
				endIndex = sm.indexOf("=");
				pName =deleteBlank(sm.substring(beginIndex, endIndex));
			}
			else{
				if(sm.indexOf(";")>0&& sm.indexOf(";")>beginIndex){
					endIndex = sm.indexOf(";");
				
					pName =deleteBlank(sm.substring(beginIndex, endIndex));
				}
				
			}
			
		}
		
		if(sm.indexOf(cName) >0){
			for(int i =0; i < sm.indexOf(cName); i++){
				if(!sm.substring(i,i+1).equals(" ")){
					
				    //i = sm.indexOf(cName);
				    cName = null;
				    break;
				}
			}
			
		}	
			
		return pName;
	}
	
	
	public static String ifnewPara(String sm, String cName ){
		String pName = null;
		
		//boolean tag = false;
		if(ifWMsequencewords(sm, "new", cName)){
			//tag = true;
			pName = new String();
			if(ifWMsequencewords(sm.substring(0, sm.indexOf(cName)), "=", "new")){
				
				int index = sm.substring(0, sm.indexOf(cName)).lastIndexOf("new")-1;
				int endIndex = 0;
				while(index >=0 &&(sm.substring(index, index+1).equals(" ")|| sm.substring(index, index+1).equals("="))){
					index --;
				}
				
				endIndex = index+1;
				index --;
				
				while(index >= 0 && sm.substring(index, index+1).matches("^[a-zA-Z]*")){
					index --;
				}
				index ++;
				pName = sm.substring(index ,endIndex);
				//System.out.println("--------"+pName+index +" "+endIndex);
			}
		}
		return pName;
	}
	public static String getVariable(String sm){
		String variable = new String(sm);
		if(sm.indexOf("=")>0)
			variable = sm.substring(0, sm.indexOf("="));
		
		return variable;
	}
	
	public static int ifIncludeAttributeList(ArrayList<String> list, String sm){
		int tag  = -1;
		int index =0;
		if(list.size() >0){
			for(int i =0; i < list.size(); i++){
				index = sm.indexOf(list.get(i));
				if(index>=0&&
						(index+1+list.get(i).length() == sm.length()||!sm.substring(index+list.get(i).length(),index+list.get(i).length()+1).matches("^[a-zA-Z]*")&&!sm.substring(index+list.get(i).length(),index+list.get(i).length()+1).equals("\""))
						&&(index == 0||!sm.substring(index-1,index).matches("^[a-zA-Z]*")||sm.substring(index-1,index).equals("\""))
						&& sm.indexOf("System.out.")<0){
					tag = i;
				//	System.out.println("compared name  "+sm+"    "+ list.get(i));	
				}
			}
		}
		
		return tag; 
	}
	public static void main(String[] args) {
	//	System.out.println(CodeMatcher.ifWMsequencewords("Attribute attribute = ( Attribute) obj;", "(", "Attribute"));
	//System.out.println(CodeMatcher.ifChangeoftype("ContentFilter filter=(ContentFilter)obj", "Filter"));
	//System.out.println(CodeMatcher.ifWMsequencewords("ContentFilter filter=(ContentFilter)obj;", "(", "Filter"));
	ArrayList<String> a = new ArrayList();
	a.add("a");
	//.a.add("ad");
	a.add("obj");
	System.out.println(CodeMatcher.ifIncludeAttributeList(a,"ContentFilter filter=(ContentFilter)obj\"(p,ad);"));
	//	System.out.println(CodeMatcher.ifWMsequencewords("atts.addAttribute(a.getNamespaceURI(),a.getName(),a.getQualifiedName(),getAttributeTypeName(a.getAttributeType()),a.getValue()); ", ".", "addAttribute"));
	//System.out.println("---"+CodeMatcher.ifWMsequencewords("rg.w3c.dom.CDATASection domCdata=domDoc.createCDATASection(cdata.getText());", ".","getText("));
	//System.out.println("---"+CodeMatcher.useofMethod("Attribute attribute=(Attribute)attributes.get(test.getname()); ", "get",1));
	//System.out.println("---"+CodeMatcher.useofMethod("Attribute attribute=(Attribute)()aaattributes.get(a,sh.get() ,a); ", "get",3));
	
	//	String a = "test";
	//System.out.println(CodeMatcher.ifnewPara2("ccc ssa new BC()", "ccc"));
	//	System.out.println(a);
	//	System.out.println(CodeMatcher.getVariable("f=new format"));
	//	System.out.println(CodeMatcher.ifWMinstanceof("ffff instanceof AC)", "AC"));
	}
}
