package Model.Interpreter.Client_Side;

import java.io.IOException;

public interface Client
{
    void connect(String ipAddress, int port) throws Exception;

    void sendMessage(String command) throws Exception;

    void close() throws IOException;

    boolean isConnected();
}
