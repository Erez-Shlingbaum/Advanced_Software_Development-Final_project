package Model.Interpreter.Commands;

import Model.Interpreter.Interpeter.InterpreterContext;

import java.util.Scanner;

public class PauseCommand implements Command
{
    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        System.out.println("Press \"ENTER\" start script...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
