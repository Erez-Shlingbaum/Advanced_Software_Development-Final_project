package Model.Interpreter.Interpeter;

import Model.Interpreter.Client_Side.Client;
import Model.Interpreter.Commands.Command;
import Model.Interpreter.Server_Side.Server;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class InterpreterContext
{
    public final ConcurrentHashMap<String, Variable> symbolTable = new ConcurrentHashMap<>();
    public final HashMap<String, Command> keywords;
    public final Client client;
    public final Server server;

    public InterpreterContext(HashMap<String, Command> keywords, Client client, Server server)
    {
        this.keywords = keywords;
        this.client = client;
        this.server = server;
    }

    public void clearSymbolTable()
    {
        symbolTable.clear();
    }
}
