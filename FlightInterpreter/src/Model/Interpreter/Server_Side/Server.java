package Model.Interpreter.Server_Side;

import Model.Interpreter.Interpeter.InterpreterContext;

public interface Server
{
    void open(int port, ClientHandler clientHandler, InterpreterContext context);

    void close();

    boolean isConnected();
}
