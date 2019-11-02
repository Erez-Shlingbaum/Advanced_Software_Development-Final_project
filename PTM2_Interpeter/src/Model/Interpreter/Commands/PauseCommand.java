package Model.Interpreter.Commands;

import java.util.Scanner;

public class PauseCommand implements Command
{
    @Override
    public void execute(String[] args) throws Exception
    {
        System.out.println("Press \"ENTER\" start script...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
