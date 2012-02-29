package cat.atridas.antagonista.graphics.animation;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;

public class LerpAnimation implements AnimationInstance {
  
  private final AnimationInstance firstAnimation, secondAnimation;
  private final HashedString parameterID;
  
  private final float duration;

  private float blendFactor;
  private float time;
  
  public LerpAnimation(
      AnimationInstance _firstAnimation,
      AnimationInstance _secondAnimation,
      HashedString _parameterID)
  {
    firstAnimation = _firstAnimation;
    secondAnimation = _secondAnimation;
    parameterID = _parameterID;
    
    duration = (firstAnimation.getDuration() + secondAnimation.getDuration()) * .5f;
  }

  @Override
  public void setParameter(HashedString parameter, float value) {
    if(parameterID.equals(parameter)) {
      blendFactor = value;
    }
    firstAnimation.setParameter(parameter, value);
    secondAnimation.setParameter(parameter, value);
  }

  @Override
  public void update(float _time) {
    updateNormalized(_time / duration);
  }

  @Override
  public void updateNormalized(float _time) {
    time = _time;
    assert time >= 0 && time <= 1;

    firstAnimation .updateNormalized(_time);
    secondAnimation.updateNormalized(_time);
  }

  @Override
  public void getBone(BoneInstance bone_) {
    firstAnimation.getBone(bone_);
    secondAnimation.modifyBone(bone_, blendFactor);
  }

  private Bone m_bAux = new Bone();
  @Override
  public void modifyBone(BoneInstance bone_, float weight) {
    if(weight == 0)
      return;
    else if(weight == 1) {
      getBone(bone_);
      return;
    }
    assert weight > 0 && weight < 1;
    
    m_bAux.armature = bone_.getArmatureId();
    m_bAux.bone = bone_.getBoneId();
    

    firstAnimation.getBone(m_bAux);
    secondAnimation.modifyBone(m_bAux, blendFactor);

    bone_.getTranslation().interpolate(m_bAux.translation, weight);
    bone_.getRotation().interpolate(m_bAux.rotation, weight);
    bone_.setScale( bone_.getScale() * (1 - weight) + m_bAux.scale * weight );
  }

  @Override
  public float getDuration() {
    return duration;
  }

  private final static class Bone implements BoneInstance {
    final Vector3f translation = new Vector3f();
    final Quat4f rotation = new Quat4f();
    float scale = 1;
    
    HashedString armature;
    HashedString bone;
    
    public HashedString getArmatureId() {
      return armature;
    }
    
    public HashedString getBoneId() {
      return bone;
    }

    @Override
    public Vector3f getTranslation() {
      return translation;
    }

    @Override
    public Quat4f getRotation() {
      return rotation;
    }

    @Override
    public float getScale() {
      return scale;
    }

    @Override
    public void setScale(float _scale) {
      scale = _scale;
    }
  }
}
