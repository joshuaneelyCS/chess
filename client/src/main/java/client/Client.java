package client;

public interface Client {
    public String help();
    public String eval(String line);
    public State getState();
    public void setState(State state);
}
