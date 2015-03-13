// author Johannes Flood 2015-03-13 12:40, did not receive any help from Hampus

public interface DriverAction{
	Object controller;
	int score = 50;
	Type type;

	public DriverActions(Object controller, Type type);
	private void subtractPoints(int points);
	private void addPoints(int points);
	public int getScore();

	public enum Type{
		BRAKING, SPEED, DISTRACTION, FEUL;
	}
}