package xyz.zihaoxu.script.obj;

import com.github.steveice10.mc.protocol.data.game.ClientCommand;
import com.github.steveice10.mc.protocol.data.game.entity.object.Direction;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ClickItemAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerActionType;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.*;
import com.github.steveice10.packetlib.Session;
import org.cloudburstmc.math.vector.Vector3i;
import xyz.zihaoxu.Bots.Bot;
import xyz.zihaoxu.Main;
import xyz.zihaoxu.Minecraft.Container;
import xyz.zihaoxu.Minecraft.Entity;
import xyz.zihaoxu.Minecraft.Player;
import xyz.zihaoxu.Utils.Logger;
import xyz.zihaoxu.script.ScriptManager;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class ScriptBot {
    private Session session;
    private Logger logger;
    public static ArrayList<Entity> entities=new ArrayList<>();
    public static ArrayList<Player> players=new ArrayList<>();
    public static ArrayList<Container> containers=new ArrayList<>();
    public static double posX=0.0d;
    public static double posY=0.0d;
    public static double posZ=0.0d;
    public ScriptBot(Session session){
        this.session=session;
        this.logger=new Logger("ScriptEngine");
    }
    public void sendChatMessage(String message){
        if (message.startsWith("/")){
            sendCommand(message.substring(1));
            return;
        }
        session.send(new ServerboundChatPacket(
                message,
                Instant.now().toEpochMilli(),
                0L,
                null,
                0,
                new BitSet()
        ));
        logger.info("发送聊天信息: "+message);
    }

    public void sendCommand(String command){
        session.send(new ServerboundChatCommandPacket(
                command,
                Instant.now().toEpochMilli(),
                0L,
                Collections.emptyList(),
                0,
                new BitSet()
        ));
        logger.info("执行服务器指令: "+command);
    }

    public void move(boolean onGround,double x,double y,double z){
        session.send(new ServerboundMovePlayerPosPacket(onGround,x,y,z));
    }

    public void respawn(){
        logger.info("正在重生...");
        session.send(new ServerboundClientCommandPacket(ClientCommand.RESPAWN));
    }

    public void reload() throws IOException, ScriptException {
        Main.scriptManager=new ScriptManager();
        Main.scriptManager.init();
        Main.scriptManager.loadFolder(ScriptManager.dir);
    }

    public void loadScriptFolder(String path) throws ScriptException, IOException {
        Main.scriptManager.loadFolder(new File(path));
    }

    public void addEntity(Entity ent){
        ScriptBot.entities.add(ent);
    }

    public void removeEntity(Entity ent){
        ScriptBot.entities.remove(ent);
    }

    public void addPlayer(Player ent){
        ScriptBot.players.add(ent);
    }

    public void removePlayer(Player ent){
        ScriptBot.players.remove(ent);
    }

    public void addContainer(Container c){
        ScriptBot.containers.add(c);
    }

    public void removeContainer(Container c){
        ScriptBot.containers.remove(c);
    }

    public void useEntity(int entityId,boolean isSneaking){
        logger.info("点击生物 "+entityId);
        session.send(new ServerboundInteractPacket(
                entityId,
                InteractAction.INTERACT,
                isSneaking
        ));
    }

    public void attackEntity(int entityId,boolean isSneaking){
        logger.info("攻击生物 "+entityId);
        session.send(new ServerboundInteractPacket(
                entityId,
                InteractAction.ATTACK,
                isSneaking
        ));
    }

    public void clickContainer(int container,int slot){
        logger.info("点击容器"+container+"中的槽位"+slot);
        session.send(new ServerboundContainerClickPacket(
                container,
                0,
                slot,
                ContainerActionType.CLICK_ITEM,
                ClickItemAction.LEFT_CLICK,
                null,
                Collections.EMPTY_MAP
        ));
    }

    public void useItem(int hand){
        logger.info("使用物品");
        session.send(new ServerboundUseItemPacket(
                hand==0?Hand.MAIN_HAND:Hand.OFF_HAND,
                0
        ));
    }

    public void changeSlot(int slot){
        if (slot<0 || slot>8){
            logger.info("错误的快捷栏槽位: "+slot);
            return;
        }
        logger.info("切换物品栏: "+slot);
        session.send(new ServerboundSetCarriedItemPacket(
                slot
        ));
    }

    public void dropItem(int direction){
        logger.info("丢弃物品");
        session.send(new ServerboundPlayerActionPacket(
                PlayerAction.DROP_ITEM,
                Vector3i.ZERO,
                Direction.from(direction),
                0
        ));
    }

    public double getPosX(){
        return ScriptBot.posX;
    }

    public double getPosY(){
        return ScriptBot.posY;
    }

    public double getPosZ(){
        return ScriptBot.posZ;
    }
}
