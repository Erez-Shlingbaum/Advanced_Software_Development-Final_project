package Commands;

import java.util.List;

public class WhileCommand extends MultiCommand
{
    @Override
    public void execute(String[] args) throws Exception
    {
        //args is predicates and toExecute are commands we need to execute in a while
        System.out.println("While worked");
        for (String str : args)
            System.out.println(str);
    }
}
