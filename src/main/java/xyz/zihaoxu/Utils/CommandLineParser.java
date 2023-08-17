package xyz.zihaoxu.Utils;

import org.apache.commons.cli.*;

public class CommandLineParser {
    private final String[] CommandLineArguments;
    public CommandLineParser(String[] args){
        this.CommandLineArguments=args;
    }

    public BasicConfig parse() {
        BasicConfig ret=new BasicConfig();
        Options options=new Options();
        options.addOption("s","server",true,"The server to connect");
        options.addOption("u","user",true,"The name of robot");
        options.addOption("p","permission-file",true,"The file of saved permissions");
        options.addOption("c","configure-file",true,"The configure file");
        DefaultParser parser=new DefaultParser();
        try{
            CommandLine commandLine=parser.parse(options,CommandLineArguments);
            if (commandLine.hasOption('s')){
                ret.serverAddress= commandLine.getOptionValue('s');
            }
            if (commandLine.hasOption('u')){
                ret.userName= commandLine.getOptionValue('u');
            }
            if (commandLine.hasOption('p')){
                ret.permissionFile= commandLine.getOptionValue('p');
            }
            if (commandLine.hasOption('c')){
                ret.configureFile= commandLine.getOptionValue('c');
            }
        } catch (ParseException e) {
            HelpFormatter helpFormatter=new HelpFormatter();
            helpFormatter.printHelp("bot",e.getMessage(),options,"",true);
            System.exit(-1);
        }
        return ret;
    }
}
