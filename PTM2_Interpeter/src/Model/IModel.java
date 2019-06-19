package Model;

public interface IModel
{
	void interpretScript(String filePath);

	void interpretScript(String... scriptLines);

	double getVarRetrivedFromScript();

	int getReturnValue();

	// can be used to add variables and execute commands, etc...
	void executeCommand(String cmdName, String... args); // Three dots '...' allow sending parameters as easy as "executeCommand("return", "1+", "2+", "3")" ...

	void retrieveVariableInScript(String variableName); // get variable from symbol table

	void calculatePath(String ip, String port, double[][] heightsInMeters, int[] startCoordinateIndex, int[] endCoordinateIndex);

	Boolean isConnectedToSimulator();

	String getSolutionForPathProblem();

	// Utilities
	void openCsvFile(String filePath);

	void sendJoystickState(double aileron, double elevator, double rudder, double throttle);

	double getxCoordinateLongitude();

	double getyCoordinateLatitude();

	double getCellSizeInDegrees();

	double[][] getCsvValues();
}