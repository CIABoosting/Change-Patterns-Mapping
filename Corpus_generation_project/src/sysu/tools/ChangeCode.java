package sysu.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.FileUtils;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;


/**
 *	提取变化代码 
 */
public class ChangeCode {

	 //变化后代码片段
	 private List<String> newChangecode = new ArrayList<>();
	 //变化前代码片段
	 private List<String> oldChangecode = new ArrayList<>();

	

	public List<String> getNewChangecode() {
		return newChangecode;
	}

	public void setNewChangecode(List<String> newChangecode) {
		this.newChangecode = newChangecode;
	}

	public List<String> getOldChangecode() {
		return oldChangecode;
	}

	public void setOldChangecode(List<String> oldChangecode) {
		this.oldChangecode = oldChangecode;
	}

	/**
	 *	oldPath：变化前类文件路径
	 *	newPath:变化后类文件路径
	 */
	public  ChangeCode(String oldPath, String newPath) {
	
		
		File oldfile = new File(oldPath);
		File newfile = new File(newPath);
		
		List<String> oldlines = null;
		List<String> newlines = null;
		
		try {
		    oldlines = org.apache.commons.io.FileUtils.readLines(oldfile,"UTF-8");
			newlines = org.apache.commons.io.FileUtils.readLines(newfile,"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(oldfile, newfile);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.out.println("Warning: error while change distilling. " + e.getMessage());
		    System.out.println(oldPath);
		    System.out.println(newPath);
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		    	
		    	//�仯�����λ��
		    	int oldStart = -1,oldEnd = -1,newStart = -1,newEnd = -1;
		    	
		    	//�仯���������
		    	int newStartLine = 0, newEndLine = 0,oldStartLine = 0,oldEndLine=0;
		    	
		    	//���������ģ���ֻ��¼��������
		    	if (change instanceof Insert) {
		    		Insert insert = (Insert)change;
		    		newStart = insert.getChangedEntity().getStartPosition();
		    		newEnd = insert.getChangedEntity().getEndPosition();
		    		newStartLine = getLineNumber(newfile, newStart);
		    		newEndLine = getLineNumber(newfile, newEnd);
		    		
		    	} 
		    	//���Ǹı�ģ������ļ��;��ļ��ı仯������ж�Ҫ��¼
		    	else if (change instanceof Update){
		    		Update update = (Update)change;
		    		newStart = update.getNewEntity().getStartPosition();
		    		newEnd = update.getNewEntity().getEndPosition();
		    		newStartLine = getLineNumber(newfile, newStart);
		    		newEndLine = getLineNumber(newfile, newEnd);
		    		
		    		oldStart = update.getChangedEntity().getStartPosition();
		    		oldEnd = update.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(oldfile,oldStart);
		    		oldEndLine = getLineNumber(oldfile,oldEnd);
		    	
		    	}
		    	//����ɾ���ģ���ֻ��¼���ļ�����
		    	else if(change instanceof Delete){
		    		Delete delete = (Delete)change;
		    		oldStart = delete.getChangedEntity().getStartPosition();
		    		oldEnd = delete.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(oldfile,oldStart);
		    		oldEndLine = getLineNumber(oldfile,oldEnd);
		    	}
		    	//�����ƶ��ģ������ļ��;��ļ��ı仯������ж�Ҫ��¼
		    	else if( change instanceof Move){
		    		Move move = (Move)change;
		    		newStart = move.getNewEntity().getStartPosition();
		    		newEnd = move.getNewEntity().getEndPosition();
		    		newStartLine = getLineNumber(newfile, newStart);
		    		newEndLine = getLineNumber(newfile, newEnd);
		    		
		    		oldStart = move.getChangedEntity().getStartPosition();
		    		oldEnd = move.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(oldfile,oldStart);
		    		oldEndLine = getLineNumber(oldfile,oldEnd);
		    	} else {
		    		System.out.println("Error no type");
		    	}
		    	
		    	
		    	if (newStartLine == 0 && newEndLine == 0)
					;
		    	else{
		    		for (int i = newStartLine; i <= newEndLine; i++) {
			    		try {
			    			//System.out.println(newlines.get(i - 1));
			    			newChangecode.add(newlines.get(i - 1));
			    		} catch (IndexOutOfBoundsException e) {
			    			
			    			// 处理数组越界，如果数组越界 直接continue，对程序影响不大
			    			/*
			    			 * System.out.println(bean.getClassName()); System.out.println("startline: " +
			    			 * startline); System.out.println("endline: " + endLine);
			    			 */
			    		}
			    	}
		    	}
		    	
		    	
		    	
		    	if (oldStartLine == 0 && oldEndLine == 0)
					;
		    	else{
		    		for (int i = oldStartLine; i <= oldEndLine; i++) {
			    		try {
			    			//System.out.println(oldlines.get(i - 1));
			    			oldChangecode.add(oldlines.get(i - 1));
			    		} catch (IndexOutOfBoundsException e) {
			    			
			    			// 处理数组越界，如果数组越界 直接continue，对程序影响不大
			    			/*
			    			 * System.out.println(bean.getClassName()); System.out.println("startline: " +
			    			 * startline); System.out.println("endline: " + endLine);
			    			 */
			    		}
			    	}
		    	}
		    		
		    }
		}

	}
	
	public  int getLineNumber(File file, int position) {
		int lineNum = 1;
		String fileContent;
		char[] charArray;
		try {
			fileContent = FileUtils.getContent(file);
			
			charArray = fileContent.toCharArray();
			for(int i=0; i<position&&i<charArray.length; i++){
				if(charArray[i]=='\n'){
					lineNum++;
				}
			}		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lineNum;
	}
	
	public static void main(String[] args) {
//		ChangeCode dChangeCode = new ChangeCode("E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2147\\old\\src\\jEdit.java", 
//				"E:\\ImpactAnalysis\\实验\\jedit\\commit\\3.2\\2147\\new\\src\\jEdit.java");
		ChangeCode dChangeCode = new ChangeCode("E:\\ImpactAnalysis\\data3\\dcm4che\\312\\old\\WadoImage.java",
				"E:\\ImpactAnalysis\\data3\\dcm4che\\312\\new\\WadoImage.java");
		
		int newChangeCount = dChangeCode.getNewChangecode().size();
		int oldChangeCount = dChangeCode.getOldChangecode().size();
		
		for(int i=0; i<newChangeCount; i++) {
			System.out.println(dChangeCode.getNewChangecode().get(i));
		}
		
		System.out.println("=================================================================================================================");
		
		for(int i=0; i<oldChangeCount; i++) {
			System.out.println(dChangeCode.getOldChangecode().get(i));
		}
	}
	
}
