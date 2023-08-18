package xyz.zihaoxu.Bots;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import xyz.zihaoxu.Main;
import xyz.zihaoxu.Utils.Logger;
import xyz.zihaoxu.Utils.Vec3d;
import xyz.zihaoxu.script.ScriptManager;
import xyz.zihaoxu.script.obj.ScriptBot;

import javax.script.ScriptException;
import java.io.IOException;

public class Bot {
    private TcpClientSession client;
    private Listener listener;
    private Logger logger;
    public static int reconnectCount=0;
    private Thread updateThread;
    public Bot(){
        this.logger=new Logger("BotBootstrap");
        String[] tmp= Main.basicConfig.serverAddress.split(":");
        String host=tmp[0];
        int port=25565;
        if (tmp.length==2){
            port=Integer.parseInt(tmp[1]);
        }
        client=new TcpClientSession(host,port, new MinecraftProtocol(Main.basicConfig.userName));
        client.setConnectTimeout(10); // 5 sec
        listener=new Listener(this);
    }

    public void run(){
        client.addListener(listener);
        logger.info("正在启动机器人并连接到服务器 "+Main.basicConfig.serverAddress);
        client.connect(true);
        logger.info("正在启动更新线程");
        updateThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Main.scriptManager.call("onUpdate", new ScriptBot(client));
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("更新线程出现错误!onUpdate事件将不再调用");
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        updateThread.start();
    }

    public void reconnect() throws IOException, ScriptException, InterruptedException {
        updateThread.interrupt();
        if (++reconnectCount>3){
            logger.info("重连次数过多,程序退出");
            System.exit(0);
        }
        String[] tmp= Main.basicConfig.serverAddress.split(":");
        String host=tmp[0];
        int port=25565;
        if (tmp.length==2){
            port=Integer.parseInt(tmp[1]);
        }
        client=new TcpClientSession(host,port, new MinecraftProtocol(Main.basicConfig.userName));
        client.setConnectTimeout(10); // 5 sec
        listener=new Listener(this);
        client.addListener(listener);
        logger.info("正在重连中("+reconnectCount+"/3)...");
        Thread.sleep(3000);
        logger.info("正在重新载入脚本...");
        Main.scriptManager=new ScriptManager();
        Main.scriptManager.init();
        Main.scriptManager.loadFolder(ScriptManager.dir);
        logger.info("正在连接服务器...");
        client.connect(true);
        logger.info("正在重启更新线程...");
        updateThread.start();
        logger.info("重连完成!");
    }
}
