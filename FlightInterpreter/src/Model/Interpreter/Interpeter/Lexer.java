package Model.Interpreter.Interpeter;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Lexer
{
    // Seperate line into tokens
    public static String[] lexString(String line)
    {
        List<String> stringList = new LinkedList<>();
        try (Scanner scanner = new Scanner(line))
        {
            while (scanner.hasNext())
                stringList.add(scanner.next());
        }

        return stringList.toArray(new String[0]);
    }
}
