package models.helpers;

public class Pair<T1, T2> {
	public T1 left;
	public T2 right;
	
	public Pair(T1 a, T2 b) {
		left = a;
		right = b;
	}
}
