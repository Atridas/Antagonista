package cat.atridas.antagonista.physics.bullet;


import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.dynamics.character.KinematicCharacterController;

public class KinematicCharacter implements BulletBody {

  private KinematicCharacterController character;
  private PairCachingGhostObject ghostObject;
  
  public KinematicCharacter(
      KinematicCharacterController _character,
      PairCachingGhostObject _ghostObject) {
    character = _character;
    ghostObject = _ghostObject;
  }
  
  @Override
  public KinematicCharacterController getBulletObject() {
    return character;
  }
  
  public PairCachingGhostObject getGhostObject() {
    return ghostObject;
  }
}
