import java.io.IOException;

public class Test
{
    protected boolean broken;
    protected Mutator originalMutator;
    protected int index;

    public Test() { }

    public void longField(Object bean, long value) throws IOException
    {
        if (broken) {
            originalMutator.set(bean, value);
            return;
        }
        try {
            localSet(bean, index, value);
	    return;
        } catch (IllegalAccessError e) {
            _reportProblem(bean, value, e);
	    return;
        } catch (SecurityException e) {
            _reportProblem(bean, value, e);
	    return;
        }
    }

    protected void _reportProblem(Object bean, Object value, Throwable t) { }
    protected void localSet(Object bean, int index, long value) { }
}

class Mutator {
    public void set(Object bean, Object value) { }
}
