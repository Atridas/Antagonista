package cat.atridas.antagonista.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public final class InputManager {
	
	private boolean close = false;

	private final HashMap<Event, String> actions = new HashMap<Event, String>();
	private final HashMap<String, String> actionToMode = new HashMap<String, String>();
	
	private int mouseX, mouseY, deltaMouseX, deltaMouseY, deltaMouseZ;
	private HashSet<String> activeActions = new HashSet<String>();
	private HashSet<String> activeModes = new HashSet<String>();
	
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
	
	
	public void registerAction(String name, String mode, Event event) {
		actions.put(event, name);
		actionToMode.put(name, mode);
		if(event.getAction() == EventAction.UP) {
			activeActions.add(name);
		}
	}
	
	public void activateMode(String mode) {
		activeModes.add(mode);
	}
	
	public void deactivateMode(String mode) {
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
	
	public boolean isActionActive(final String action) {
		return activeActions.contains(action) && activeModes.contains(actionToMode.get(action));
	}
	
	public void update()
	{
		if(Display.isCloseRequested())
		{
			close = true;
		}
		
		HashSet<String> oneFrameActions = new HashSet<String>();
		for(Entry<Event, String> action : actions.entrySet()) {
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
				String action                  = null;
				String actionPermanent         = null;
				String actionPermanentToRemove = null;
				
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
			String action                  = null;
			String actionPermanent         = null;
			String actionPermanentToRemove = null;
			
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
	
	
	public static enum EventAction {
		UP,
		DOWN,
		UP_DOWN,
		DOWN_UP
	}
	
	public static enum EventType {
		KEYBOARD,
		MOUSE
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
