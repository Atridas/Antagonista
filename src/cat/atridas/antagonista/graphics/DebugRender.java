package cat.atridas.antagonista.graphics;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.FontManager.TextAlignment;

/**
 * Class that enqueues debug information to be rendered on the scene during renderization.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class DebugRender {
  
  /**
   * Number of stacks on a sphere.
   * 
   * @since 0.1
   */
  protected final static int SPHERE_STACKS = 15;
  /**
   * Number of longitudinal aristes in a sphere or circle.
   * 
   * @since 0.1
   */
  protected final static int SPHERE_SUBDIV = 15;

  /**
   * Number of floats in a vertex with position and color information.
   * @since 0.1
   */
  protected static final int POS_COL_VERTEX_SIZE = (3 + 3); //Floats
  
  private boolean active;

  /**
   * Lines to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Line>        lines = new ArrayList<>();
  /**
   * Crosses to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Cross>       crosses = new ArrayList<>();
  /**
   * Spheres to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Sphere>      spheres = new ArrayList<>();
  /**
   * Circles to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Circle>      circles = new ArrayList<>();
  /**
   * Axes to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Axes>        axes = new ArrayList<>();
  /**
   * Triangles to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<Triangle>    triangles = new ArrayList<>();
  /**
   * Axis Aligned Bounding Boxes to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<AABB>        aabbs = new ArrayList<>();
  /**
   * Oriented Bounding Boxes to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<OBB>         obbs = new ArrayList<>();
  /**
   * Text to be rendered.
   * 
   * @since 0.1
   */
  protected final ArrayList<DebugString> strings = new ArrayList<>();
  
  /**
   * Material object used to render all debug information but the text. 
   * 
   * @since 0.1
   */
  protected Material debugMaterial;
  
  {
    debugMaterial = Core.getCore().getMaterialManager().getResource(Utils.DEBUG_MATERIAL_NAME);
  }

  /**
   * Activates or deactivates the renderer.
   * 
   * @param _active if this debug render should continue to register new debug information.
   * @since 0.1
   */
  public void activate(boolean _active) {
    active = _active;
  }
  
  /**
   * Checks if the renderer is active.
   * 
   * @return <code>true</code> if the renderer is active, <code>false</code> otherwise.
   * @since 0.1
   */
  public boolean isActive() {
    return active;
  }
  
  /**
   * Activates the renderer. This renderer will begin to register debug information to be rendered.
   * 
   * @since 0.1
   */
  public void activate() {
    active = true;
  }
  
  /**
   * Deactivates the renderer. No more debug information will be registered.
   * 
   * @since 0.1
   */
  public void deactivate() {
    active = false;
  }
  
  /**
   * Adds a line.
   * 
   * @param origin origin of the line.
   * @param destination final point of the line.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      lines.add(new Line(origin, destination, duration, depthEnabled, color));
  }
  
  /**
   * Adds a Cross, that represents a point in 3D space.
   * 
   * @param center point.
   * @param color color of the primitive.
   * @param size radi of the lines exiting from the center point.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      crosses.add(new Cross(center, size, duration, depthEnabled, color));
  }
  
  /**
   * Adds a Sphere.
   * 
   * @param center point.
   * @param radius of the sphere.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      spheres.add( new Sphere(center, radius, duration, depthEnabled, color));
  }
  
  /**
   * Adds a circle.
   * 
   * @param center point.
   * @param planeNormal normal to the circle.
   * @param radius of the circle.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
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
  
  /**
   * Displais a transformation, using a transformed axe. Positive X axe will be red, 
   * Positive Y green and Z will be blue.
   * 
   * @param transformation transformation to display.
   * @param size of the lines exiting from the center point.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addAxes( 
      Matrix4f transformation,
      float size,
      float duration, 
      boolean depthEnabled) {
    if(active)
      axes.add(new Axes(transformation, size, duration, depthEnabled));
  }
  
  /**
   * Adds the edges of a triangle to the scene.
   * 
   * @param v0 first point.
   * @param v1 second point.
   * @param v2 third point.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
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
  
  /**
   * Displays an aligned bounding box.
   * 
   * @param minCoords minimum coordinates
   * @param maxCoords maximim coordinates.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      aabbs.add(new AABB(minCoords, maxCoords, duration, depthEnabled, color));
  }
  
  /**
   * Displays an oriented Bounding Box.
   * 
   * @param centerTransformation transformation to the center of the box.
   * @param scaleXYZ size of the box.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,  
      float duration, 
      boolean depthEnabled) {
    if(active)
      obbs.add(new OBB(centerTransformation, scaleXYZ, duration, depthEnabled, color));
  }
  
  /**
   * Adds a text to a 3D point in space.
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    if(active)
      strings.add(new DebugString(position, font, text, desiredHeight, false, alignment, duration, depthEnabled, color));
  }
  
  /**
   * Adds a text in 2D respect the screen.
   * 
   * @param position Normalized (1 means the entire screen height) position. Positive coordinates 
   *        are taken respect the upper left screen corner and negative from the bottom right.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in normalized units (1 means the entire screen height).
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   */
  public void addString2D( 
      Point2f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color, 
      float duration) {
    if(active)
      strings.add(new DebugString(new Point3f(position.x, position.y, 0), font, text, desiredHeight, true, alignment, duration, false, color));
  }
  
  
  
  
  /**
   * Same as <code>addLine(origin, destination, color, duration, <strong>true</strong>)</code>
   * 
   * @param origin origin of the line.
   * @param destination final point of the line.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addLine(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      float duration 
      ) {
    addLine(origin,destination,color,duration,true);
  }
  /**
   * Same as <code>addLine(origin, destination, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param origin origin of the line.
   * @param destination final point of the line.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addLine(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color, 
      boolean depthEnabled
      ) {
    addLine(origin,destination,color,0,depthEnabled);
  }
  /**
   * Same as <code>addLine(origin, destination, color, <strong>0, true</strong>)</code>
   * 
   * @param origin origin of the line.
   * @param destination final point of the line.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addLine(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addLine( 
      Point3f origin, 
      Point3f destination, 
      Color3f color) {
    addLine(origin,destination,color,0,true);
  }
  

  /**
   * Same as <code>addCross(center, color, size, duration, <strong>true</strong>)</code>
   * 
   * @param center point.
   * @param color color of the primitive.
   * @param size radi of the lines exiting from the center point.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addCross(Point3f, Color3f, float, float, boolean)
   */
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      float duration) {
    addCross(center,color,size,duration,true);
  }
  /**
   * Same as <code>addCross(center, color, size, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param center point.
   * @param color color of the primitive.
   * @param size radi of the lines exiting from the center point.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addCross(Point3f, Color3f, float, float, boolean)
   */
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size, 
      boolean depthEnabled) {
    addCross(center,color,size,0,depthEnabled);
  }
  /**
   * Same as <code>addCross(center, color, size, <strong>0, true</strong>)</code>
   * 
   * @param center point.
   * @param color color of the primitive.
   * @param size radi of the lines exiting from the center point.
   * @since 0.1
   * @see #addCross(Point3f, Color3f, float, float, boolean)
   */
  public void addCross( 
      Point3f center,
      Color3f color, 
      float size) {
    addCross(center,color,size,0,true);
  }
  

  /**
   * Same as <code>addSphere(center, radius, color, duration, <strong>true</strong>)</code>
   * 
   * @param center point.
   * @param radius of the sphere.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addSphere(Point3f, float, Color3f, float, boolean)
   */
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      float duration) {
    addSphere(center,radius,color,duration,true);
  }
  /**
   * Same as <code>addSphere(center, radius, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param center point.
   * @param radius of the sphere.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addSphere(Point3f, float, Color3f, float, boolean)
   */
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color, 
      boolean depthEnabled) {
    addSphere(center,radius,color,0,depthEnabled);
  }
  /**
   * Same as <code>addSphere(center, radius, color, <strong>0, true</strong>)</code>
   * 
   * @param center point.
   * @param radius of the sphere.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addSphere(Point3f, float, Color3f, float, boolean)
   */
  public void addSphere( 
      Point3f center,
      float radius,
      Color3f color) {
    addSphere(center,radius,color,0,true);
  }

  /**
   * Same as <code>addCircle(center, planeNormal, radius, color, duration, <strong>true</strong>)</code>
   * 
   * @param center point.
   * @param planeNormal normal to the circle.
   * @param radius of the circle.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addCircle(Point3f, Vector3f, float, Color3f, float, boolean)
   */
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color, 
      float duration) {
    addCircle(center, planeNormal, radius, color, duration, true);
  }
  /**
   * Same as <code>addCircle(center, planeNormal, radius, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param center point.
   * @param planeNormal normal to the circle.
   * @param radius of the circle.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addCircle(Point3f, Vector3f, float, Color3f, float, boolean)
   */
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color, 
      boolean depthEnabled) {
    addCircle(center, planeNormal, radius, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addCircle(center, planeNormal, radius, color, <strong>0, true</strong>)</code>
   * 
   * @param center point.
   * @param planeNormal normal to the circle.
   * @param radius of the circle.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addCircle(Point3f, Vector3f, float, Color3f, float, boolean)
   */
  public void addCircle( 
      Point3f center, 
      Vector3f planeNormal,
      float radius,
      Color3f color) {
    addCircle(center, planeNormal, radius, color, 0, true);
  }
  

  /**
   * Same as <code>addAxes(transformation, size, duration, <strong>true</strong>)</code>
   * 
   * @param transformation transformation to display.
   * @param size of the lines exiting from the center point.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addAxes(Matrix4f, float, float, boolean)
   */
  public void addAxes( 
      Matrix4f transformation,
      float size,
      float duration) {
    addAxes(transformation, size, duration, true);
  }
  /**
   * Same as <code>addAxes(transformation, size, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param transformation transformation to display.
   * @param size of the lines exiting from the center point.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addAxes(Matrix4f, float, float, boolean)
   */
  public void addAxes( 
      Matrix4f transformation,
      float size,
      boolean depthEnabled) {
    addAxes(transformation, size, 0, depthEnabled);
  }
  /**
   * Same as <code>addAxes(transformation, size, <strong>0, true</strong>)</code>
   * 
   * @param transformation transformation to display.
   * @param size of the lines exiting from the center point.
   * @since 0.1
   * @see #addAxes(Matrix4f, float, float, boolean)
   */
  public void addAxes( 
      Matrix4f transformation,
      float size) {
    addAxes(transformation, size, 0, true);
  }
  

  /**
   * Same as <code>addTriangle(v0, v1, v2, color, duration, <strong>true</strong>)</code>
   * 
   * @param v0 first point.
   * @param v1 second point.
   * @param v2 third point.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addTriangle(Point3f, Point3f, Point3f, Color3f, float, boolean)
   */
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      float duration) {
    addTriangle(v0, v1, v2, color, duration, true);
  }
  /**
   * Same as <code>addTriangle(v0, v1, v2, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param v0 first point.
   * @param v1 second point.
   * @param v2 third point.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addTriangle(Point3f, Point3f, Point3f, Color3f, float, boolean)
   */
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color, 
      boolean depthEnabled) {
    addTriangle(v0, v1, v2, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addTriangle(v0, v1, v2, color, <strong>0, true</strong>)</code>
   * 
   * @param v0 first point.
   * @param v1 second point.
   * @param v2 third point.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addTriangle(Point3f, Point3f, Point3f, Color3f, float, boolean)
   */
  public void addTriangle( 
      Point3f v0, 
      Point3f v1, 
      Point3f v2,
      Color3f color) {
    addTriangle(v0, v1, v2, color, 0, true);
  }
  

  /**
   * Same as <code>addAABB(minCoords, maxCoords, color, duration, <strong>true</strong>)</code>
   * 
   * @param minCoords minimum coordinates
   * @param maxCoords maximim coordinates.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addAABB(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color,  
      float duration) {
    addAABB(minCoords, maxCoords, color, duration, true);
  }
  /**
   * Same as <code>addAABB(minCoords, maxCoords, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param minCoords minimum coordinates
   * @param maxCoords maximim coordinates.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addAABB(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color,  
      boolean depthEnabled) {
    addAABB(minCoords, maxCoords, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addAABB(minCoords, maxCoords, color, <strong>0, true</strong>)</code>
   * 
   * @param minCoords minimum coordinates
   * @param maxCoords maximim coordinates.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addAABB(Point3f, Point3f, Color3f, float, boolean)
   */
  public void addAABB( 
      Point3f minCoords, 
      Point3f maxCoords, 
      Color3f color) {
    addAABB(minCoords, maxCoords, color, 0, true);
  }
  

  /**
   * Same as <code>addOBB( centerTransformation, scaleXYZ, color, duration, <strong>true</strong>)</code>
   * 
   * @param centerTransformation transformation to the center of the box.
   * @param scaleXYZ size of the box.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addOBB(Matrix4f, Tuple3f, Color3f, float, boolean)
   */
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,
      float duration) {
    addOBB( centerTransformation, scaleXYZ, color, duration, true);
  }
  /**
   * Same as <code>addOBB( centerTransformation, scaleXYZ, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param centerTransformation transformation to the center of the box.
   * @param scaleXYZ size of the box.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addOBB(Matrix4f, Tuple3f, Color3f, float, boolean)
   */
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color,
      boolean depthEnabled) {
    addOBB( centerTransformation, scaleXYZ, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addOBB( centerTransformation, scaleXYZ, color, <strong>0, true</strong>)</code>
   * 
   * @param centerTransformation transformation to the center of the box.
   * @param scaleXYZ size of the box.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addOBB(Matrix4f, Tuple3f, Color3f, float, boolean)
   */
  public void addOBB( 
      Matrix4f centerTransformation,
      Tuple3f scaleXYZ, 
      Color3f color) {
    addOBB( centerTransformation, scaleXYZ, color, 0, true);
  }

  /**
   * Same as <code>addString(position, font, text, desiredHeight, alignment, color, duration, <strong>true</strong>)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color, 
      float duration) {
    addString(position, font, text, desiredHeight, alignment, color, duration, true);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, alignment, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color, 
      boolean depthEnabled) {
    addString(position, font, text, desiredHeight, alignment, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, alignment, color, <strong>0, true</strong>)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color) {
    addString(position, font, text, desiredHeight, alignment, color, 0, true);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, <strong>MID_CENTER</strong>, color, duration, depthEnabled)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color, 
      float duration, 
      boolean depthEnabled) {
    addString(position, font, text, desiredHeight, TextAlignment.MID_CENTER, color, duration, depthEnabled);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, <strong>MID_CENTER</strong>, color, duration, <strong>true</strong>)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color, 
      float duration) {
    addString(position, font, text, desiredHeight, TextAlignment.MID_CENTER, color, duration, true);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, <strong>MID_CENTER</strong>, color, <strong>0</strong>, depthEnabled)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param color color of the primitive.
   * @param depthEnabled <code>true</code> if this primitive should be depth tested, 
   *        <code>false</code> otherwise.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color, 
      boolean depthEnabled) {
    addString(position, font, text, desiredHeight, TextAlignment.MID_CENTER, color, 0, depthEnabled);
  }
  /**
   * Same as <code>addString(position, font, text, desiredHeight, <strong>MID_CENTER</strong>, color, <strong>0, true</strong>)</code>
   * 
   * @param position of the text in 3D space.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in engine units of the text.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addString(Point3f, Font, String, float, TextAlignment, Color3f, float, boolean)
   */
  public void addString( 
      Point3f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color) {
    addString(position, font, text, desiredHeight, TextAlignment.MID_CENTER, color, 0, true);
  }

  /**
   * Same as <code>addString2D(position, font, text, desiredHeight, alignment, color, <strong>0</strong>)</code>
   * 
   * @param position Normalized (1 means the entire screen height) position. Positive coordinates 
   *        are taken respect the upper left screen corner and negative from the bottom right.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in normalized units (1 means the entire screen height).
   * @param alignment of the text respect the point passed.
   * @param color color of the primitive.
   * @since 0.1
   * @see #addString2D(Point2f, Font, String, float, TextAlignment, Color3f, float)
   */
  public void addString2D( 
      Point2f position,
      Font font,
      String text,
      float desiredHeight,
      TextAlignment alignment,
      Color3f color) {
    addString2D(position, font, text, desiredHeight, alignment, color, 0);
  }
  /**
   * Same as <code>addString2D(position, font, text, desiredHeight, <strong>TOP_LEFT</strong>, color, duration)</code>
   * 
   * @param position Normalized (1 means the entire screen height) position. Positive coordinates 
   *        are taken respect the upper left screen corner and negative from the bottom right.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in normalized units (1 means the entire screen height).
   * @param color color of the primitive.
   * @param duration time, in seconds, this information will be rendered.
   * @since 0.1
   * @see #addString2D(Point2f, Font, String, float, TextAlignment, Color3f, float)
   */
  public void addString2D( 
      Point2f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color, 
      float duration) {
    addString2D(position, font, text, desiredHeight, TextAlignment.TOP_LEFT, color, duration);
  }
  /**
   * Same as <code>addString2D(position, font, text, desiredHeight, <strong>TOP_LEFT</strong>, color, <strong>0</strong>)</code>
   * 
   * @param position Normalized (1 means the entire screen height) position. Positive coordinates 
   *        are taken respect the upper left screen corner and negative from the bottom right.
   * @param font used in the text.
   * @param text to display.
   * @param desiredHeight height in normalized units (1 means the entire screen height).
   * @param color color of the primitive.
   * @since 0.1
   * @see #addString2D(Point2f, Font, String, float, TextAlignment, Color3f, float)
   */
  public void addString2D( 
      Point2f position,
      Font font,
      String text,
      float desiredHeight,
      Color3f color) {
    addString2D(position, font, text, desiredHeight, TextAlignment.TOP_LEFT, color, 0);
  }
  

  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Renders all debug information. After rendering them, cleans those whose time has finished, so
   * every primitive is rendered for at least one frame.
   * 
   * @param rm Render Manager.
   * @param dt time elapsed since the last call.
   */
  public void render(RenderManager rm, DeltaTime dt) {
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

    cleanList(lines, dt.dt);
    cleanList(crosses, dt.dt);
    cleanList(spheres, dt.dt);
    cleanList(circles, dt.dt);
    cleanList(axes, dt.dt);
    cleanList(triangles, dt.dt);
    cleanList(aabbs, dt.dt);
    cleanList(obbs, dt.dt);
    cleanList(strings, dt.dt);
  }
  
  /**
   * From a list of DebugObjects, cleans all whose time has explired.
   * 
   * @param list
   * @param dt
   */
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

  /**
   * Called before any other render method.
   * 
   * @param rm Render Manager
   * @since 0.1
   * 
   * @see #renderLines(RenderManager)
   * @see #renderCrosses(RenderManager)
   * @see #renderSpheres(RenderManager)
   * @see #renderSpheres(RenderManager)
   * @see #renderCircles(RenderManager)
   * @see #renderAxes(RenderManager)
   * @see #renderTriangles(RenderManager)
   * @see #renderBBs(RenderManager)
   */
  protected abstract void beginRender(RenderManager rm);

  /**
   * Called after any other render method.
   * 
   * @since 0.1
   * 
   * @see #renderLines(RenderManager)
   * @see #renderCrosses(RenderManager)
   * @see #renderSpheres(RenderManager)
   * @see #renderSpheres(RenderManager)
   * @see #renderCircles(RenderManager)
   * @see #renderAxes(RenderManager)
   * @see #renderTriangles(RenderManager)
   * @see #renderBBs(RenderManager)
   */
  protected abstract void endRender();

  /**
   * Overwrite and implement how to render all lines.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #lines
   */
  protected abstract void renderLines(RenderManager rm);
  /**
   * Overwrite and implement how to render all crosses.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #crosses
   */
  protected abstract void renderCrosses(RenderManager rm);
  /**
   * Overwrite and implement how to render all spheres.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #spheres
   */
  protected abstract void renderSpheres(RenderManager rm);
  /**
   * Overwrite and implement how to render all circles.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #circles
   */
  protected abstract void renderCircles(RenderManager rm);
  /**
   * Overwrite and implement how to render all axes.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #axes
   */
  protected abstract void renderAxes(RenderManager rm);
  /**
   * Overwrite and implement how to render all triangles.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #triangles
   */
  protected abstract void renderTriangles(RenderManager rm);
  /**
   * Overwrite and implement how to render all bounding boxes.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #aabb
   * @see #obb
   */
  protected abstract void renderBBs(RenderManager rm);
  
  
  


  /**
   * Renders all bounding strings, using the Font Manager.
   * 
   * @param rm Render Manager
   * 
   * @since 0.1
   * 
   * @see #strings
   * @see FontManager
   */
  private final void renderStrings(RenderManager rm) {
    
    if(strings.size() == 0)
      return;
    
    FontManager fm = Core.getCore().getFontManager();
    
    //////////////////////////////////////////////
    Vector3f v3Aux  = new Vector3f();
    Point3f  p3CameraPos = new Point3f();
    Quat4f   qAux  = new Quat4f();

    Matrix4f orthoProj          = new Matrix4f();
    Matrix4f viewProj           = new Matrix4f();
    Matrix4f model              = new Matrix4f();
    Matrix4f modelViewProj      = new Matrix4f();
    orthoProj.setIdentity();
    viewProj.setIdentity();
    
    //rm.getSceneData().getViewMatrix(view);
    rm.getSceneData().getViewProjectionMatrix(viewProj);
    rm.getSceneData().getCameraPosition(p3CameraPos);
    float normalizedWidth = rm.getAspectRatio();
    SceneData.getOrtho(0, normalizedWidth, 0, 1, -1, 1, orthoProj);
    ///////////////////////////////////////////////

    assert Utils.V3_MINUS_Z.x == 0 && Utils.V3_MINUS_Z.y == 0 && Utils.V3_MINUS_Z.z == -1;
    assert Utils.V3_Z.x == 0 && Utils.V3_Z.y == 0 && Utils.V3_Z.z == 1;
    
    Vector3f cameraUpVector = new Vector3f();
    rm.getSceneData().getCameraUpVector(cameraUpVector);
    
    for(DebugString text : strings) {
      
      rm.setDepthTest(text.depthEnabled);

      if(text.on2D) {
        model.setIdentity();
        v3Aux.x = (Utils.isNegative(text.position.x))? normalizedWidth + text.position.x : text.position.x;
        v3Aux.y = (Utils.isNegative(text.position.y))? 1.f             + text.position.y : text.position.y;
        v3Aux.z = 0;
        model.setTranslation(v3Aux);
        model.setScale(text.desiredHeight / text.font.getLineHeight());
        
        
        modelViewProj.mul(orthoProj, model);
      } else {
        model.setIdentity();
        v3Aux.set(text.position);
        model.setTranslation(v3Aux);
        model.setScale(text.desiredHeight / text.font.getLineHeight());
        
        v3Aux.sub(p3CameraPos, text.position);
        v3Aux.normalize();
        Utils.getClosestRotation(Utils.V3_MINUS_Z, Utils.V3_MINUS_Y, v3Aux, cameraUpVector, qAux);
        model.setRotation(qAux);
  
        
        modelViewProj.mul(viewProj, model);
      }
      
      fm.printString(text.font, text.text, text.color, modelViewProj, text.alignment, rm);
    }
  }
  
  
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Base class of all debug objects.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static class DebugObject {
    /**
     * Time left until this object will be erased.
     * @since 0.1
     */
    public float duration;
    /**
     * If this object should or should not be depth culled.
     * @since 0.1
     */
    public final boolean depthEnabled;

    /**
     * Default constructor.
     * 
     * @param _duration
     * @param _depthEnabled
     * @since 0.1
     */
    DebugObject(final float _duration, final boolean _depthEnabled) {
      duration = _duration;
      depthEnabled = _depthEnabled;
    }
  }

  /**
   * Specialization with a color.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static class DebugObjectWithColor extends DebugObject {
    /**
     * Color this debug object will be rendered.
     * @since 0.1
     */
    public final Color3f color;

    /**
     * Default constructor.
     * 
     * @param _duration
     * @param _depthEnabled
     * @param _color
     * @since 0.1
     */
    DebugObjectWithColor(final float _duration, final boolean _depthEnabled, final Color3f _color) {
      super(_duration, _depthEnabled);
      color = new Color3f(_color);
    }
  }
  
  /**
   * Represents a line that goes from one point to another.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Line extends DebugObjectWithColor {
    /**
     * First point of the line.
     * @since 0.1
     */
    public final Point3f origin;
    /**
     * Last point of the line.
     * @since 0.1
     */
    public final Point3f destination;
    
    /**
     * Default constructor.
     * 
     * @param _origin
     * @param _destination
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    Line(Point3f _origin, Point3f _destination,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      origin= new Point3f( _origin );
      destination = new Point3f( _destination );
    }
  }
  
  /**
   * Represents a cross around one point.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Cross extends DebugObjectWithColor {
    /**
     * Center point of that cross.
     * @since 0.1
     */
    public final Point3f center;
    /**
     * Size from the center to the end of the cross.
     * @since 0.1
     */
    public final float size;
    
    /**
     * Default constructor.
     * 
     * @param _center
     * @param _size
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    Cross(Point3f _center, float _size,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      size = _size;
    }
  }
  
  /**
   * Represents a sphere.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Sphere extends DebugObjectWithColor {
    /**
     * Center of the sphere.
     * @since 0.1
     */
    public final Point3f center;
    /**
     * Radius of the sphere.
     * @since 0.1
     */
    public final float radius;
    
    /**
     * Default constructor.
     * 
     * @param _center
     * @param _radius
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    Sphere(Point3f _center, float _radius,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      center= new Point3f( _center );
      radius = _radius;
    }
  }
  
  /**
   * Represents a circle.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Circle extends DebugObjectWithColor {
    /**
     * Center of the circle.
     * @since 0.1
     */
    public final Point3f center;
    /**
     * Normal of the plane where the circle will be rendered.
     * @since 0.1
     */
    public final Vector3f planeNormal;
    /**
     * Radius of the circle.
     * @since 0.1
     */
    public final float radius;
    
    /**
     * Default constructor.
     * 
     * @param _center
     * @param _radius
     * @param _planeNormal
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
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
  
  /**
   * Represent a set of transformed axes.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Axes extends DebugObject {
    /**
     * Transformation aplied to said axes.
     * @since 0.1
     */
    public final Matrix4f transformation;
    /**
     * Size of each axe.
     * @since 0.1
     */
    public final float size;
    
    /**
     * Default Constructor.
     * 
     * @param _transformation
     * @param _size
     * @param duration
     * @param depthEnabled
     * @since 0.1
     */
    Axes(Matrix4f _transformation, float _size,
        final float duration, final boolean depthEnabled) {
      super(duration, depthEnabled);      
      transformation = new Matrix4f( _transformation );
      size = _size;
    }
  }
  
  /**
   * Represents a wireframe triangle.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class Triangle extends DebugObjectWithColor {
    /**
     * Point of the triangle.
     * @since 0.1
     */
    public final Point3f v0;
    /**
     * Point of the triangle.
     * @since 0.1
     */
    public final Point3f v1;
    /**
     * Point of the triangle.
     * @since 0.1
     */
    public final Point3f v2;
    
    /**
     * Default Constructor.
     * 
     * @param _v0
     * @param _v1
     * @param _v2
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    Triangle(Point3f _v0, Point3f _v1, Point3f _v2,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      v0 = new Point3f(_v0);
      v1 = new Point3f(_v1);
      v2 = new Point3f(_v2);
    }
  }
  
  /**
   * Represents an axis aligned bounding box.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   */
  protected static final class AABB extends DebugObjectWithColor {
    /**
     * Coordinates to the lower left close corner.
     * @since 0.1
     */
    public final Point3f minCoords;
    /**
     * Coordinates of the upper right far corner.
     * @since 0.1
     */
    public final Point3f maxCoords;
    
    /**
     * Default constructor.
     * 
     * @param _minCoords
     * @param _maxCoords
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    AABB(Point3f _minCoords, Point3f _maxCoords,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      minCoords = new Point3f(_minCoords);
      maxCoords = new Point3f(_maxCoords);
    }
  }
  
  /**
   * Represents an oriented bounding box.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class OBB extends DebugObjectWithColor {
    /**
     * Transformation from the center of the scene to the center of the box.
     * @since 0.1
     */
    public final Matrix4f centerTransformation;
    /**
     * Size of each box side.
     * @since 0.1
     */
    public final Tuple3f scaleXYZ;
    
    /**
     * Default Constructor.
     * 
     * @param _centerTransformation
     * @param _scaleXYZ
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    OBB(final Matrix4f _centerTransformation, final Tuple3f _scaleXYZ,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      centerTransformation = new Matrix4f(_centerTransformation);
      scaleXYZ = new Point3f(_scaleXYZ);
    }
  }
  
  /**
   * Represents a text rendered to the scene or the screen.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
  protected static final class DebugString extends DebugObjectWithColor {
    /**
     * Position of the text. With 3D coordinates if it is a 3D text, and 2D normalized
     * coordinates if it is a screen text.
     * @since 0.1
     */
    public final Point3f position;
    /**
     * Font used to render the text.
     * @since 0.1
     */
    public final Font font;
    /**
     * Text to render.
     * @since 0.1
     */
    public final String text;
    /**
     * If this text is on the 3D scene (<code>false</code>) or on the screen (<code>true</code>)
     * @since 0.1
     */
    public final boolean on2D;
    /**
     * Alignment of the text.
     * @since 0.1
     */
    public final TextAlignment alignment;
    /**
     * Height of the text. In engine units if 3D, in normalized units if 2D.
     * @since 0.1
     */
    public final float desiredHeight;
    
    /**
     * Default Constructor.
     * 
     * @param _position
     * @param _font
     * @param _text
     * @param _desiredHeight
     * @param _on2D
     * @param _aligment
     * @param duration
     * @param depthEnabled
     * @param color
     * @since 0.1
     */
    DebugString(final Point3f _position, final Font _font, final String _text, final float _desiredHeight,
        final boolean _on2D, final TextAlignment _aligment,
        final float duration, final boolean depthEnabled, final Color3f color) {
      super(duration, depthEnabled, color);
      position = new Point3f( _position );
      font = _font;
      text = _text;
      desiredHeight = _desiredHeight;
      on2D = _on2D;
      alignment = _aligment;
    }
  }

  /**
   * Num indices a LINE_STRIPE sphere will have.
   * 
   * @since 0.1
   */
  protected int sphereNumIndices;
  /**
   * Num vertexes a LINE_LOOP circle will have.
   * 
   * @since 0.1
   */
  protected int circlesNumVertexs;
  /**
   * Num indices a LINE bounding box will have.
   * 
   * @since 0.1
   */
  protected int bbNumIndices;
  /**
   * Num vertexes a LINE crosses will have.
   * 
   * @since 0.1
   */
  protected static final int crossesNumVertexs = 6;
  /**
   * Num vertexes a LINE axe will have.
   * 
   * @since 0.1
   */
  protected static final int axesNumVertexs = 6;
  
  /**
   * Creates the vertex buffer to render a cross.
   * 
   * @return that vertex buffer.
   * @since 0.1
   */
  protected final FloatBuffer createCrossVertexBuffer() {
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add(1.f);
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(-1.f);
    vertices.add(0.f);
    vertices.add(0.f);

    
    vertices.add(0.f);
    vertices.add(1.f);
    vertices.add(0.f);
    
    vertices.add(0.f);
    vertices.add(-1.f);
    vertices.add(0.f);

    
    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(1.f);
    
    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(-1.f);
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    
    return vertexBuffer;
  }
  
  /**
   * Creates a vertex buffer for a sphere.
   * 
   * @return the vertex buffer.
   * @since 0.1
   */
  protected final FloatBuffer createSphereVertexBuffer() {
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add( 1.f); //top
    
    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add(-1.f); //botom
    
    for(int i = 1; i < SPHERE_STACKS; i++) {
      float z = i / (SPHERE_STACKS/2.f) - 1;
      for(int j = 0; j < SPHERE_SUBDIV; ++j) {
        float len = (float)Math.sqrt(1 - z*z);
        float x = (float) Math.sin( j * Math.PI * 2 / SPHERE_SUBDIV) * len;
        float y = (float) Math.cos( j * Math.PI * 2 / SPHERE_SUBDIV) * len;
        

        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
      }
    }
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    
    return vertexBuffer;
  }

  /**
   * Creates a index buffer of a sphere.
   * 
   * @return the index buffer.
   * @since 0.1
   */
  protected final ShortBuffer createSphereIndexBuffer() {

    ArrayList<Short> indexes = new ArrayList<>();
    
    //part de sota
    indexes.add((short)1);
    for(short j = 0; j < SPHERE_SUBDIV - 1; j += 2) {
      indexes.add( (short)( 2 + j    ) );
      indexes.add( (short)( 2 + j +1 ) );
      indexes.add((short)1);
    }
    short baseStack;
    
    //paral·lels
    for(short i = 1; i < SPHERE_STACKS; i++) {
      baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
      for(short j = 0; j < SPHERE_SUBDIV -1; ++j) {
        indexes.add( (short)( baseStack + j    ) );
      }
      indexes.add( (short)( baseStack ) );
    }


    //part de sobre
    baseStack = (short)( (SPHERE_STACKS-2) * SPHERE_SUBDIV + 2 );
    indexes.add((short)0);
    for(short j = 0; j < SPHERE_SUBDIV - 1; j += 2) {
      indexes.add( (short)( baseStack + j    ) );
      indexes.add( (short)( baseStack + j +1 ) );
      indexes.add((short)0);
    }
    
    //meridians
    for(short j = 1; j < SPHERE_SUBDIV; ++j) {
      if(j % 2 == 1) {
        //de dalt a baix
        //indexes.add( (short)( 1 ) );
        for(short i = SPHERE_STACKS-1; i > 0; i--) {
          baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
          indexes.add( (short)( baseStack + j    ) );
        }
        indexes.add( (short)( 1 ) );
      } else {
        // de baix a dalt
        //indexes.add( (short)( 0 ) );
        for(short i = 1; i < SPHERE_STACKS; i++) {
          baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
          indexes.add( (short)( baseStack + j    ) );
        }
        indexes.add( (short)( 0 ) );
      }
    }
    
    //////////////////////////////////////////////////////////////
    Short saux1[] = indexes.toArray(new Short[indexes.size()]);
    short saux2[] = new short[saux1.length];
    for(int i = 0; i < saux1.length; i++) {
      saux2[i] = saux1[i];
    }
    saux1 = null;
    indexes = null;
    //////////////////////////////////////////////////////////////
    

    ShortBuffer indexBuffer = BufferUtils.createShortBuffer(saux2.length);
    sphereNumIndices = saux2.length;
    indexBuffer.put(saux2);
    indexBuffer.flip();
    
    return indexBuffer;
  }

  /**
   * Creates a vertex buffer to render a circle.
   * 
   * @return the vertex buffer.
   * @since 0.1
   */
  protected final FloatBuffer createCircleVertexBuffer() {
    ArrayList<Float> vertices = new ArrayList<>();

    for(int j = 0; j < SPHERE_SUBDIV; ++j) {
      float x = (float) Math.sin( j * Math.PI * 2 / SPHERE_SUBDIV);
      float y = (float) Math.cos( j * Math.PI * 2 / SPHERE_SUBDIV);
      

      vertices.add(x);
      vertices.add(y);
      vertices.add(0.f);
    }
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    circlesNumVertexs = faux1.length / 3;
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    
    return vertexBuffer;
  }

  /**
   * Creates a vertex buffer to render an axe.
   * 
   * @return the vertex buffer.
   * @since 0.1
   */
  protected final FloatBuffer createAxesVertexBuffer() {

    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(1.f); //color
    vertices.add(0.f);
    vertices.add(0.f);

    vertices.add(1.f);
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(1.f); //color
    vertices.add(0.f);
    vertices.add(0.f);

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(1.f);
    vertices.add(0.f);

    vertices.add(0.f);
    vertices.add(1.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(1.f);
    vertices.add(0.f);

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(0.f);
    vertices.add(1.f);

    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(1.f);
    
    vertices.add(0.f); //color
    vertices.add(0.f);
    vertices.add(1.f);
    
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    
    return vertexBuffer;
  }
  
  /**
   * Creates a vertex buffer to render a bounding box.
   * 
   * @return the vertex buffer.
   * @since 0.1
   */
  protected final FloatBuffer createBBVertexBuffer() {
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add( 1.f);
    vertices.add(-1.f);
    vertices.add( 1.f);

    vertices.add( 1.f);
    vertices.add( 1.f);
    vertices.add( 1.f);

    vertices.add(-1.f);
    vertices.add( 1.f);
    vertices.add( 1.f);

    vertices.add(-1.f);
    vertices.add(-1.f);
    vertices.add( 1.f);

    vertices.add( 1.f);
    vertices.add(-1.f);
    vertices.add(-1.f);

    vertices.add( 1.f);
    vertices.add( 1.f);
    vertices.add(-1.f);

    vertices.add(-1.f);
    vertices.add( 1.f);
    vertices.add(-1.f);

    vertices.add(-1.f);
    vertices.add(-1.f);
    vertices.add(-1.f);
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    
    return vertexBuffer;
  }

  /**
   * Creates a index buffer to render a bounding box.
   * 
   * @return the index buffer.
   */
  protected final ShortBuffer createBBIndexBuffer() {
    ArrayList<Short> indexes = new ArrayList<>();

    //bot
    indexes.add((short) 0);
    indexes.add((short) 1);

    indexes.add((short) 1);
    indexes.add((short) 2);

    indexes.add((short) 2);
    indexes.add((short) 3);

    indexes.add((short) 3);
    indexes.add((short) 0);

    //top
    indexes.add((short) 4);
    indexes.add((short) 5);

    indexes.add((short) 5);
    indexes.add((short) 6);

    indexes.add((short) 6);
    indexes.add((short) 7);

    indexes.add((short) 7);
    indexes.add((short) 4);

    //up
    indexes.add((short) 0);
    indexes.add((short) 4);

    indexes.add((short) 1);
    indexes.add((short) 5);

    indexes.add((short) 2);
    indexes.add((short) 6);

    indexes.add((short) 3);
    indexes.add((short) 7);
    
    //////////////////////////////////////////////////////////////
    Short saux1[] = indexes.toArray(new Short[indexes.size()]);
    short saux2[] = new short[saux1.length];
    for(int i = 0; i < saux1.length; i++) {
      saux2[i] = saux1[i];
    }
    saux1 = null;
    indexes = null;
    //////////////////////////////////////////////////////////////

    ShortBuffer indexBuffer = BufferUtils.createShortBuffer(saux2.length);
    bbNumIndices = saux2.length;
    indexBuffer.put(saux2);
    indexBuffer.flip();
    
    return indexBuffer;
  }
}
