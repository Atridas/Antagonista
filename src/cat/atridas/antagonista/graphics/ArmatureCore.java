package cat.atridas.antagonista.graphics;

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

public class ArmatureCore extends Resource {
  private static Logger LOGGER = Logger.getLogger(ArmatureCore.class.getCanonicalName());

  /**
   * "arm"
   * @since 0.1
   */
  private static final HashedString HS_ARM = new HashedString("arm");
  
  private ArrayList<Bone> bones = new ArrayList<>();
  private HashMap<HashedString, Bone> boneMap = new HashMap<>();

  public ArmatureCore(HashedString _resourceName) {
    super(_resourceName);
  }
  
  public Bone getBone(int id) {
    return bones.get(id);
  }
  
  public Bone getBone(HashedString id) {
    return boneMap.get(id);
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
  
  private Bone loadBone(String[] lines, Bone parent) {
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
    Bone bone = new Bone(id, transformMatrix, numChildren, parent);
    
    bones.add(bone);
    boneMap.put(id, bone);
    
    for(int i = 0; i < numChildren; ++i) {
      Bone child = loadBone(lines, bone);
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
      
      return true;
    } catch(Exception e) {
      LOGGER.warning("Error loading material file with text format.");
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
    Bone root = new Bone(Utils.ROOT, identity, 0, null);
    
    bones.add(root);
    boneMap.put(Utils.ROOT, root);
    
  }

  @Override
  public int getRAMBytesEstimation() {
    return Bone.RAM_SIZE * bones.size();
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
  public static class Bone {
    private final HashedString name;
    private final Matrix4f transformMatrix, inverseTransformMatrix;
    private final ArrayList<Bone> children;
    private final Bone parent;
    
    private static final int RAM_SIZE = Utils.LONG_SIZE //name
                                      + 2 * 16 * Utils.FLOAT_SIZE //matrixes
                                      + Utils.INTEGER_SIZE * 2; //apuntadors
    
    private Bone(
        HashedString _name, 
        Matrix4f _transformMatrix,
        int numChildren,
        Bone _parent)
    {
      name = _name;
      
      transformMatrix = new Matrix4f(_transformMatrix);
      inverseTransformMatrix = new Matrix4f(transformMatrix);
      inverseTransformMatrix.invert();
      
      children = new ArrayList<>(numChildren);
      
      parent = _parent;
    }
    
    private void addChild(Bone child) {
      children.add(child);
    }

    public HashedString getName() {
      return name;
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

    public ArrayList<Bone> getChildren() {
      return children;
    }

    public Bone getParent() {
      return parent;
    }
    
  }
  
}
