package cat.atridas.antagonista;

/**
 * Used to designated the quality of various parameters, for example, Shader Techniques.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 *
 */
public enum Quality {
  NONE, LOW, MID, HIGH, ULTRA;
  
  /**
   * Gets the quality directly bellow. In case of the lowest quality (NONE), returns itself.
   * @return
   */
  public Quality previousQuality() {
    switch(this) {
    case NONE:
      return NONE;
    case LOW:
      return NONE;
    case MID:
      return LOW;
    case HIGH:
      return MID;
    case ULTRA:
      return HIGH;
    default:
      throw new IllegalArgumentException();
    }
  }
  
  /**
   * Creates a quality from a string.
   * 
   * @param str String to parse.
   * @return a quality.
   * @throws IllegalArgumentException if the input does not map to any enum value.
   */
  public static Quality parseString(String str) {
    switch(str.toUpperCase()) {
    case "NONE":
      return NONE;
    case "LOW":
      return LOW;
    case "MID":
      return MID;
    case "HIGH":
      return HIGH;
    case "ULTRA":
      return ULTRA;
    default:
      throw new IllegalArgumentException();
    }
  }
  
  @Override
  public String toString() {
    switch(this) {
    case NONE:
      return "NONE";
    case LOW:
      return "LOW";
    case MID:
      return "MID";
    case HIGH:
      return "HIGH";
    case ULTRA:
      return "ULTRA";
    default:
      throw new IllegalStateException();
    }
  }
}
