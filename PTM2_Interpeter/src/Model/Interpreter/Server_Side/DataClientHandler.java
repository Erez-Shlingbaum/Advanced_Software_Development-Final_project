package Model.Interpreter.Server_Side;

import Model.Interpreter.Interpeter.InterpreterContext;
import Model.Interpreter.Interpeter.Variable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataClientHandler implements ClientHandler
{
    public static volatile boolean isStop = false;
    private final int linesPerSecond;

    public DataClientHandler(int linesPerSecond)
    {
        this.linesPerSecond = linesPerSecond;
    }

    // outClient is not used
    @Override
    public void handleClient(InputStream in, OutputStream out, InterpreterContext context) // Handle conversation
    {
        System.out.println("Data server is running...");
        BufferedReader clientInput = new BufferedReader(new InputStreamReader(in));
        String str;
        List<String> simNames = new ArrayList<>(26);

        try
        {
            // Create a list of simulator bind paths from file (the order of the paths is important)
            Scanner simulatorBinds = new Scanner(new File("./PTM2_Interpeter/Resources/simulator_variables_paths.txt"));
            while (simulatorBinds.hasNextLine())
                simNames.add(simulatorBinds.nextLine());
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        try
        {
            while ((str = clientInput.readLine()) != null && !isStop) // This reads 10 lines per second, unless the server sends in a different rate
            {
                String[] simsXYZ = str.split(",");
                Variable[] variables = context.symbolTable.values().toArray(new Variable[0]);

                for (int i = 0; i < simNames.size(); i++)
                    for (Variable variable : variables)
                        if (simNames.get(i).equals(variable.getPath()))
                            try
                            {
                                // System.out.println("DataServer: updating var '" + variables[j].getPath() + "' with " + simsXYZ[i]);
                                variable.setValue(Double.parseDouble(simsXYZ[i]), false);
                                break;
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
            }

            clientInput.close();
            out.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
