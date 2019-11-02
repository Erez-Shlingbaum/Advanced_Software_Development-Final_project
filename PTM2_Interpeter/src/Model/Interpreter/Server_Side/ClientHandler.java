package Model.Interpreter.Server_Side;

import Model.Interpreter.Interpeter.InterpreterContext;

import java.io.InputStream;
import java.io.OutputStream;

interface ClientHandler
{
    void handleClient(InputStream in, OutputStream out, InterpreterContext context);
}
