package Model.Interpreter.Server_Side;

import Model.Interpreter.Interpeter.InterpreterContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DataServer implements Server
{
    private ServerSocket serverSocket = null;

    @Override
    public void open(int port, ClientHandler clientHandler, InterpreterContext context)
    {
        new Thread(() -> runServer(port, clientHandler, context)).start();
    }

    private void runServer(int port, ClientHandler clientHandler, InterpreterContext context)
    {

        try
        {
            //open server
            serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();

            //conversation

            clientHandler.handleClient(clientSocket.getInputStream(), clientSocket.getOutputStream(), context);

            //close server
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void close()
    {
        DataClientHandler.isStop = true; // Stop the thread from running
    }

    @Override
    public boolean isConnected()
    {
        return serverSocket != null;
    }
}


