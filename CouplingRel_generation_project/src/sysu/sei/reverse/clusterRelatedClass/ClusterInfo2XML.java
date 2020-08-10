package sysu.sei.reverse.clusterRelatedClass;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import sysu.sei.reverse.clusterRelatedClass.XMLModel.Body;
import sysu.sei.reverse.clusterRelatedClass.XMLModel.Clusters;
import sysu.sei.reverse.clusterRelatedClass.XMLModel.DesignDecision;
import sysu.sei.reverse.clusterRelatedClass.XMLModel.Head;
import sysu.sei.reverse.clusterRelatedClass.util.MiwUtil;
import sysu.sei.reverse.clusterRelatedClass.XMLModel.ClassInfo;

public class ClusterInfo2XML {
	
	public String clusterInfo2XML(){
		Clusters clusters = new Clusters();
		Head head = new Head("1.21","1.22","JDom");
		Body body = new Body();		
		clusters.setHead(head);
		clusters.setBody(body);
		String xml = MiwUtil.genXML(clusters);
		return xml;
	}
	
	public void writeLog(String logFilePath,List <String> log){
		
		try {				
			File f = new File(logFilePath);			       
			if (f.exists()) {
		      //    System.out.println("文件存在");
		   //      TemporarySpace.setConsoleString("文件存在");			    				
			}			    
			else {
		          System.out.println("文件不存在，正在创建...");
		         if (f.createNewFile()) {
		       	   System.out.println("文件创建成功！");
		          }
		    	   else {
		            System.out.println("文件创建失败！");			    
		    	   }			     
			}
		//	 BufferedWriter output = new BufferedWriter(new FileWriter(f));
			BufferedWriter output =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFilePath, true)));  

		      for(int i =0; i < log.size(); i++){
	          
		    	  output.write(log.get(i));
		    	  output.write("\r\n");
		      }
	          output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	
	public static void main(String[] args){
		
		List<ClassInfo> lci1 = new ArrayList<ClassInfo>();
		lci1.add(new ClassInfo("Docment","org.jdom"));
		lci1.add(new ClassInfo("Node","org.jdom"));
		lci1.add(new ClassInfo("Attribute","org.jdom"));
		
		List<ClassInfo> lci2 = new ArrayList<ClassInfo>();
		lci2.add(new ClassInfo("Element","org.jdom"));
		lci2.add(new ClassInfo("Comment","org.jdom"));
		
		DesignDecision cm1 = new DesignDecision(lci1);
		DesignDecision cm2 = new DesignDecision(lci2);
		
		List<DesignDecision> lcm = new ArrayList<DesignDecision>();
		lcm.add(cm1);
		lcm.add(cm2);
		Clusters clusters = new Clusters();
		Head head = new Head("1.21","1.22","XStream");
		Body body = new Body(lcm);		
		clusters.setHead(head);
		clusters.setBody(body);
		String xml = MiwUtil.genXML(clusters);
		System.out.print(xml);
		List<String> li = new ArrayList<String>();
		li.add(xml);
		
		ClusterInfo2XML ci2xml = new ClusterInfo2XML();
		ci2xml.writeLog("G:\\xml.txt",li);
	}

}
