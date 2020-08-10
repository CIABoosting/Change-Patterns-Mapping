package com.troy.ui.model;

/**
 * 定义项目中每个版本的模型，项目中的每个版本看着一个文件夹
 * @author Troy
 *
 */
public class File4ProgramFile {
	private int id;
	private String fileName;
	private String absolutePath;
	private String size;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}


}
