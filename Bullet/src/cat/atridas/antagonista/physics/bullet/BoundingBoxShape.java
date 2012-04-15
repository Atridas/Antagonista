package cat.atridas.antagonista.physics.bullet;

import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;

public class BoundingBoxShape implements PhysicShapeBullet {
  private final BoxShape bulletShape;
  private final Vector3f fromGameToBulletVector;
  
  public BoundingBoxShape(Tuple3f minBB, Tuple3f maxBB) {
    
    fromGameToBulletVector = new Vector3f(maxBB);
    fromGameToBulletVector.sub(minBB);
    fromGameToBulletVector.scale(.5f);
    
    bulletShape = new BoxShape(fromGameToBulletVector);
    
    fromGameToBulletVector.add(minBB, maxBB);
    fromGameToBulletVector.scale(.5f);
    
  }

  @Override
  public BoxShape getBulletShape() {
    return bulletShape;
  }

  @Override
  public void getFromGameToBulletVector(Vector3f out_) {
    out_.set(fromGameToBulletVector);
  }
}
