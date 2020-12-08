import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Main {

	public static void main(String[] args) throws IOException {
		String root_path = "E:\\ImpactAnalysis\\data4";
		traverseFolder2(root_path);
//		
//		List<String> projectList = Arrays.asList("freecol", "hsqldb", "jamwiki", "jedit", "jhotdraw", "makagiga", "omegat");
//
//		for(String project: projectList) {
//			String file_path = root_path + "\\" + project;
//			System.out.println(file_path);
//		processIOC("E:\\ImpactAnalysis\\data\\cas\\1107\\new\\DuoMultifactorWebflowConfigurer.java");
		
		return;
	}
	
	public static void traverseFolder2(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder2(file2.getAbsolutePath());
                    } else {
                        if(file2.getName().endsWith(".java")) {
                        	System.out.println(file2.getAbsolutePath());
                        	processIOC(file2.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println("File doesn't exist.");
        }
    }
		
	public static void appendToFile(String str)
	{
		String file = "./result.txt";
		BufferedWriter out = null ;  
        try  {  
            out = new  BufferedWriter( new  OutputStreamWriter(  
                    new  FileOutputStream(file,  true )));  
            out.write(str);  
        } catch  (Exception e) {  
            e.printStackTrace();
        } finally  {  
            try  {  
                out.close();  
            } catch  (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
	
	private static void processIOC(String file_path) throws IOException {
		File file_to_parse= new File(file_path);
		CompilationUnit cu_temp;
		
		try {
			cu_temp = StaticJavaParser.parse(file_to_parse);
		} catch (Exception e) {
			System.out.println("Error.\n");
			return;
		}
		
		List<String> names = new ArrayList();
		IOCExtraction iocextraction = new IOCExtraction();
		iocextraction.visit(cu_temp, names);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			System.out.println(name);
			appendToFile(file_path+":"+name+"\n");
			break;
		}
		return;
	}
}

