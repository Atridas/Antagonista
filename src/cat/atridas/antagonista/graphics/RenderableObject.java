package cat.atridas.antagonista.graphics;

import javax.vecmath.Matrix4f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;

public final class RenderableObject {
  public final HashedString name;
  
  private final Transformation trans = new Transformation();
  public final Mesh mesh;
  
  public boolean visible = true;
  public boolean culled  = false;
  
  public RenderableObject(HashedString _name, Mesh _mesh) {
    name = _name;
    mesh = _mesh;
  }

  public void getTransformation(Transformation trans_) {
    trans_.setTransform(trans);
  }

  public void setTransformation(Transformation _trans) {
    trans.setTransform(_trans);
  }

  public void getTransformation(Matrix4f matrix_) {
    trans.getMatrix(matrix_);
  }

  public void setTransformation(Matrix4f _trans) {
    trans.setTransform(_trans);
  }
}
