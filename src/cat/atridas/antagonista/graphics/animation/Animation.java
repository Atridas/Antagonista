package cat.atridas.antagonista.graphics.animation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.animation.ArmatureInstance.BoneInstance;

/**
 * TODO
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.3
 *
 */
public final class Animation extends Resource {
  private static Logger LOGGER = Logger.getLogger(Animation.class.getCanonicalName());

  /**
   * "ani"
   * @since 0.1
   */
  private static final HashedString HS_ANI = new HashedString("ani");
  
  /**
   * Armature this animation is directed.
   * @since 0.3
   */
  private ArmatureCore armature;
  
  /**
   * Time this animation lasts, in seconds.
   * @since 0.3
   */
  private float duration;
  
  /**
   * Total number of samples.
   * @since 0.3
   */
  private int samples;
  
  private final HashMap<HashedString, BoneSample[]> boneTracks = new HashMap<>();

  public Animation(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading animation " + resourceName);
    
    assert HS_ANI.equals(extension);

    try {
      Utils.CommonFileTypes mft = Utils.readHeader(is, Utils.FILE_TYPES, Utils.CommonFileTypes.ERROR);
      
      switch(mft) {
      case TEXT:
        return loadText(is);
      case BINARY:
        return loadBinary(is);
      case ERROR:
      default:
        LOGGER.warning("Unrecognized header");
        return false;
      }
      
    } catch (IOException e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      return false;
    }
  }
  
  /**
   * Loads a text file.
   * @param is file
   * @return if the resource was correctly loaded.
   * @since 0.3
   */
  private boolean loadText(InputStream is) {
    try {
      String str = Utils.readInputStream(is);
      String[] lines = str.split("\n");
      
      assert lines.length >= 4;
      
      String[] parameters = lines[2].split(" ");
      assert parameters.length == 4;
      
      HashedString armatureName = new HashedString(parameters[0]);
      armature = Core.getCore().getArmatureManager().getResource(armatureName);
      samples = Integer.parseInt(parameters[1]);
      duration = Float.parseFloat(parameters[2]);
      
      int numBones = Integer.parseInt(parameters[3]);

      Vector3f translation = new Vector3f();
      Quat4f rotation = new Quat4f();
      float scale;
      
      for(int i = 0; i < numBones; ++i) {
        int boneFirstLine = 4 + (i * (samples + 1));
        HashedString boneId = new HashedString(lines[boneFirstLine]);
        BoneSample[] boneTrack = new BoneSample[samples];
        
        for(int j = 0; j < samples; ++j) {
          int line = boneFirstLine + 1 + j;
          String data[] = lines[line].split(" ");
          assert data.length == 8;

          translation.x = Float.parseFloat(data[0]);
          translation.y = Float.parseFloat(data[1]);
          translation.z = Float.parseFloat(data[2]);

          rotation.w = Float.parseFloat(data[3]);
          rotation.x = Float.parseFloat(data[4]);
          rotation.y = Float.parseFloat(data[5]);
          rotation.z = Float.parseFloat(data[6]);

          scale = Float.parseFloat(data[7]);
          
          boneTrack[j] = new BoneSample(translation, rotation, scale);
        }
        
        boneTracks.put(boneId, boneTrack);
      }
      
      assert samples >= 2 || samples == 0;
      
      return true;
    } catch(Exception e) {
      LOGGER.warning("Error loading animation file with text format.");
      return false;
    }
  }
  
  /**
   * Loads a binary file.
   * @param is file
   * @return if the resource was correctly loaded.
   * @since 0.3
   */
  private boolean loadBinary(InputStream is) {
    throw new IllegalStateException("Not yet implemented");
  }

  void loadDefault() {
    armature = Core.getCore().getArmatureManager().getDefaultResource();
    duration = 0;
    samples = 0;
  }
  
  public float getDuration() {
    return duration;
  }
  
  /**
   * Sets the bone parameters to this animation state, in the time specified.
   * If the time isn't between 0 and the duration of this animation, the result is
   * unknown (may throw an exception).
   * 
   * @param bone_ to be animated.
   * @param time in seconds, of this animation.
   */
  public void setBone(BoneInstance bone_, float time) {
    if(samples == 0) { //default animation
      setDefaultBone(bone_);
      return;
    }
    
    float normalizedTime = time / duration;
    setBoneNormalized(bone_, normalizedTime);
  }
  
  /**
   * Sets the bone parameters to this animation, in the time specified, as if the whole
   * animation would last 1 unit. If the time isn't between 0 and 1, the result is
   * unknown (may throw an exception).
   * 
   * @param bone_ to be animated.
   * @param normalizedTime a value between 0 and 1
   */
  public void setBoneNormalized(BoneInstance bone_, float normalizedTime) {
    assert normalizedTime >= 0 && normalizedTime <= 1f;
    assert bone_.getArmatureId().equals(armature.resourceName);
    
    if(samples == 0) { //default animation
      setDefaultBone(bone_);
      return;
    }
    
    BoneSample[] track = boneTracks.get(bone_.getBoneId());

    if(track == null) { //default animation
      setDefaultBone(bone_);
      return;
    }
    
    float sampledTime = normalizedTime * (samples - 1);
    int firstSample = (int) sampledTime;
    float blendFactor = sampledTime - firstSample;
    if(blendFactor < Utils.EPSILON) {
      //estem sobre una mostra exactament, no cal "normalitzar"
      
      BoneSample sample = track[firstSample];

      bone_.translation.set(sample.translation);
      bone_.rotation.set(sample.rotation);
      bone_.scale = sample.getScale();
    } else {

      BoneSample sample1 = track[firstSample    ];
      BoneSample sample2 = track[firstSample + 1];

      bone_.translation.interpolate(
          sample1.translation, 
          sample2.translation, 
          blendFactor);
      bone_.rotation.interpolate(
          sample1.rotation, 
          sample2.rotation, 
          blendFactor);
      bone_.scale = sample1.scale * (1.f - blendFactor) + sample2.scale * blendFactor;
    }
  }
  
  private void setDefaultBone(BoneInstance bone_) {//TODO agafar la posició de l'esquelet
    bone_.translation.set(0,0,0);
    bone_.rotation.set(0,0,0,1);
    bone_.scale = 1;
  }
  
  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
    return 0;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

  public final static class BoneSample {
    private final Vector3f translation;
    private final Quat4f rotation;
    private final float scale;
    
    private BoneSample(Vector3f _translation, Quat4f _rotation, float _scale) {
      assert ((new Vector4f(_rotation)).lengthSquared() - 1) < Utils.EPSILON; //comprovem que tingui longitud 1 (més o menys)
      
      translation = new Vector3f(_translation);
      rotation = new Quat4f(_rotation);
      scale = _scale;
    }
    
    public void getRotation(Quat4f rotation_) {
      rotation_.set(rotation);
    }
    
    public void getTranslation(Vector3f translation_) {
      translation_.set(translation);
    }
    
    public float getScale() {
      return scale;
    }
  }
}
