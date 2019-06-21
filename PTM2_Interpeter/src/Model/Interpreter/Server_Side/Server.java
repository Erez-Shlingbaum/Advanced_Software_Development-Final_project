package Model.Interpreter.Server_Side;

interface Server
{
	void open(int port, ClientHandler clientHandler);
	void close();
}
