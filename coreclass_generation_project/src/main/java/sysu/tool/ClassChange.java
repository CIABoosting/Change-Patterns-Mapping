package sysu.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import sysu.bean.Line;
import sysu.tool.CodeComparator;


public class ClassChange {

	private int id;
	private ArrayList<Integer[]> relationship;
	private List<ChangeStatement> statementList = new ArrayList<ChangeStatement>();

	private int newCodeLines;
	private int oldCodeLines;

	private int newMethodNum;
	private int oldMethodNum;

	private List<Line> codes;

	private TypeDeclaration oldClass;
	private TypeDeclaration newClass;
	private String oldPackageName;
	private String newPackageName;
	private List<ImportDeclaration> oldImportList;
	private List<ImportDeclaration> newImportList;

	//private List a;

	private List<MethodDeclaration> addedMethods;
	private List<MethodChange> changedMethods;
	private List<MethodDeclaration> deletedMethods;
	private List<FieldDeclaration> addedFields;

	private List<FieldDeclaration> deletedFields;

	public ClassChange() {

	}

	public ClassChange(CompilationUnit oldUnit, CompilationUnit newUnit, TypeDeclaration oldClass,
			TypeDeclaration newClass, String oldPackageName, String newPackageName,
			List<ImportDeclaration> oldImportList, List<ImportDeclaration> newImportList, String oldFilePath,
			String newFilePath) {
		this.oldPackageName = oldPackageName;
		this.newPackageName = newPackageName;
		this.oldImportList = oldImportList;
		this.newImportList = newImportList;

		this.oldClass = oldClass;
		this.newClass = newClass;

		if (newClass != null) {
			this.newCodeLines = newUnit.getLineNumber(newClass.getStartPosition() + newClass.getLength() - 1)
					- newUnit.getLineNumber(newClass.getStartPosition()) + 1;
			this.newMethodNum = newClass.getMethods().length;
		}

		if (oldClass != null) {
			this.oldCodeLines = oldUnit.getLineNumber(oldClass.getStartPosition() + oldClass.getLength() - 1)
					- oldUnit.getLineNumber(oldClass.getStartPosition()) + 1;
			this.oldMethodNum = oldClass.getMethods().length;
		}

		if(oldFilePath==null) {
			oldFilePath ="";
		}
		if(newFilePath == null) {
			newFilePath = "";
		}
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);

		if (oldFile.exists() && newFile.exists()) {
			try {
//				System.out.println("compare "+oldFile.getAbsolutePath()+" "+newFile.getAbsolutePath());
				this.codes = CodeComparator.compare(FileUtils.readLines(oldFile, "UTF-8"),
						FileUtils.readLines(newFile, "UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (oldFile.exists()) {
			List<Line> codes = new ArrayList<Line>();
			List<String> oldCodes = new ArrayList<String>();
			try {
				oldCodes = FileUtils.readLines(oldFile, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < oldCodes.size(); i++) {
				Line line = new Line();
				line.setLine(oldCodes.get(i));
				line.setLineNumber(i + 1);
				line.setOffset(0);
				line.setType("delete");
				codes.add(line);
			}
			this.codes = codes;
		} else if (newFile.exists()) {
			List<Line> codes = new ArrayList<Line>();
			List<String> newCodes = new ArrayList<String>();
			try {
				newCodes = FileUtils.readLines(newFile, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < newCodes.size(); i++) {
				Line line = new Line();
				line.setLine(newCodes.get(i));
				line.setLineNumber(i + 1);
				line.setOffset(0);
				line.setType("add");
				codes.add(line);
			}
			this.codes = codes;
		}

	}

	public List<ChangeStatement> getStatementList() {
		return statementList;
	}

	public void setStatementList(List<ChangeStatement> statementList) {
		this.statementList = statementList;
	}

	public void addStatement(ChangeStatement sta) {
		statementList.add(sta);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Integer[]> getRelationship() {

		return relationship;
	}

	public void setRelationship(ArrayList<Integer[]> relationship) {
		this.relationship = relationship;
	}

	public boolean addRevisionRelationship(int classId, int type) {
		boolean tag = false;
		if (relationship != null && relationship.size() > classId) {
			tag = true;
			if (relationship.get(classId) == null) {
				relationship.set(classId, new Integer[100]);
			}
			if (relationship.get(classId)[type] == null) {
				relationship.get(classId)[type] = new Integer(0);
			}
			relationship.get(classId)[type]++;

		}

		return tag;
	}

	public boolean deleteRevisionRelationship(int classId, int type) {
		boolean tag = false;
		if (relationship != null && relationship.size() > classId) {
			tag = true;
			if (relationship.get(classId) == null) {
				relationship.set(classId, new Integer[200]);
			}
			if (relationship.get(classId)[type] == null) {
				relationship.get(classId)[type] = new Integer(0);
			}
			relationship.get(classId)[type]--;

		}

		return tag;
	}

	public boolean ifRelated(int classId) {
		boolean tag = false;
		if (relationship != null && relationship.size() >= classId) {
			if (relationship.get(classId) != null) {
				for (int i = 0; i < 100; i++) {
					if (relationship.get(classId)[i] != null && relationship.get(classId)[i].intValue() != 0) {
						tag = true;
					}
				}
			}
		}
		return tag;
	}

	public void initializeRelationship(int classId) {

		if (relationship != null && relationship.size() >= classId) {
			if (classId < relationship.size() && relationship.get(classId) != null) {
				for (int i = 0; i < 100; i++) {
					relationship.get(classId)[i] = new Integer(0);

				}

			}
		}
	}

	public void initRelationship(int size) {
		if (relationship == null) {
			relationship = new ArrayList<Integer[]>();
			for (int i = 0; i < size; i++) {
				relationship.set(i, new Integer[100]);
			}
		}
	}

	public List<ImportDeclaration> getOldImportList() {
		return oldImportList;
	}

	public void setOldImportList(List<ImportDeclaration> oldImportList) {
		this.oldImportList = oldImportList;
	}

	public List<ImportDeclaration> getNewImportList() {
		return newImportList;
	}

	public void setNewImportList(List<ImportDeclaration> newImportList) {
		this.newImportList = newImportList;
	}

	public String getOldPackageName() {
		return oldPackageName;
	}

	public void setOldPackageName(String oldPackageName) {
		this.oldPackageName = oldPackageName;
	}

	public String getNewPackageName() {
		return newPackageName;
	}

	public void setNewPackageName(String newPackageName) {
		this.newPackageName = newPackageName;
	}

	public TypeDeclaration getOldClass() {
		return oldClass;
	}

	public void setOldClass(TypeDeclaration oldClass) {
		this.oldClass = oldClass;
	}

	public TypeDeclaration getNewClass() {
		return newClass;
	}

	public void setNewClass(TypeDeclaration newClass) {
		this.newClass = newClass;
	}

	public List<MethodDeclaration> getAddedMethods() {
		if (addedMethods == null) {
			addedMethods = new ArrayList<MethodDeclaration>();
		}
		return addedMethods;
	}

	public void setAddedMethods(List<MethodDeclaration> addedMethods) {

		this.addedMethods = addedMethods;
	}

	public void addAddedMethod(MethodDeclaration method) {

		this.getAddedMethods().add(method);
	}

	public List<MethodChange> getChangedMethods() {
		if (changedMethods == null) {
			changedMethods = new ArrayList<MethodChange>();
		}
		return changedMethods;
	}

	public void setChangedMethods(List<MethodChange> changedMethods) {
		this.changedMethods = changedMethods;
	}

	public void addChangedMethod(MethodChange method){

		this.getChangedMethods().add(method);
	}

	public List<MethodDeclaration> getDeletedMethods() {
		if (deletedMethods == null) {
			deletedMethods = new ArrayList<MethodDeclaration>();
		}
		return deletedMethods;
	}

	public void setDeletedMethods(List<MethodDeclaration> deletedMethods) {

		this.deletedMethods = deletedMethods;
	}

	public void addDeletedMethod(MethodDeclaration method) {

		this.getDeletedMethods().add(method);
	}

	public List<FieldDeclaration> getAddedFields() {
		if (addedFields == null) {
			addedFields = new ArrayList<FieldDeclaration>();
		}
		return addedFields;
	}

	public void setAddedFields(List<FieldDeclaration> addedFields) {

		this.addedFields = addedFields;
	}

	public void addAddedField(FieldDeclaration filed) {

		this.getAddedFields().add(filed);
	}

	public List<FieldDeclaration> getDeletedFields() {
		if (deletedFields == null) {
			deletedFields = new ArrayList<FieldDeclaration>();
		}
		return deletedFields;
	}

	public void setDeletedFields(List<FieldDeclaration> deletedFields) {

		this.deletedFields = deletedFields;
	}

	public void addDeletedField(FieldDeclaration filed) {

		this.getDeletedFields().add(filed);
	}
	
	public int getNewCodeLines() {
		return newCodeLines;
	}

	public void setNewCodeLines(int newCodeLines) {
		this.newCodeLines = newCodeLines;
	}

	public int getOldCodeLines() {
		return oldCodeLines;
	}

	public void setOldCodeLines(int oldCodeLines) {
		this.oldCodeLines = oldCodeLines;
	}

	public int getNewMethodNum() {
		return newMethodNum;
	}

	public void setNewMethodNum(int newMethodNum) {
		this.newMethodNum = newMethodNum;
	}

	public int getOldMethodNum() {
		return oldMethodNum;
	}

	public void setOldMethodNum(int oldMethodNum) {
		this.oldMethodNum = oldMethodNum;
	}

	public List<Line> getCodes() {
		return codes;
	}

	public void setCodes(List<Line> codes) {
		this.codes = codes;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}