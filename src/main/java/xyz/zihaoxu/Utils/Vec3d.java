package xyz.zihaoxu.Utils;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vec3d {
    public double x;
    public double y;
    public double z;
    public Vec3d(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public double distanceTo(Vec3d pos){
        return 0.0d;
    }
}
