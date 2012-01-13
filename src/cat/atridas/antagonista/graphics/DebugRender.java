package cat.atridas.antagonista.graphics;

import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public abstract class DebugRender {

  protected final ArrayList<Line>        lines = new ArrayList<>();
  protected final ArrayList<Cross>       crosses = new ArrayList<>();
  protected final ArrayList<Sphere>      spheres = new ArrayList<>();
  protected final ArrayList<Circle>      circles = new ArrayList<>();
  protected final ArrayList<Axes>        axes = new ArrayList<>();
  protected final ArrayList<Triangle>    triangles = new ArrayList<>();
  protected final ArrayList<AABB>        aabbs = new ArrayList<>();
  protected final ArrayList<OBB>         obbs = new ArrayList<>();
  protected final ArrayList<DebugString> strings = new ArrayList<>();

  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float lineWidth, 
      float duration, 
      boolean depthEnabled) {
    lines.add(new Line(origin, destination, duration, depthEnabled, color, lineWidth));
  }
  
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      float duration, 
      boolean depthEnabled) {
    crosses.add(new Cross(center, size, duration, depthEnabled, color));
  }
  
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    spheres.add( new Sphere(center, radius, duration, depthEnabled, color));
  }
  
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    circles.add( new Circle(center, radius, planeNormal, duration, depthEnabled, color));
  }
  
  public void addAxes( 
      Matrix4f transformation,
      Color3f color,
      float size,
      float duration, 
      boolean depthEnabled) {
    axes.add(new Axes(transformation, size, duration, depthEnabled, color));
  }
  
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      float lineWidth, 
      float duration, 
      boolean depthEnabled) {
    triangles.add(new Triangle(v0, v1, v2, duration, depthEnabled, color, lineWidth));
  }
  
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color, 
      float lineWidth, 
      float duration, 
      boolean depthEnabled) {
    aabbs.add(new AABB(minCoords, maxCoords, duration, depthEnabled, color, lineWidth));
  }
  
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color, 
      float lineWidth, 
      float duration, 
      boolean depthEnabled) {
    obbs.add(new OBB(centerTransformation, scaleXYZ, duration, depthEnabled, color, lineWidth));
  }
  
  public void addString( 
      Point3f position,
      String text,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    strings.add(new DebugString(position, text, duration, depthEnabled, color));
  }
  
  
  
  

  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float lineWidth, 
      float duration 
      ) {
    addLine(origin,destination,color,lineWidth,duration,true);
  }
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float lineWidth) {
    addLine(origin,destination,color,lineWidth,0,true);
  }
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color) {
    addLine(origin,destination,color,1,0,true);
  }
  

  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      float duration) {
    addCross(center,color,size,duration,true);
  }
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size) {
    addCross(center,color,size,0,true);
  }
  

  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      float duration) {
    addSphere(center,radius,color,duration,true);
  }
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color) {
    addSphere(center,radius,color,0,true);
  }

  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color, 
      float duration) {
    addCircle(center, planeNormal, radius, color, duration, true);
  }
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color) {
    addCircle(center, planeNormal, radius, color, 0, true);
  }
  

  public void addAxes( 
      Matrix4f transformation,
      Color3f color,
      float size,
      float duration) {
    addAxes(transformation, color, size, duration, true);
  }
  public void addAxes( 
      Matrix4f transformation,
      Color3f color,
      float size) {
    addAxes(transformation, color, size, 0, true);
  }
  

  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      float lineWidth, 
      float duration) {
    addTriangle(v0, v1, v2, color, lineWidth, duration, true);
  }
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      float lineWidth) {
    addTriangle(v0, v1, v2, color, lineWidth, 0, true);
  }
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color) {
    addTriangle(v0, v1, v2, color, 1, 0, true);
  }
  

  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color, 
      float lineWidth, 
      float duration) {
    addAABB(minCoords, maxCoords, color, lineWidth, duration, true);
  }
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color, 
      float lineWidth) {
    addAABB(minCoords, maxCoords, color, lineWidth, 0, true);
  }
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color) {
    addAABB(minCoords, maxCoords, color, 1, 0, true);
  }
  

  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color, 
      float lineWidth, 
      float duration) {
    addOBB( centerTransformation, scaleXYZ, color, lineWidth, duration, true);
  }
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color, 
      float lineWidth) {
    addOBB( centerTransformation, scaleXYZ, color, lineWidth, 0, true);
  }
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color) {
    addOBB( centerTransformation, scaleXYZ, color, 1, 0, true);
  }

  public void addString( 
      Point3f position,
      String text,
      Color3f color, 
      float duration) {
    addString(position, text, color, duration, true);
  }
  public void addString( 
      Point3f position,
      String text,
      Color3f color) {
    addString(position, text, color, 0, true);
  }
  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
  public void render(float dt) {
    renderArrays();

    cleanList(lines, dt);
    cleanList(crosses, dt);
    cleanList(spheres, dt);
    cleanList(circles, dt);
    cleanList(axes, dt);
    cleanList(triangles, dt);
    cleanList(aabbs, dt);
    cleanList(obbs, dt);
    cleanList(strings, dt);
  }
  
  private static <T> void cleanList(ArrayList<? extends DebugObject> list, float dt) {
    int size = list.size();
    for(int i = 0; i < size; ++i) {
      DebugObject debugObject = list.get(i);
      if(debugObject.duration <= dt) {
        Collections.swap(list, i, size - 1);
        size--;
        i--;
      } else {
        debugObject.duration -= dt;
      }
    }
    for(int i = list.size() - 1; i >= size; --i) {
      list.remove(i);
    }
  }
  
  protected abstract void renderArrays();
  
  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  

  protected static class DebugObject {
    float duration;
    boolean depthEnabled;
    Color3f color;

    DebugObject(final float _duration, final boolean _depthEnabled, final Color3f _color) {
      duration = _duration;
      depthEnabled = _depthEnabled;
      color = new Color3f(_color);
    }
  }
  
  protected static class DebugObjectWithWidth extends DebugObject {
    float lineWidth;
    
    DebugObjectWithWidth(final float duration, final boolean depthEnabled, final Color3f color, final float _lineWidth) {
      super(duration, depthEnabled, color);
      lineWidth = _lineWidth;
    }
  }
  
  protected static final class Line extends DebugObjectWithWidth {
    Point3f origin;
    Point3f destination;
    
    Line(Point3f _origin, Point3f _destination,
        final float duration, final boolean depthEnabled, final Color3f color, final float lineWidth) {
      super(duration, depthEnabled, color, lineWidth);
      origin= new Point3f( _origin );
      destination = new Point3f( _destination );
    }
  }
  
  protected static final class Cross extends DebugObject {
    Point3f center;
    float size;
    
    Cross(Point3f _center, float _size,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      size = _size;
    }
  }
  
  protected static final class Sphere extends DebugObject {
    Point3f center;
    float radius;
    
    Sphere(Point3f _center, float _radius,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      radius = _radius;
    }
  }
  
  protected static final class Circle extends DebugObject {
    Point3f center;
    Vector3f planeNormal;
    float radius;
    
    Circle(Point3f _center, float _radius, Vector3f _planeNormal,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center = new Point3f ( _center );
      radius = _radius ;
      planeNormal = new Vector3f( _planeNormal );
    }
  }
  
  protected static final class Axes extends DebugObject {
    Matrix4f transformation;
    float size;
    
    Axes(Matrix4f _transformation, float _size,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      transformation = new Matrix4f( _transformation );
      size = _size;
    }
  }
  
  protected static final class Triangle extends DebugObjectWithWidth {
    Point3f v0;
    Point3f v1;
    Point3f v2;
    
    Triangle(Point3f _v0, Point3f _v1, Point3f _v2,
        final float duration, final boolean depthEnabled, final Color3f color, final float lineWidth) {
      super(duration, depthEnabled, color, lineWidth);
      v0 = new Point3f(_v0);
      v1 = new Point3f(_v1);
      v2 = new Point3f(_v2);
    }
  }
  
  protected static final class AABB extends DebugObjectWithWidth {
    Point3f minCoords;
    Point3f maxCoords;
    
    AABB(Point3f _minCoords, Point3f _maxCoords,
        final float duration, final boolean depthEnabled, final Color3f color, final float lineWidth) {
      super(duration, depthEnabled, color, lineWidth);
      minCoords = new Point3f(_minCoords);
      maxCoords = new Point3f(_maxCoords);
    }
  }
  
  protected static final class OBB extends DebugObjectWithWidth {
    Matrix4f centerTransformation;
    Tuple3f scaleXYZ;
    
    OBB(final Matrix4f _centerTransformation, final Tuple3f _scaleXYZ,
        final float duration, final boolean depthEnabled, final Color3f color, final float lineWidth) {
      super(duration, depthEnabled, color, lineWidth);
      centerTransformation = new Matrix4f(_centerTransformation);
      scaleXYZ = new Point3f(_scaleXYZ);
    }
  }
  
  protected static final class DebugString extends DebugObject {
    Point3f position;
    String text;
    
    DebugString(final Point3f _position, final String _text,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      position = new Point3f( _position );
      text = _text;
    }
  }
}
