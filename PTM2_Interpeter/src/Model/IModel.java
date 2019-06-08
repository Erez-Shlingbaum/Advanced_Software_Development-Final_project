package Model;

public interface IModel
{
	void interpretScript(String[] lines);
	int getReturnValue();
	// can be used to add variables and execute commands, etc...
	void executeCommand(String cmdName, String... args); // Three dots '...' allow sending parameters as easy as "executeCommand("return", "1+", "2+", "3")" ...

	void calculatePath(String ip, int port, double[][] heightsInMeters, int[] startCoordinate, int[] endCoordinate);
	String getCalculatedPath();
}