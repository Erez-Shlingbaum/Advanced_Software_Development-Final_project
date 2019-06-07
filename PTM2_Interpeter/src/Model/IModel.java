package Model;

public interface IModel
{
	void interpretScript(String[] lines);

	// can be used to add variables and execute commands, etc...
	void executeCommand(String cmdName, String[] args);

	void calculatePath(String ip, int port, double[][] heightsInMeters, int[] startCoordinate, int[] endCoordinate);
	String getCalculatedPath();
}