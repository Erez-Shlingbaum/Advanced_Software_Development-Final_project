package Commands;

public class AssignmentCommand implements Command
{
    @Override
    public void execute(String[] args) throws Exception //args will contain all of the line, including variable name
    {
        if(args.length < 3)
            throw new Exception("Syntax error: assignment operator should be of the form 'varName = expression' where expression can be a number or more complex");
        String varName = args[0];


        //TODO: cast Calculator.calc(string) from int to double
    }
}
