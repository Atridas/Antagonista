package cat.atridas.antagonista;

import java.io.InputStream;

/**
 * Class that represents a Resource of the game. Every resource must extend from this class.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @version 1.1 22/1/2012
 * @since 0.1
 *
 */
public abstract class Resource {
  
  /**
   * Name of the resource. Must be unique for every resource type.
   * @since 0.1
   */
  protected final HashedString resourceName;
  
  /**
   * Constructor that sets the constant field resourceName.
   * 
   * @since 0.1
   * @param _resourceName name of the resource.
   */
  protected Resource(HashedString _resourceName) {
    resourceName = _resourceName;
  }
  
  /**
   * Overwrite that to implement the way this resource is loaded. The Engine will provide the input
   * stream, reseted to the beginning of the file, and the extension this file had, in case
   * different kind of resources had different formats (for example dds and tga images).
   * 
   * @since 0.1
   * @param is Input Stream used to load this resource.
   * @param extension Extension of the resource in case there are different formats.
   * @return <code>true</code> in case the resource was correctly loaded. <code>false</code>
   *         otherwise
   */
  public abstract boolean load(InputStream is, HashedString extension);
  
  /**
   * Debug method that may return the memory resources used in bytes.
   * 
   * @since 0.1
   * @return Byte RAM use estimation.
   */
  public abstract int getRAMBytesEstimation();
  /**
   * Debug method that may return the video memory resources used in bytes.
   * 
   * @since 0.1
   * @return Byte VRAM use estimation.
   */
  public abstract int getVRAMBytesEstimation();
  
  /**
   * Indicates if the resource has been cleaned. Should be used in the beginning of most
   * methods in an <code>asset</code> statement.
   */
  protected boolean cleaned = false;
      
  /**
   * Cleans the resource. Used to clean up the OpenGL state in most implementations. Must
   * set the field cleaned to true.
   * 
   * @since 0.1
   */
  public abstract void cleanUp();
  
  @Override
  public void finalize() {
    if(!cleaned) {
      cleanUp();
      assert !cleaned;
    }
  }
}
