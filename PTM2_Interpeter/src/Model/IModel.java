package Model;

public interface IModel
{
	public void interpretScript(String filePath);
	void interpretScript(String... scriptLines);
	int getReturnValue();
	// can be used to add variables and execute commands, etc...
	void executeCommand(String cmdName, String... args); // Three dots '...' allow sending parameters as easy as "executeCommand("return", "1+", "2+", "3")" ...

	void calculatePath(String ip, int port, double[][] heightsInMeters, int[] startCoordinate, int[] endCoordinate);
	String getCalculatedPath();
}