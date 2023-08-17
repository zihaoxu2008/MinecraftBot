package xyz.zihaoxu.Bots;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import xyz.zihaoxu.Main;
import xyz.zihaoxu.Utils.Logger;

public class Bot {
    private TcpClientSession client;
    private Listener listener;
    private Logger logger;
    public Bot(){
        this.logger=new Logger("BotBootstrap");
        String[] tmp= Main.basicConfig.serverAddress.split(":");
        String host=tmp[0];
        int port=25565;
        if (tmp.length==2){
            port=Integer.parseInt(tmp[1]);
        }
        client=new TcpClientSession(host,port, new MinecraftProtocol(Main.basicConfig.userName));
        // client.setConnectTimeout(5); // 5 sec
        listener=new Listener();
    }

    public void run(){
        client.addListener(listener);
        logger.info("正在启动机器人并连接到服务器 "+Main.basicConfig.serverAddress);
        client.connect(true);
        logger.info("正在启动更新线程");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Main.scriptManager.call("onUpdate");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("更新线程出现错误!onUpdate事件将不再调用");
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
