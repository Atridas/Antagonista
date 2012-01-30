package cat.atridas.antagonista.entities;

import java.util.logging.Logger;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;

/**
 * Embeds an Entity of the game.
 * 
 * @author Isaac 'Atridas' Serrano Guash
 * @since 0.2
 *
 */
public final class Entity {
  private static Logger LOGGER = Logger.getLogger(Entity.class.getCanonicalName());

  /**
   * String identifier.
   * @since 0.2
   */
  private final HashedString id;
  
  /**
   * Constructor.
   * 
   * @param _id unique identifier.
   * @since 0.2
   */
  Entity(HashedString _id) {
    id = _id;
  }
  
  /**
   * Gets the entity unique identifier.
   * 
   * @return the entity unique identifier.
   * @since 0.2
   */
  public HashedString getId() {
    return id;
  }
  
  
  
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Entity other = (Entity) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return "Entity " + id;
  }
  
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      LOGGER.severe(Utils.logExceptionStringAndStack(e));
      throw new RuntimeException(e);
    }
  }
}
