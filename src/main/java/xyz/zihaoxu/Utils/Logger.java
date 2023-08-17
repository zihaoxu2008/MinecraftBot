package xyz.zihaoxu.Utils;

public class Logger {
    private String name;
    public Logger(String name){
        this.name=name;
    }
    public void info(String message){
        System.out.println("["+this.name+"/Info] "+message);
    }
    public void warn(String message){
        System.out.println("["+this.name+"/Warn] "+message);
    }
    public void error(String message){
        System.out.println("["+this.name+"/Error] "+message);
    }
}
