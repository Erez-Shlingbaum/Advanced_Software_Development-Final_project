package Client_Side;

import Commands.ConnectCommand;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectClient implements Client
{
    Socket socket = null;

    static ConnectClient client = null;

    private ConnectClient()
    {
    }

    public static ConnectClient getReference()
    {
        if (client == null)
            client = new ConnectClient();
        return client;
    }

    public static boolean isReferenceExists()
    {
        if (client == null)
            return false;
        return true;
    }

    @Override
    public void connect(String ipAddress, int port) throws Exception
    {
        if (socket != null)
            throw new Exception("Syntax error: trying to connect twice");
        try
        {
            socket = new Socket(ipAddress, port);   //TODO: check if valid ip, else throw Exeption("syntax error..")
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String command) throws Exception
    {
        if (socket == null)
            throw new Exception("Trying to send message when connection is not open");
        try
        {
            PrintWriter writerToServer = new PrintWriter(socket.getOutputStream());
            writerToServer.println(command);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void close()
    {
        try
        {
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}