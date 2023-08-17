package xyz.zihaoxu.Minecraft;

import com.github.steveice10.mc.protocol.data.game.entity.object.ObjectData;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;

import java.util.UUID;

public class Entity {
    public int Id;
    public EntityType type;
    public ObjectData data;
    public UUID uuid;
    public Entity(int id,EntityType type,ObjectData data,UUID uuid){
        this.Id=id;
        this.type=type;
        this.data=data;
        this.uuid=uuid;
    }
}
