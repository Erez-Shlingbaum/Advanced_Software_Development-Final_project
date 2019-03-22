package interpeter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Lexer
{
	public static String[] lexer(String line)
	{
		List<String> stringList = new LinkedList<>();
		Scanner scanner = new Scanner(line);

		while (scanner.hasNext())
			stringList.add(scanner.next());

		return stringList.toArray(new String[stringList.size()]);
	}
}
