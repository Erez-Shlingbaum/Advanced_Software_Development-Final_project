package Model.Interpreter.Client_Side;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectClient implements Client
{
    private Socket socket = null;

    private static boolean validIP(String ip)
    {
        try
        {
            if (ip == null || ip.isEmpty())
                return false;
            if (ip.equals("localhost"))
                return true;

            String[] parts = ip.split("\\.");
            if (parts.length != 4)
                return false;

            for (String s : parts)
            {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255))
                    return false;
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    @Override
    public void connect(String ipAddress, int port) throws Exception
    {
        if (socket != null)
            throw new Exception("Syntax error: trying to connect twice");
        if (!validIP(ipAddress))
            throw new Exception("Syntax error: ip address is not valid");

        boolean tryAgain = true;
        while (tryAgain)
            try
            {
                socket = new Socket(ipAddress, port);
                tryAgain = false;
                System.out.println("Connected to FlightGear...");
            } catch (IOException e)
            {
                System.out.println("Connecting to flightGear failed, trying again...");
                Thread.sleep(3000);
                //e.printStackTrace();
            }
    }

    @Override
    public boolean isConnected()
    {
        return this.socket != null && this.socket.isConnected();
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
            writerToServer.flush();
            //System.out.println("Command sent: " + command);
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