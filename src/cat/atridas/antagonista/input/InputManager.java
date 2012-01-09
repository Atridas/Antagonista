package cat.atridas.antagonista.input;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Utils;

public final class InputManager {
  private static final Logger LOGGER = Logger.getLogger(InputManager.class.getCanonicalName());
	
	private boolean close = false;

	private final HashMap<Event, HashedString> actions = new HashMap<>();
	private final HashMap<HashedString, HashedString> actionToMode = new HashMap<>();
	
	private int mouseX, mouseY, deltaMouseX, deltaMouseY, deltaMouseZ;
	private HashSet<HashedString> activeActions = new HashSet<>();
	private HashSet<HashedString> activeModes = new HashSet<>();
	
	public void init() {
		try {
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void close() {
		Keyboard.destroy();
		Mouse.destroy();
	}
	
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
	
	public void registerAction(HashedString name, HashedString mode, Event event) {
		actions.put(event, name);
		actionToMode.put(name, mode);
		if(event.getAction() == EventAction.UP) {
			activeActions.add(name);
		}
	}
	
	public void activateMode(HashedString mode) {
		activeModes.add(mode);
	}
	
	public void deactivateMode(HashedString mode) {
		activeModes.remove(mode);
	}
	
	public boolean isCloseRequested()
	{
		return close;
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public int getDeltaMouseX() {
		return deltaMouseX;
	}
	
	public int getDeltaMouseY() {
		return deltaMouseY;
	}
	
	public int getDeltaMouseZ() {
		return deltaMouseZ;
	}
	
	public boolean isActionActive(final HashedString action) {
		return activeActions.contains(action) && activeModes.contains(actionToMode.get(action));
	}
	
	public void update()
	{
		if(Display.isCloseRequested())
		{
			close = true;
		}
		
		HashSet<HashedString> oneFrameActions = new HashSet<>();
		for(Entry<Event, HashedString> action : actions.entrySet()) {
			switch(action.getKey().getAction()) {
			case UP_DOWN:
			case DOWN_UP:
				oneFrameActions.add(action.getValue());
			}
		}
		
		activeActions.removeAll(oneFrameActions);
		
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		
		deltaMouseX =
		deltaMouseY =
		deltaMouseZ = 0;
		
		while(Mouse.next()) {
			if(Mouse.getEventButton() == -1)
			{
				deltaMouseX = Mouse.getDX();
				deltaMouseY = Mouse.getDY();
				deltaMouseZ = Mouse.getDWheel();
			} else {
			  HashedString action                  = null;
			  HashedString actionPermanent         = null;
			  HashedString actionPermanentToRemove = null;
				
				int button = Mouse.getEventButton();
				if(Mouse.getEventButtonState()) {
					action = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.UP_DOWN,
									button
									));
					actionPermanent = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.DOWN,
									button
									));
					actionPermanentToRemove = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.UP,
									button
									));
				} else  {
					action = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.DOWN_UP,
									button
									));
					actionPermanent = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.UP,
									button
									));
					actionPermanentToRemove = actions.get(
							new Event(
									EventType.MOUSE, 
									EventAction.DOWN,
									button
									));
				}

				if(action != null) {
					activeActions.add(action);
				}
				if(actionPermanent != null) {
					activeActions.add(actionPermanent);
				}
				if(actionPermanentToRemove != null) {
					activeActions.remove(actionPermanentToRemove);
				}
			}
		}
		
		
		while(Keyboard.next()) {
		  HashedString action                  = null;
		  HashedString actionPermanent         = null;
		  HashedString actionPermanentToRemove = null;
			
			int key = Keyboard.getEventKey();
			if(Keyboard.getEventKeyState()) {
				action = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.UP_DOWN,
								key
								));
				actionPermanent = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.DOWN,
								key
								));
				actionPermanentToRemove = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.UP,
								key
								));
			} else  {
				action = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.DOWN_UP,
								key
								));
				actionPermanent = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.UP,
								key
								));
				actionPermanentToRemove = actions.get(
						new Event(
								EventType.KEYBOARD, 
								EventAction.DOWN,
								key
								));
			}
			
			if(action != null) {
				activeActions.add(action);
			}
			if(actionPermanent != null) {
				activeActions.add(actionPermanent);
			}
			if(actionPermanentToRemove != null) {
				activeActions.remove(actionPermanentToRemove);
			}
		}
	}
	
	private static int getKeyCode(String key_name) {
	  switch(key_name.toUpperCase()) {
    case "KEY_1":
      return Keyboard.KEY_1;
    case "KEY_2":
      return Keyboard.KEY_2;
    case "KEY_3":
      return Keyboard.KEY_3;
    case "KEY_4":
      return Keyboard.KEY_4;
    case "KEY_5":
      return Keyboard.KEY_5;
    case "KEY_6":
      return Keyboard.KEY_6;
    case "KEY_7":
      return Keyboard.KEY_7;
    case "KEY_8":
      return Keyboard.KEY_8;
    case "KEY_9":
      return Keyboard.KEY_9;
    case "KEY_0":
      return Keyboard.KEY_0;
    case "KEY_A":
      return Keyboard.KEY_A;
    case "KEY_B":
      return Keyboard.KEY_B;
    case "KEY_C":
      return Keyboard.KEY_C;
    case "KEY_D":
      return Keyboard.KEY_D;
    case "KEY_E":
      return Keyboard.KEY_E;
    case "KEY_F":
      return Keyboard.KEY_F;
    case "KEY_G":
      return Keyboard.KEY_G;
    case "KEY_H":
      return Keyboard.KEY_H;
    case "KEY_I":
      return Keyboard.KEY_I;
    case "KEY_J":
      return Keyboard.KEY_J;
    case "KEY_K":
      return Keyboard.KEY_K;
    case "KEY_L":
      return Keyboard.KEY_L;
    case "KEY_M":
      return Keyboard.KEY_M;
    case "KEY_N":
      return Keyboard.KEY_N;
    case "KEY_O":
      return Keyboard.KEY_O;
    case "KEY_P":
      return Keyboard.KEY_P;
    case "KEY_Q":
      return Keyboard.KEY_Q;
    case "KEY_R":
      return Keyboard.KEY_R;
    case "KEY_S":
      return Keyboard.KEY_S;
    case "KEY_T":
      return Keyboard.KEY_T;
    case "KEY_U":
      return Keyboard.KEY_U;
    case "KEY_V":
      return Keyboard.KEY_V;
    case "KEY_W":
      return Keyboard.KEY_W;
    case "KEY_X":
      return Keyboard.KEY_X;
    case "KEY_Y":
      return Keyboard.KEY_Y;
    case "KEY_Z":
      return Keyboard.KEY_Z;
    case "KEY_ESCAPE":
      return Keyboard.KEY_ESCAPE;
    case "KEY_SPACE":
      return Keyboard.KEY_SPACE;
    case "KEY_RETURN":
      return Keyboard.KEY_RETURN;
    case "KEY_F1":
      return Keyboard.KEY_F1;
    case "KEY_F2":
      return Keyboard.KEY_F2;
    case "KEY_F3":
      return Keyboard.KEY_F3;
    case "KEY_F4":
      return Keyboard.KEY_F4;
    case "KEY_F5":
      return Keyboard.KEY_F5;
    case "KEY_F6":
      return Keyboard.KEY_F6;
    case "KEY_F7":
      return Keyboard.KEY_F7;
    case "KEY_F8":
      return Keyboard.KEY_F8;
    case "KEY_F9":
      return Keyboard.KEY_F9;
    case "KEY_F10":
      return Keyboard.KEY_F10;
    case "KEY_F11":
      return Keyboard.KEY_F11;
    case "KEY_F12":
      return Keyboard.KEY_F12;
    default:
      throw new IllegalArgumentException(key_name);
	  }
	}
	
	public static enum EventAction {
		UP,
		DOWN,
		UP_DOWN,
		DOWN_UP;
    
    public static EventAction getFromString(String str) {
      switch(str.toUpperCase()) {
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
	
	public static enum EventType {
		KEYBOARD,
		MOUSE;
		
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
	
	public static final class Event {
		private EventType   type;
		private EventAction action;
		private int         key;
		
		public Event(EventType _type, EventAction _action, int _key) {
			type = _type;
			action = _action;
			key = _key;
		}
		
		public EventType getType() {
			return type;
		}
		
		public EventAction getAction() {
			return action;
		}
		
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
