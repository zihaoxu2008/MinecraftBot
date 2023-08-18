package xyz.zihaoxu.Bots;

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundPlayerInfoUpdatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundEntityEventPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.ClientboundRemoveEntitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerCombatKillPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerClosePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.level.ClientboundSoundPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.title.ClientboundSetTitleTextPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import xyz.zihaoxu.Main;
import xyz.zihaoxu.Minecraft.Container;
import xyz.zihaoxu.Minecraft.Entity;
import xyz.zihaoxu.Minecraft.Player;
import xyz.zihaoxu.Utils.Logger;
import xyz.zihaoxu.script.obj.ScriptBot;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Listener implements SessionListener {
    private boolean isConnected;
    private Logger logger;
    public ScriptBot bot;
    private Bot controller;
    public Listener(Bot bot){
        this.controller=bot;
        logger=new Logger("Bot");
    }

    private String parseTextComponent(TextComponent tc){
        StringBuilder ret=new StringBuilder();
        ret.append(tc.content());
        if (!tc.children().isEmpty()){
            for (Component tcc:tc.children()){
                if (tcc instanceof TextComponent){
                    ret.append(parseTextComponent((TextComponent) tcc));
                } else if (tcc instanceof TranslatableComponent) {
                    ret.append(((TranslatableComponent) tcc).key());
                }
            }
        }
        return ret.toString();
    }

    @Override
    public void packetReceived(Session session, Packet packet) {
        bot=new ScriptBot(session);
        if (packet instanceof ClientboundPlayerChatPacket
            && ((ClientboundPlayerChatPacket) packet).getName() instanceof TextComponent){
            logger.info("接收到来自"+
                    ((TextComponent) ((ClientboundPlayerChatPacket) packet).getName()).content()+
                    "的聊天信息: "+((ClientboundPlayerChatPacket) packet).getContent());
            Main.scriptManager.call(
                    "onChat",
                    bot,
                    ((TextComponent) ((ClientboundPlayerChatPacket) packet).getName()).content(),
                    ((ClientboundPlayerChatPacket) packet).getContent());
        }else if (packet instanceof ClientboundPlayerChatPacket){
            logger.info("接收到未知发送者的聊天信息: "+((ClientboundPlayerChatPacket) packet).getContent());
            Main.scriptManager.call("onChat",bot,"",
                    ((ClientboundPlayerChatPacket) packet).getContent());
        }
        if (packet instanceof ClientboundSystemChatPacket
                && ((ClientboundSystemChatPacket) packet).getContent() instanceof TextComponent){
            String msg=parseTextComponent((TextComponent) (((ClientboundSystemChatPacket) packet).getContent()));
            logger.info("接收到服务器信息: "+msg);
            Main.scriptManager.call("onServerChat",bot,msg);
        } else if (packet instanceof ClientboundSystemChatPacket
            && ((ClientboundSystemChatPacket) packet).getContent() instanceof TranslatableComponent) {
            String key=((TranslatableComponent) ((ClientboundSystemChatPacket) packet).getContent()).key();
            List<String> values= new java.util.ArrayList<>(Collections.emptyList());
            for (Component t:((TranslatableComponent) ((ClientboundSystemChatPacket) packet).getContent()).args()){
                if (t instanceof TextComponent){
                    values.add(((TextComponent) t).content());
                }
            }
            logger.info("接收到服务器的TranslatableComponent: "+key);
            Main.scriptManager.call("onTranslatableComponent",bot,key,values);
        }
        if (packet instanceof ClientboundPlayerCombatKillPacket){
            logger.info("机器人死亡");
            Main.scriptManager.call("onDied",bot);
        }
        if (packet instanceof ClientboundSetTitleTextPacket
                && ((ClientboundSetTitleTextPacket) packet).getText() instanceof TextComponent){
            String text=((TextComponent) ((ClientboundSetTitleTextPacket) packet).getText()).content();
            logger.info("接收到标题: "+text);
            Main.scriptManager.call("onTitle",bot,text);
        }
        if (packet instanceof ClientboundLoginPacket){
            logger.info("机器人登录");
            Main.scriptManager.call("onLogin",bot);
        }
        if (packet instanceof ClientboundAddEntityPacket){
            Entity ent=new Entity(
                    ((ClientboundAddEntityPacket) packet).getEntityId(),
                    ((ClientboundAddEntityPacket) packet).getType(),
                    ((ClientboundAddEntityPacket) packet).getData(),
                    ((ClientboundAddEntityPacket) packet).getUuid()
            );
            logger.info("新增生物: Id: "+ent.Id+",类型: "+ent.type.toString());
            bot.addEntity(ent);
            Main.scriptManager.call("addEntity",bot,ent);
        }
        if (packet instanceof ClientboundEntityEventPacket){
            logger.info("生物(ID: "
                    +((ClientboundEntityEventPacket) packet).getEntityId()
                    +")触发事件: "
                    +((ClientboundEntityEventPacket) packet).getEvent().toString());
            Main.scriptManager.call("entityEvent",bot,
                    ((ClientboundEntityEventPacket) packet).getEntityId(),
                    ((ClientboundEntityEventPacket) packet).getEvent().toString());
        }
        if (packet instanceof ClientboundAddPlayerPacket){
            Entity ent=new Entity(
                    ((ClientboundAddPlayerPacket) packet).getEntityId(),
                    EntityType.PLAYER,
                    null,
                    ((ClientboundAddPlayerPacket) packet).getUuid()
            );
            // logger.info(ent.uuid.toString());
            logger.info("新增玩家生物: Id: "+ent.Id+",类型: "+ent.type.toString());
            bot.addEntity(ent);
            Main.scriptManager.call("addPlayerEntity",bot,ent);
        }
        if (packet instanceof ClientboundRemoveEntitiesPacket){
            int[] ids=((ClientboundRemoveEntitiesPacket) packet).getEntityIds();
            List ids_list=Arrays.asList(ids);
            for (Entity ent:ScriptBot.entities){
                if (ids_list.contains(ent.Id)){
                    logger.info("移除生物: Id: "+ent.Id+",类型: "+ent.type.toString());
                    bot.removeEntity(ent);
                }
            }
        }
        if (packet instanceof ClientboundPlayerInfoUpdatePacket){
            PlayerListEntry[] playerListEntry=((ClientboundPlayerInfoUpdatePacket) packet).getEntries();
            for (PlayerListEntry p:playerListEntry){
                if (p.getDisplayName() instanceof TextComponent){
                    // logger.info(p.getDisplayName().toString());
                    int id=-1;
                    for (Entity ent:ScriptBot.entities){
//                        logger.info("__________________________");
//                        logger.info(ent.uuid.toString());
//                        logger.info(p.getProfileId().toString());
                        if(ent.uuid.equals(p.getProfileId())){
                            id=ent.Id;
                        }
                    }
                    Player player=new Player(
                            id,
                            p.getProfileId(),
                            p.getProfile(),
                            p.getGameMode(),
                            p.getProfile().getName()
                    );
                    // logger.info(p.getProfileId().toString());
                    boolean found=false;
                    Main.scriptManager.call("playerUpdate",bot,player);
                    for (Player player_:ScriptBot.players){
                        if (player_.profileId==p.getProfileId()){
                            player_.entityID=id;
                            player_.gameMode=p.getGameMode();
                            player_.displayName=p.getProfile().getName();
                            player_.profile=p.getProfile();
                            found=true;
                        }
                    }
                    if (!found){
                        bot.addPlayer(player);
                    }
                    logger.info("玩家" +
                            p.getProfile().getName()+
                            "状态更新(ID:"+id+")");
                }
            }
        }
        if (packet instanceof ClientboundOpenScreenPacket){
            if (((ClientboundOpenScreenPacket) packet).getTitle() instanceof TextComponent){
                String title=parseTextComponent((TextComponent) ((ClientboundOpenScreenPacket) packet).getTitle());
                Container container=new Container(((ClientboundOpenScreenPacket) packet).getContainerId(),title,
                        ((ClientboundOpenScreenPacket) packet).getType());
                logger.info("打开容器"+container.type.toString()+": "+container.title+",容器ID: "+container.id);
                bot.addContainer(container);
                Main.scriptManager.call("addContainer",bot,container);
            }
        }
        if (packet instanceof ClientboundContainerClosePacket){
            for (Container c:ScriptBot.containers){
                if (c.id==((ClientboundContainerClosePacket) packet).getContainerId()){
                    bot.removeContainer(c);
                    Main.scriptManager.call("removeContainer",bot,c);
                    return;
                }
            }
        }
        if (packet instanceof ClientboundPlayerPositionPacket){
            ScriptBot.posX=((ClientboundPlayerPositionPacket) packet).getX();
            ScriptBot.posY=((ClientboundPlayerPositionPacket) packet).getY();
            ScriptBot.posZ=((ClientboundPlayerPositionPacket) packet).getZ();
        }
        if (packet instanceof ClientboundSoundPacket){
            logger.info("播放音效: "+((ClientboundSoundPacket) packet).getSound().getName());
            Main.scriptManager.call("onSound",bot,((ClientboundSoundPacket) packet).getSound().getName());
        }
    }

    @Override
    public void packetSending(PacketSendingEvent event) {
        bot=new ScriptBot(event.getSession());
    }

    @Override
    public void packetSent(Session session, Packet packet) {
        bot=new ScriptBot(session);
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        bot=new ScriptBot(event.getSession());
        Main.scriptManager.call("onPacketError",bot,event.getCause().getMessage());
    }

    @Override
    public void connected(ConnectedEvent event) {
        Bot.reconnectCount=0;
        bot=new ScriptBot(event.getSession());
        this.isConnected=true;
        logger.info("服务器已连接");
        Main.scriptManager.call("onConnected",bot);
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        bot=new ScriptBot(event.getSession());
        this.isConnected=false;
        logger.info("正在与服务器断开连接");
        Main.scriptManager.call("onDisconnecting",bot);
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        bot=new ScriptBot(event.getSession());
        if (event.getReason() instanceof TextComponent){
            logger.info("与服务器断开连接: "+parseTextComponent((TextComponent) event.getReason()));
        } else if (event.getReason() instanceof TranslatableComponent) {
            logger.info("与服务器断开连接: "+((TranslatableComponent) event.getReason()).key());
        } else {
            logger.info("与服务器断开连接");
        }
        // logger.info(event.toString());
        Main.scriptManager.call("onDisconnected",bot);
        try {
            this.controller.reconnect();
        } catch (IOException | ScriptException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
