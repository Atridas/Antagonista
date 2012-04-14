package cat.atridas.antagonista.input;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.Clock.DeltaTime;

/**
 * Input manager class. This class is responsible of registering all user input actions and map them
 * to a configuration specified events.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 *
 */
public abstract class InputManager {
  private static final Logger LOGGER = Logger.getLogger(InputManager.class.getCanonicalName());

  /**
   * Horizontal mouse move identifier.
   * @since 0.1
   */
  public static final int MOUSE_EVENT_KEY_MOVEMENT_X     = 0;
  /**
   * Vertical mouse move identifier.
   * @since 0.1
   */
  public static final int MOUSE_EVENT_KEY_MOVEMENT_Y     = 1;
  /**
    * Mouse wheel move identifier.
    * @since 0.1
    */
  public static final int MOUSE_EVENT_KEY_MOVEMENT_WHEEL = 2;
  
  /**
   * If this application had requested to be closed.
   * @since 0.1
   */
	private boolean close = false;

	/**
	 * Maps different input events to an action ID.
	 * @since 0.1
	 */
	private final HashMap<Event, HashedString> actions = new HashMap<>();
	/**
	 * Maps all actions ID to it's mode.
	 * @since 0.1
	 * @see #activeModes
	 */
	private final HashMap<HashedString, HashedString> actionToMode = new HashMap<>();
	
	/**
	 * Mouse state variables.
	 * @since 0.1
	 */
	private int mouseX, mouseY, deltaMouseX, deltaMouseY, deltaMouseZ;
	/**
	 * Action IDs of all active actions, with a float "strength" number.
	 * @since 0.1
	 */
	private HashMap<HashedString,Float> activeActions = new HashMap<>();
	/**
	 * Set of all active modes.
	 * @since 0.1
	 */
	private HashSet<HashedString> activeModes = new HashSet<>();
	
	/**
	 * Initializes the manager.
	 * @since 0.1
	 */
	public abstract void init();
	
	/**
	 * Closes the manager.
	 * @since 0.1
	 */
	public abstract void close();
	
	/**
	 * Loads an xml configuration file.
	 * 
	 * @param file path to the configuration file.
	 * @throws IllegalArgumentException on severe errors such as file not found, or ill-formated xml.
	 * @since 0.1
	 */
	public void loadActions(String file) {
	  
	  
	  try {
	    InputStream is = Utils.findInputStream(file);
	    
      DocumentBuilder db;
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();
      
      
      Element input_managerXML = doc.getDocumentElement();
      if("input_manager".compareTo(input_managerXML.getTagName()) != 0) {
        LOGGER.severe("Root element is not \"input_manager\".");
        throw new Exception();
      }
      
      NodeList modesXML = input_managerXML.getElementsByTagName("mode");
      for(int i = 0; i < modesXML.getLength(); ++i) {
        Element modeXML = (Element) modesXML.item(i);
        
        String mode = modeXML.getAttribute("name");
        HashedString hsMode = new HashedString(mode);
        
        NodeList eventsXML = input_managerXML.getElementsByTagName("event");
        for(int j = 0; j < eventsXML.getLength(); ++j) {
          Element eventXML = (Element) eventsXML.item(j);
          String name   = eventXML.getAttribute("name");
          String type   = eventXML.getAttribute("type");
          String action = eventXML.getAttribute("action");
          String key    = eventXML.getAttribute("key");
          
          try {
            EventAction ea = EventAction.getFromString(action);
            EventType   et = EventType.getFromString(type);
            int keyID = getKeyCode(key);
            
            registerAction(new HashedString(name), hsMode, new Event(et,ea,keyID));
            
          } catch(IllegalArgumentException iae) {
            LOGGER.warning("Illegal parameter " + iae.toString());
          }
        }
      }
    
    } catch (FileNotFoundException e) {
      LOGGER.severe("Could not find input file");
      throw new IllegalArgumentException(e);
    } catch (Exception e) {
      LOGGER.severe("Error reading xml file");
      throw new IllegalArgumentException(e);
    }
	}
	
	/**
	 * Registers a new action.
	 * 
	 * @param name action identifier.
	 * @param mode in witch the action is active.
	 * @param event that triggers the action.
	 * @since 0.1
	 */
	public void registerAction(HashedString name, HashedString mode, Event event) {
		actions.put(event, name);
		actionToMode.put(name, mode);
		if(event.getAction() == EventAction.UP) {
			activeActions.put(name, 0.f);
		}
	}
	
	/**
	 * Activates a mode.
	 * 
	 * @param mode to be activated.
	 * @since 0.1
	 */
	public void activateMode(HashedString mode) {
		activeModes.add(mode);
	}
	

  /**
   * Deactivates a mode.
   * 
   * @param mode to be deactivated.
   * @since 0.1
   */
	public void deactivateMode(HashedString mode) {
		activeModes.remove(mode);
	}
	
	/**
	 * Checks if the user is trying to close the application.
	 * 
	 * @return <code>true</code> when the user is trying to close the application.
	 * @since 0.1
	 */
	public boolean isCloseRequested()
	{
		return close;
	}
	
	/**
	 * Gets the mouse pixel position in the X axis. The 0 coordinate is in the left of the screen.
	 * 
	 * @return the mouse pixel position in the X axis.
	 * @since 0.1
	 */
	public int getMouseX() {
		return mouseX;
	}

  /**
   * Gets the mouse pixel position in the Y axis. The 0 coordinate is in the bottom of the screen.
   * 
   * @return the mouse position in the Y axis.
   * @since 0.1
   */
	public int getMouseY() {
		return mouseY;
	}
	
	/**
	 * Gets the mouse pixel displacement in the X axis during the last frame.
	 * A positive displacement is a movement to the right.
	 * 
	 * @return the mouse pixel displacement in the X axis.
   * @since 0.1
	 */
	public int getDeltaMouseX() {
		return deltaMouseX;
	}

  /**
   * Gets the mouse pixel displacement in the Y axis during the last frame.
   * A positive displacement is a movement to the top of the screen.
   * 
   * @return the mouse pixel displacement in the Y axis.
   * @since 0.1
   */
	public int getDeltaMouseY() {
		return deltaMouseY;
	}
	
	/**
	 * Gets the mouse wheel displacement during the last frame.
	 * A positive displacement means that the wheel has been rotated in the direction to the user.
	 * 
	 * @return the mouse wheel displacement.
	 * @since 0.1
	 */
	public int getDeltaMouseZ() {
		return deltaMouseZ;
	}
  
	/**
	 * Checks if an action is active. If an action is active, but it's mode is not, said action is
	 * treated as non-active.
	 * 
	 * @param action identifier.
	 * @return <code>true</code> if both the action and it's mode are active.
	 * @since 0.1
	 */
  public boolean isActionActive(final HashedString action) {
    return activeActions.containsKey(action) && activeModes.contains(actionToMode.get(action));
  }
  
  /**
   * Gets the value of an active action. It is an error to call this method using and inactive action
   * identifier and the method may throw and exception in that case. The action value is 1 in
   * single frame actions, the last frame pixel mouse displacement in mouse movement events and
   * the time since last state change in key/button presses/unpresses.
   * 
   * @param action identifier.
   * @return action active value.
   * @since 0.1
   */
  public float getActionValue(final HashedString action) {
    assert isActionActive(action);
    return activeActions.get(action);
  }
	
  /**
   * Checks the input actions.
   * 
   * @param dt time since last call to this method.
   * @since 0.1
   */
	public abstract void update(DeltaTime dt);
	
	/**
	 * Translate a key name to a int identifier.
	 * 
	 * @param key_name
	 * @return
	 */
	protected abstract int getKeyCode(String key_name);
	
	/**
	 * Kind of input event.
	 * 
	 * @author Isaac 'Atridas' Serrano Guasch
	 * @since 0.1
	 *
	 */
	public static enum EventAction {
	  /**
	   * Movement, use in mouse events.
	   * @since 0.1
	   */
	  MOVE,
	  /**
	   * Key/Button is unpressed.
     * @since 0.1
	   */
		UP,
		/**
		 * Key/button is pressed.
     * @since 0.1
		 */
		DOWN,
		/**
		 * Key/button has been pressed this frame.
     * @since 0.1
		 */
		UP_DOWN,
		/**
		 * Key/button has been unpressed this frame.
     * @since 0.1
		 */
		DOWN_UP;
    
	  /**
	   * Parses a string to create an enumeration value.
	   * 
	   * @param str String to parse.
	   * @return enumeration value
	   * @throws IllegalArgumentException if the string does not represent any value.
	   * @since 0.1
	   */
    public static EventAction getFromString(String str) {
      switch(str.toUpperCase()) {
      case "MOVE":
        return MOVE;
      case "UP":
        return UP;
      case "DOWN":
        return DOWN;
      case "UP_DOWN":
        return UP_DOWN;
      case "DOWN_UP":
        return DOWN_UP;
      default:
        throw new IllegalArgumentException(str);
      }
    }
	}

  /**
   * Input device.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
	public static enum EventType {
		KEYBOARD,
		MOUSE;
		
	  /**
     * Parses a string to create an enumeration value.
     * 
     * @param str String to parse.
     * @return enumeration value
     * @throws IllegalArgumentException if the string does not represent any value.
     * @since 0.1
     */
		public static EventType getFromString(String str) {
		  switch(str.toUpperCase()) {
      case "KEYBOARD":
        return KEYBOARD;
      case "MOUSE":
        return MOUSE;
      default:
        throw new IllegalArgumentException(str);
		  }
		}
	}

  /**
   * Input event.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   *
   */
	public static final class Event {
		private EventType   type;
		private EventAction action;
		private int         key;
		
		/**
		 * Builds an unmodificable input event.
		 * 
		 * @param _type device type.
		 * @param _action input type.
		 * @param _key identifier.
		 * @since 0.1
		 */
		public Event(EventType _type, EventAction _action, int _key) {
			type = _type;
			action = _action;
			key = _key;
		}
		
		/**
		 * Gets the input device type.
		 * @return the input device type.
		 * @since 0.1
		 */
		public EventType getType() {
			return type;
		}
		
		/**
		 * Gets the action type.
		 * 
		 * @return the action type.
		 * @since 0.1
		 */
		public EventAction getAction() {
			return action;
		}
		
		/**
		 * Gets the key/button pressed.
		 * @return the key/button pressed.
		 * @since 0.1
		 */
		public int getKey() {
			return key;
		}

		@Override
		public boolean equals(Object o) {
			if(o.getClass() != Event.class) {
				return false;
			}
			Event e = (Event) o;
			if(e.type != type) {
				return false;
			}
			if(e.action != action) {
				return false;
			}
			if(e.key != key) {
				return false;
			}
			
			return true;
		}

		@Override
		public int hashCode() {
			int hash = key;
			hash = hash * 37 + type.hashCode();
			hash = hash * 49 + action.hashCode();
			
			return hash;
		}
		
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("Event: ");
			
			switch(type) {
			case MOUSE:
				sb.append("mouse");
				break;
			case KEYBOARD:
				sb.append("keyboard");
				break;
			}
			
			sb.append(", ");
			
			switch(action) {
			case DOWN:
				sb.append("down");
				break;
			case UP:
				sb.append("up");
				break;
			case DOWN_UP:
				sb.append("down_up");
				break;
			case UP_DOWN:
				sb.append("up_down");
				break;
			}
			
			sb.append(", ");
			sb.append(key);
			
			return sb.toString();
		}
	}
}
