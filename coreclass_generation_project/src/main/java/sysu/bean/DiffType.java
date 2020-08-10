package sysu.bean;


import java.util.ArrayList;
import java.util.List;


public class DiffType {
	
	private String type;
	

	private int newStartLine;
	

	private int newEndLine;
	

	private int oldStartLine;
	

	private int oldEndLine;
	

	private List<Long> newHashList = new ArrayList<Long>();
	

	private List<Long> oldHashList = new ArrayList<Long>();

	private List<String> newKeywordList = new ArrayList<String>();
	

	private List<String> oldKeywordList = new ArrayList<String>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getOldStartLine() {
		return oldStartLine;
	}
	public void setOldStartLine(int oldStartLine) {
		this.oldStartLine = oldStartLine;
	}
	public int getOldEndLine() {
		return oldEndLine;
	}
	public void setOldEndLine(int oldEndLine) {
		this.oldEndLine = oldEndLine;
	}
	public int getNewStartLine() {
		return newStartLine;
	}
	public void setNewStartLine(int newStartLine) {
		this.newStartLine = newStartLine;
	}
	public int getNewEndLine() {
		return newEndLine;
	}
	public void setNewEndLine(int newEndLine) {
		this.newEndLine = newEndLine;
	}
	public List<Long> getNewHashList() {
		return newHashList;
	}
	public void setNewHashList(List<Long> newHashList) {
		this.newHashList = newHashList;
	}
	public List<Long> getOldHashList() {
		return oldHashList;
	}
	public void setOldHashList(List<Long> oldHashList) {
		this.oldHashList = oldHashList;
	}
	public void addNewHash(long newHash){
		newHashList.add(newHash);
	}
	public void addOldHash(long oldHash){
		oldHashList.add(oldHash);
	}
	public List<String> getNewKeywordList() {
		return newKeywordList;
	}
	public void setNewKeywordList(List<String> newKeywordList) {
		this.newKeywordList = newKeywordList;
	}
	public List<String> getOldKeywordList() {
		return oldKeywordList;
	}
	public void setOldKeywordList(List<String> oldKeywordList) {
		this.oldKeywordList = oldKeywordList;
	}
	public void addNewKeyword(String keyword){
		newKeywordList.add(keyword);
	}
	public void addOldKeyword(String keyword){
		oldKeywordList.add(keyword);
	}
	

}
