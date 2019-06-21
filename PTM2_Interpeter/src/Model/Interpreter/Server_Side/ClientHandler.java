package Model.Interpreter.Server_Side;

import java.io.InputStream;
import java.io.OutputStream;

interface ClientHandler
{
	void handleClient(InputStream in, OutputStream out);
}
