package cat.atridas.antagonista;

public enum Quality {
  NONE, LOW, MID, HIGH, ULTRA;
  
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
      throw new IllegalStateException();
    }
  }
  
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
      throw new IllegalStateException();
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
