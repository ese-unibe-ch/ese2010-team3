package models.helpers;

public abstract class Filter<InputType> extends Mapper<InputType, InputType> {

	protected abstract boolean by(InputType i);

	@Override
	protected InputType visit(InputType i) {
		return by(i) ? i : null;
	}

}
