package xyz.zihaoxu.Minecraft;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Player {
    public int entityID;
    public UUID profileId;
    public GameProfile profile;
    public GameMode gameMode;
    public String displayName;
    public Player(int EntityID,UUID profileId,GameProfile gameProfile,GameMode gameMode,String displayName){
        this.entityID=EntityID;
        this.profileId=profileId;
        this.profile=gameProfile;
        this.gameMode=gameMode;
        this.displayName=displayName;
    }
}
