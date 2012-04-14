package cat.atridas.antagonista.graphics.animation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;
import cat.atridas.antagonista.graphics.FontManager;

public class ArmatureCore extends Resource {
  private static Logger LOGGER = Logger.getLogger(ArmatureCore.class.getCanonicalName());

  /**
   * "arm"
   * @since 0.3
   */
  private static final HashedString HS_ARM = new HashedString("arm");
  
  private ArrayList<BoneCore> bones = new ArrayList<>();
  private HashMap<HashedString, BoneCore> boneMap = new HashMap<>();

  public ArmatureCore(HashedString _resourceName) {
    super(_resourceName);
  }
  
  public BoneCore getBone(int id) {
    return bones.get(id);
  }
  
  public BoneCore getBone(HashedString id) {
    return boneMap.get(id);
  }
  
  public int getNumBones() {
    return bones.size();
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    if(LOGGER.isLoggable(Level.CONFIG))
      LOGGER.config("Loading armature " + resourceName);
    
    assert HS_ARM.equals(extension);

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

  private static final int FIRST_BONE_LINE = 3;
  
  private BoneCore loadBone(String[] lines, BoneCore parent) {
    String[] line = lines[FIRST_BONE_LINE + bones.size()].split(" ");
    
    //name num_children pos.xyz rotation.wxyz scale
    assert line.length == 10;
    String name = line[0];
    int numChildren = Integer.parseInt(line[1]);

    Vector3f position = new Vector3f();
    Quat4f   rotation = new Quat4f();

    position.x = Float.parseFloat(line[2]);
    position.y = Float.parseFloat(line[3]);
    position.z = Float.parseFloat(line[4]);

    rotation.w = Float.parseFloat(line[5]);
    rotation.x = Float.parseFloat(line[6]);
    rotation.y = Float.parseFloat(line[7]);
    rotation.z = Float.parseFloat(line[8]);

    float scale = Float.parseFloat(line[9 ]);
    
    Matrix4f transformMatrix = new Matrix4f(rotation, position, scale);
    
    HashedString id = new HashedString(name);
    BoneCore bone = new BoneCore(id, transformMatrix, numChildren, parent, bones.size());
    
    bones.add(bone);
    boneMap.put(id, bone);
    
    for(int i = 0; i < numChildren; ++i) {
      BoneCore child = loadBone(lines, bone);
      bone.addChild(child);
    }
    
    return bone;
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
      
      String numRootBonesLine = lines[2];
      
      int rootBones = Integer.parseInt(numRootBonesLine);
      
      for(int i = 0; i < rootBones; ++i) {
        loadBone(lines, null);
      }
      
      bones.trimToSize();
      for(BoneCore bone : bones) {
        bone.children.trimToSize();
      }
      
      return true;
    } catch(Exception e) {
      LOGGER.warning("Error loading armature file with text format.");
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
  
  public void loadDefault() {
    Matrix4f identity = new Matrix4f();
    identity.setIdentity();
    BoneCore root = new BoneCore(Utils.ROOT, identity, 0, null, 0);
    
    bones.add(root);
    boneMap.put(Utils.ROOT, root);
    
  }
  
  public void debugRender(DebugRender dr, Matrix4f worldMatrix) {
    Matrix4f aux = new Matrix4f();
    Vector3f translation = new Vector3f();
    Font font = Core.getCore().getFontManager().getResource(FontManager.FONT_14);
    
    for(BoneCore bone : bones) {
      aux.set(worldMatrix);
      bone.mulTransformMatrix(aux);
      
      dr.addAxes(aux, .1f, false);
      
      aux.get(translation);
      dr.addString(translation, font, bone.getName().toString(), .1f, Utils.BLACK, false);
    }
    
  }

  @Override
  public int getRAMBytesEstimation() {
    return BoneCore.RAM_SIZE * bones.size();
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

  /**
   * TODO
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.3
   *
   */
  public static class BoneCore {
    private final HashedString name;
    private final Matrix4f transformMatrix, inverseTransformMatrix;
    private final ArrayList<BoneCore> children;
    private final BoneCore parent;
    private final int index;
    
    private static final int RAM_SIZE = Utils.LONG_SIZE //name
                                      + 2 * 16 * Utils.FLOAT_SIZE //matrixes
                                      + Utils.INTEGER_SIZE * 2; //apuntadors
    
    private BoneCore(
        HashedString _name, 
        Matrix4f _transformMatrix,
        int numChildren,
        BoneCore _parent,
        int _index)
    {
      name = _name;
      
      transformMatrix = new Matrix4f(_transformMatrix);
      inverseTransformMatrix = new Matrix4f(transformMatrix);
      inverseTransformMatrix.invert();
      
      children = new ArrayList<>(numChildren);
      
      parent = _parent;
      
      index = _index;
    }
    
    private void addChild(BoneCore child) {
      children.add(child);
    }

    public HashedString getName() {
      return name;
    }

    public int getIndex() {
      return index;
    }

    public void getTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.set( transformMatrix );
    }

    public void getInverseTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.set( inverseTransformMatrix );
    }

    public void mulTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.mul( transformMatrix );
    }

    public void mulInverseTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.mul( inverseTransformMatrix );
    }

    public void mulRightTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.mul( transformMatrix, outputMatrix_ );
    }

    public void mulRightInverseTransformMatrix(Matrix4f outputMatrix_) {
      outputMatrix_.mul( inverseTransformMatrix, outputMatrix_ );
    }

    public ArrayList<BoneCore> getChildren() {
      return children;
    }

    public BoneCore getParent() {
      return parent;
    }
    
  }
  
}
