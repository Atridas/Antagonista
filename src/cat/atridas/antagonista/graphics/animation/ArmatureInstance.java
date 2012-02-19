package cat.atridas.antagonista.graphics.animation;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.FontManager;
import cat.atridas.antagonista.graphics.animation.ArmatureCore.BoneCore;

public final class ArmatureInstance {
  private final ArmatureCore armatureCore;
  
  private ArmatureIntern animatedResult;
  
  private Matrix4f[] matrixPalete;
  
  public ArmatureInstance(ArmatureCore _armatureCore) {
    armatureCore = _armatureCore;
    
    animatedResult = new ArmatureIntern();
    
    matrixPalete = new Matrix4f[animatedResult.boneMap.size()];
    for(int i = 0; i < matrixPalete.length; ++i) {
      matrixPalete[i] = new Matrix4f();
      matrixPalete[i].setIdentity();
    }
    
    //TODO agafar posició "per defecte"
    updateMatrixPalete();
  }
  
  
  public void performSingleAnimation(Animation animation, float time) {
    for(BoneInstance bone : animatedResult.boneMap.values()) {
      animation.setBone(bone, time);
    }
    
    updateMatrixPalete();
  }
  
  public Matrix4f[] getMatrixPalete() {
    return matrixPalete;
  }
  
  public void debugRender(DebugRender dr, Matrix4f worldMatrix) {
    Matrix4f aux = new Matrix4f();
    Vector3f translation = new Vector3f();
    Font font = Core.getCore().getFontManager().getResource(FontManager.FONT_14);
    
    for(int i = 0; i < matrixPalete.length; ++i) {
      BoneCore boneCore = armatureCore.getBone(i);
      
      aux.set(worldMatrix);
      aux.mul(matrixPalete[i]);
      boneCore.mulTransformMatrix(aux);
      
      dr.addAxes(aux, .1f, false);
      
      aux.get(translation);
      dr.addString(translation, font, boneCore.getName().toString(), .1f, Utils.BLACK, false);
    }
    
  }

  private void updateMatrixPalete() {
    for(BoneInstance bone : animatedResult.rootBones) {
      Matrix4f currentMatrix = matrixPalete[bone.boneCore.getIndex()];
      
      currentMatrix.set(bone.rotation, bone.translation, bone.scale);
      
      for(BoneInstance child : bone.children) {
        updateMatrixPaleteBone(child, currentMatrix);
      }
    }
    
    
    for(int i = 0; i < matrixPalete.length; ++i) {
      armatureCore.getBone(i).mulInverseTransformMatrix(matrixPalete[i]);
    }
  }
  
  private void updateMatrixPaleteBone(BoneInstance bone, Matrix4f parentMatrix) {
    Matrix4f currentMatrix = matrixPalete[bone.boneCore.getIndex()];

    currentMatrix.set(bone.rotation, bone.translation, bone.scale);
    currentMatrix.mul(parentMatrix, currentMatrix);
    //currentMatrix.mul(parentMatrix);

    for(BoneInstance child : bone.children) {
      updateMatrixPaleteBone(child, currentMatrix);
    }
  }
  
  private final class ArmatureIntern {
    private final BoneInstance[] rootBones;
    private final HashMap<HashedString, BoneInstance> boneMap = new HashMap<>();
    
    private ArmatureIntern() {
      
      for(int i = 0; i < armatureCore.getNumBones(); ++i) {
        BoneCore boneCore = armatureCore.getBone(i);
        HashedString id = boneCore.getName();
        
        BoneInstance boneInstance = new BoneInstance(boneCore);
        
        boneMap.put(id, boneInstance);
      }
      
      ArrayList<BoneInstance> l_RootBones = new ArrayList<>();
      
      for(BoneInstance bone : boneMap.values()) {
        BoneCore parentCore = bone.boneCore.getParent();
        if(parentCore == null) {
          l_RootBones.add(bone);
        } else {
          BoneInstance parent = boneMap.get(parentCore.getName());
          parent.children.add(bone);
          //bone.parent = parent;
        }
      }
      
      rootBones = new BoneInstance[l_RootBones.size()];
      l_RootBones.toArray(rootBones);
      
      for(BoneInstance bone : boneMap.values()) {
        bone.children.trimToSize();
      }
    }
    
  }
  
  public final class BoneInstance {
    final Vector3f translation = new Vector3f();
    final Quat4f rotation = new Quat4f();
    float scale = 1;

    private final BoneCore boneCore;
    
    //private BoneInstance parent;
    private final ArrayList<BoneInstance> children = new ArrayList<>();
    
    private BoneInstance(BoneCore _boneCore) {
      boneCore = _boneCore;
    }
    
    public HashedString getArmatureId() {
      return armatureCore.resourceName;
    }
    
    public HashedString getBoneId() {
      return boneCore.getName();
    }
  }
}
