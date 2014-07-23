package waffle.apache.catalina;

import org.apache.catalina.Container;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;

public class SimplePipeline implements Pipeline {

    private Valve[] _valves = new Valve[0];

    @Override
    public void addValve(Valve arg0) {

    }

    @Override
    public Valve getBasic() {
        return null;
    }

    @Override
    public Container getContainer() {
        return null;
    }

    @Override
    public Valve getFirst() {
        return null;
    }

    @Override
    public Valve[] getValves() {
        return _valves.clone();
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public void removeValve(Valve arg0) {

    }

    @Override
    public void setBasic(Valve arg0) {

    }

    @Override
    public void setContainer(Container arg0) {

    }
}
