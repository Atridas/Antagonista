package cat.atridas.antagonista;

import org.lwjgl.Sys;

/**
 * Clock class to control times in the engine.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public class Clock {
	public static final int WINDOW_LENGTH = 60;
	
	DeltaTime lastDeltaTime = new DeltaTime();
	long lastTime;
	final long[] deltaTimes = new long[WINDOW_LENGTH];
	int current = 0;
	
	/**
	 * Creates a new clock.
	 * @since 0.1
	 */
	public Clock() {
		lastTime = Sys.getTime();
	}
	
	/**
	 * Updates the time and returns the time lapsed since last call.
	 * 
   * @since 0.1
	 * @return a DeltaTime object witch includes info of the time lapsed
	 *         since last call.
	 */
	public DeltaTime update() {
		long time = Sys.getTime();
		long realDeltaTime = deltaTimes[current] = time - lastTime;
		lastTime = time;
		if(deltaTimes[current] < 1)
			deltaTimes[current] = 1;
		current++;
		
		current %= WINDOW_LENGTH;
		
		long sum = 0;
		int total = WINDOW_LENGTH;
		for(int i = 0; i < WINDOW_LENGTH; ++i) {
			sum += deltaTimes[i];
			if(deltaTimes[i] == 0) {
				total = i;
				break;
			}
		}
		
		float dt = ((float) sum) / (total * Sys.getTimerResolution());
		
		lastDeltaTime = new DeltaTime(dt, realDeltaTime);
		return lastDeltaTime;
	}
	
	/**
	 * Fetches the last delta time object.
	 * 
	 * @return the last delta time object.
	 * @since 0.2
	 */
	public DeltaTime getCurrentFrameDeltaTime() {
	  return lastDeltaTime;
	}
	
	/**
	 * Class that encapsulates information avout the time lapsed in every frame.
	 * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
	 *
	 */
	public class DeltaTime {
	  /**
	   * Approximation, in seconds, of the time lapsed this frame.
	   * @since 0.1
	   */
	  public final float dt;
	  /**
	   * Approximation of the number of frames produced every second according to calls to this
	   * clock.
     * @since 0.1
	   */
	  public final int fps;
	  /**
	   * Exact time in miliseconds since the creation of this clock.
     * @since 0.1
	   */
    public final long  timeMilisSinceStart;
    /**
     * Frame numer. This variable contains the number of times wich the <code>update</code> function
     * of the corresponding clock has been called.
     * @since 0.1
     */
    public final long  frame;
    
    private DeltaTime() {
      dt = 0;
      fps = 0;
      timeMilisSinceStart = 0;
      frame = 0;
    }
    
    private DeltaTime(float _dt, long _realDTMilis) {
      dt = _dt;
      fps = (int)(1 / dt);
      timeMilisSinceStart = lastDeltaTime.timeMilisSinceStart + _realDTMilis;
      frame = lastDeltaTime.frame + 1;
    }
    
    @Override
    public String toString() {
      return timeMilisSinceStart + "ms, " + fps + "FPS";
    }
	}
}
