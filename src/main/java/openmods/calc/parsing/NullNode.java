package openmods.calc.parsing;

import java.util.List;

import openmods.calc.IExecutable;

import com.google.common.base.Preconditions;

public class NullNode<E> implements IInnerNode<E> {

	private IExprNode<E> child;

	@Override
	public void addChild(IExprNode<E> child) {
		Preconditions.checkState(child == null, "More then one child in non-function parentheses");
		this.child = child;
	}

	@Override
	public void flatten(List<IExecutable<E>> output) {
		Preconditions.checkState(child != null, "Empty parentheses");
		if (child != null) child.flatten(output);
	}

	@Override
	public String toString() {
		return "<" + child + ">";
	}
}
