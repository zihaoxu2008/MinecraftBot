package xyz.zihaoxu;

import xyz.zihaoxu.Bots.Bot;
import xyz.zihaoxu.Utils.BasicConfig;
import xyz.zihaoxu.Utils.CommandLineParser;
import xyz.zihaoxu.Utils.Logger;
import xyz.zihaoxu.script.ScriptManager;

import javax.script.ScriptException;
import java.io.IOException;

public class Main {
    public static BasicConfig basicConfig;
    public static ScriptManager scriptManager;
    public static void main(String[] args) throws IOException, ScriptException {
        CommandLineParser parser=new CommandLineParser(args);
        basicConfig=parser.parse();
        Logger logger=new Logger("Main");
        logger.info("正在加载脚本...");
        scriptManager=new ScriptManager();
        scriptManager.init();
        scriptManager.loadFolder(ScriptManager.dir);
        logger.info("正在启动...");
        Bot mcbot=new Bot();
        mcbot.run();
    }
}