package com.troy.ui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.troy.ui.model.File4ProgramFile;

public class ProgramFileTableModel extends AbstractTableModel{
	private static final long serialVersionUID =123457898776l;
	private List<File4ProgramFile> list = new ArrayList<File4ProgramFile>();
	
	public ProgramFileTableModel(){
		
	}
	
	public List<File4ProgramFile> getList() {
		return list;
	}


	public void setList(List<File4ProgramFile> list) {
		this.list = list;
	}


	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex<0||rowIndex>list.size()-1){
			return null;
		}
		if(columnIndex<0||columnIndex>5){
			return null;
		}
		File4ProgramFile file4WF=(File4ProgramFile)list.get(rowIndex);

		switch(columnIndex){	
		case 0:return String.valueOf(rowIndex+1);		
		case 1:return file4WF.getFileName();
		case 2:return file4WF.getAbsolutePath();
		case 3:return file4WF.getSize();

		default:return null;
			
		}
	}
	
	public String getColumnName(int col) {
		switch(col){		
		case 0:return "ID";
		case 1:return "FileName";
		case 2:return "FilePath";
		case 3:return "Size(kb)";
		default:return null;
		}
	}

}
