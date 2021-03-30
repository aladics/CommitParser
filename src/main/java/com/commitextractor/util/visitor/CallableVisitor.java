package com.commitextractor.util.visitor;

import java.util.List;
import java.util.Set;

import com.commitextractor.model.MethodNode;
import com.commitextractor.model.Range;
import com.github.javaparser.Position;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public abstract class CallableVisitor extends VoidVisitorAdapter<Void>{
	protected List<Range> diffs;
	protected Set<MethodNode> methods;
	
	public CallableVisitor(List<Range> diffs, Set<MethodNode> methods) {
		super();
		this.diffs = diffs;
		this.methods = methods;
	}
	
	protected void addMethod(CallableDeclaration<?> n) {
		Position beginPos = n.getBegin().get();
		Position endPos = n.getEnd().get();
		methods.add(new MethodNode(n.getNameAsString(), beginPos.line, beginPos.column, endPos.line, endPos.column));
	}
	
	public abstract void visitCallable(CallableDeclaration<?> n, Void arg);
	

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		super.visit(n, arg);
		visitCallable(n, arg);
	}

	@Override
	public void visit(ConstructorDeclaration n, Void arg) {
		super.visit(n, arg);
		visitCallable(n, arg);
	}

	public Set<MethodNode> getMethods() {
		return methods;
	}
	
	
}