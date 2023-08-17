package xyz.zihaoxu.Minecraft;

import com.github.steveice10.mc.protocol.data.game.inventory.ContainerType;

public class Container {
    public int id;
    public String title;
    public ContainerType type;
    public Container(int id,String title,ContainerType type){
        this.id=id;
        this.title=title;
        this.type=type;
    }
}
