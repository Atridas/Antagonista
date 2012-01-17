package cat.atridas.antagonista.graphics;

import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.core.Core;

public abstract class DebugRender {
  
  private boolean active;

  protected final ArrayList<Line>        lines = new ArrayList<>();
  protected final ArrayList<Cross>       crosses = new ArrayList<>();
  protected final ArrayList<Sphere>      spheres = new ArrayList<>();
  protected final ArrayList<Circle>      circles = new ArrayList<>();
  protected final ArrayList<Axes>        axes = new ArrayList<>();
  protected final ArrayList<Triangle>    triangles = new ArrayList<>();
  protected final ArrayList<AABB>        aabbs = new ArrayList<>();
  protected final ArrayList<OBB>         obbs = new ArrayList<>();
  protected final ArrayList<DebugString> strings = new ArrayList<>();

  protected Material debugMaterial;
  
  {
    debugMaterial = Core.getCore().getMaterialManager().getResource(Utils.DEBUG_MATERIAL_NAME);
  }

  
  public void activate(boolean _active) {
    active = _active;
  }
  
  public boolean isActive() {
    return active;
  }
  
  public void activate() {
    active = true;
  }
  
  public void deactivate() {
    active = false;
  }
  
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      lines.add(new Line(origin, destination, duration, depthEnabled, color));
  }
  
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      crosses.add(new Cross(center, size, duration, depthEnabled, color));
  }
  
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      spheres.add( new Sphere(center, radius, duration, depthEnabled, color));
  }
  
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      circles.add( new Circle(center, radius, planeNormal, duration, depthEnabled, color));
  }
  
  public void addAxes( 
      Matrix4f transformation,
      float size,
      float duration, 
      boolean depthEnabled) {
    if(active)
      axes.add(new Axes(transformation, size, duration, depthEnabled));
  }
  
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color,
      float duration, 
      boolean depthEnabled) {
    if(active)
      triangles.add(new Triangle(v0, v1, v2, duration, depthEnabled, color));
  }
  
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      aabbs.add(new AABB(minCoords, maxCoords, duration, depthEnabled, color));
  }
  
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,  
      float duration, 
      boolean depthEnabled) {
    if(active)
      obbs.add(new OBB(centerTransformation, scaleXYZ, duration, depthEnabled, color));
  }
  
  public void addString( 
      Point3f position,
      String text,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      strings.add(new DebugString(position, text, duration, depthEnabled, color));
  }
  
  
  
  

  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float duration 
      ) {
    addLine(origin,destination,color,duration,true);
  }
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      boolean depthEnabled
      ) {
    addLine(origin,destination,color,0,depthEnabled);
  }
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color) {
    addLine(origin,destination,color,0,true);
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
      float size, 
      boolean depthEnabled) {
    addCross(center,color,size,0,depthEnabled);
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
      Color3f color, 
      boolean depthEnabled) {
    addSphere(center,radius,color,0,depthEnabled);
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
      Color3f color, 
      boolean depthEnabled) {
    addCircle(center, planeNormal, radius, color, 0, depthEnabled);
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
      float size,
      float duration) {
    addAxes(transformation, size, duration, true);
  }
  public void addAxes( 
      Matrix4f transformation,
      float size,
      boolean depthEnabled) {
    addAxes(transformation, size, 0, depthEnabled);
  }
  public void addAxes( 
      Matrix4f transformation,
      float size) {
    addAxes(transformation, size, 0, true);
  }
  

  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      float duration) {
    addTriangle(v0, v1, v2, color, duration, true);
  }
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      boolean depthEnabled) {
    addTriangle(v0, v1, v2, color, 0, depthEnabled);
  }
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color) {
    addTriangle(v0, v1, v2, color, 0, true);
  }
  

  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color,  
      float duration) {
    addAABB(minCoords, maxCoords, color, duration, true);
  }
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color,  
      boolean depthEnabled) {
    addAABB(minCoords, maxCoords, color, 0, depthEnabled);
  }
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color) {
    addAABB(minCoords, maxCoords, color, 0, true);
  }
  

  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,
      float duration) {
    addOBB( centerTransformation, scaleXYZ, color, duration, true);
  }
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,
      boolean depthEnabled) {
    addOBB( centerTransformation, scaleXYZ, color, 0, depthEnabled);
  }
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color) {
    addOBB( centerTransformation, scaleXYZ, color, 0, true);
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
      Color3f color, 
      boolean depthEnabled) {
    addString(position, text, color, 0, depthEnabled);
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
  
  
  public void render(RenderManager rm, float dt) {
    beginRender(rm);
    renderLines(rm);
    renderCrosses(rm);
    renderSpheres(rm);
    renderCircles(rm);
    renderAxes(rm);
    renderTriangles(rm);
    renderBBs(rm);
    renderStrings(rm);
    endRender();

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

  protected abstract void beginRender(RenderManager rm);
  protected abstract void endRender();

  protected abstract void renderLines(RenderManager rm);
  protected abstract void renderCrosses(RenderManager rm);
  protected abstract void renderSpheres(RenderManager rm);
  protected abstract void renderCircles(RenderManager rm);
  protected abstract void renderAxes(RenderManager rm);
  protected abstract void renderTriangles(RenderManager rm);
  protected abstract void renderBBs(RenderManager rm);
  protected abstract void renderStrings(RenderManager rm);
  
  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////


  protected static class DebugObject {
    public float duration;
    public boolean depthEnabled;

    DebugObject(final float _duration, final boolean _depthEnabled) {
      duration = _duration;
      depthEnabled = _depthEnabled;
    }
  }

  protected static class DebugObjectWithColor extends DebugObject {
    public Color3f color;

    DebugObjectWithColor(final float _duration, final boolean _depthEnabled, final Color3f _color) {
      super(_duration, _depthEnabled);
      color = new Color3f(_color);
    }
  }
  
  protected static final class Line extends DebugObjectWithColor {
    public Point3f origin;
    public Point3f destination;
    
    Line(Point3f _origin, Point3f _destination,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      origin= new Point3f( _origin );
      destination = new Point3f( _destination );
    }
  }
  
  protected static final class Cross extends DebugObjectWithColor {
    public Point3f center;
    public float size;
    
    Cross(Point3f _center, float _size,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      size = _size;
    }
  }
  
  protected static final class Sphere extends DebugObjectWithColor {
    public Point3f center;
    public float radius;
    
    Sphere(Point3f _center, float _radius,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      radius = _radius;
    }
  }
  
  protected static final class Circle extends DebugObjectWithColor {
    public Point3f center;
    public Vector3f planeNormal;
    public float radius;
    
    Circle(Point3f _center, float _radius, Vector3f _planeNormal,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center = new Point3f ( _center );
      radius = _radius ;
      
      assert Math.abs(_planeNormal.lengthSquared() - 1) < Utils.EPSILON;
      
      planeNormal = new Vector3f();
      planeNormal.normalize(_planeNormal);
    }
  }
  
  protected static final class Axes extends DebugObject {
    public Matrix4f transformation;
    public float size;
    
    Axes(Matrix4f _transformation, float _size,
        final float duration, final boolean depthEnabled) {
      super(duration, depthEnabled);      
      transformation = new Matrix4f( _transformation );
      size = _size;
    }
  }
  
  protected static final class Triangle extends DebugObjectWithColor {
    public Point3f v0;
    public Point3f v1;
    public Point3f v2;
    
    Triangle(Point3f _v0, Point3f _v1, Point3f _v2,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      v0 = new Point3f(_v0);
      v1 = new Point3f(_v1);
      v2 = new Point3f(_v2);
    }
  }
  
  protected static final class AABB extends DebugObjectWithColor {
    public Point3f minCoords;
    public Point3f maxCoords;
    
    AABB(Point3f _minCoords, Point3f _maxCoords,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      minCoords = new Point3f(_minCoords);
      maxCoords = new Point3f(_maxCoords);
    }
  }
  
  protected static final class OBB extends DebugObjectWithColor {
    public Matrix4f centerTransformation;
    public Tuple3f scaleXYZ;
    
    OBB(final Matrix4f _centerTransformation, final Tuple3f _scaleXYZ,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      centerTransformation = new Matrix4f(_centerTransformation);
      scaleXYZ = new Point3f(_scaleXYZ);
    }
  }
  
  protected static final class DebugString extends DebugObjectWithColor {
    public Point3f position;
    public String text;
    
    DebugString(final Point3f _position, final String _text,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      position = new Point3f( _position );
      text = _text;
    }
  }
}
