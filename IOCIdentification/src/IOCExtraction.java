import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class IOCExtraction extends VoidVisitorAdapter<List<String>> {
	IOCExtraction() {
		super();
	}

	@Override
	public void visit(MethodCallExpr methodCallExpr, List<String> names) {
		super.visit(methodCallExpr, names);
		if(methodCallExpr.getNameAsString().equals("getBean")) {
			if(methodCallExpr.getParentNode().get().toString().contains("applicationContext") && !methodCallExpr.getParentNode().get().toString().contains("return") ){
				List<String> variableDeclarationList = new ArrayList<String>();
				VisitVariableDeclaration vde= new VisitVariableDeclaration();
				methodCallExpr.getParentNode().get().getParentNode().get().accept(vde, variableDeclarationList);
				if(variableDeclarationList.size()>0)
					names.add(methodCallExpr.getParentNode().get().getChildNodes().get(0).toString());	
			}
		}
		
	}
	
}
