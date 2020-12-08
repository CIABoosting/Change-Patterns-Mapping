import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VisitVariableDeclaration extends VoidVisitorAdapter<List<String>>{
	VisitVariableDeclaration() {
		super();
	}

	@Override
	public void visit(VariableDeclarationExpr vde, List<String> variableDeclaration) {
		super.visit(vde, variableDeclaration);
		variableDeclaration.add(vde.toString());
	}
}
