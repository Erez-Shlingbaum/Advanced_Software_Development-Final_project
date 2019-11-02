package Model.Interpreter.Expressions;

public class Number implements Expression
{
    private final double value;

    public Number(double value)
    {
        this.value = value;
    }

    @Override
    public double calculate()
    {
        return value;
    }

}
