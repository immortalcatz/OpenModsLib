package openmods.calc;

public abstract class Operator<E> implements IExecutable<E> {

	protected final String id;

	public Operator(String id) {
		this.id = id;
	}

	public abstract boolean isLessThan(Operator<E> other);

	@Override
	public String serialize() {
		return id;
	}

}
