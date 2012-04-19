package cat.atridas.antagonista.graphics.animation;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;

public interface BoneInstance {

  Vector3f getTranslation();

  Quat4f getRotation();

  float getScale();

  void setScale(float _scale);

  HashedString getArmatureId();

  HashedString getBoneId();
}
