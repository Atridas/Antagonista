package cat.atridas.antagonista;

import org.lwjgl.Sys;

/**
 * Clock class to control times in the engine.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
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
	 */
	public Clock() {
		lastTime = Sys.getTime();
	}
	
	/**
	 * Updates the time and returns the time lapsed since last call.
	 * 
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
	 * Class that encapsulates information avout the time lapsed in every frame.
	 * 
	 * @author Isaac 'Atridas' Serrano Guasch
	 *
	 */
	public class DeltaTime {
	  /**
	   * Approximation, in seconds, of the time lapsed this frame.
	   */
	  public final float dt;
	  /**
	   * Approximation of the number of frames produced every second according to calls to this
	   * clock.
	   */
	  public final int fps;
	  /**
	   * Exact time in miliseconds since the creation of this clock.
	   */
    public final long  timeMilisSinceStart;
    /**
     * Frame numer. This variable contains the number of times wich the <code>update</code> function
     * of the corresponding clock has been called.
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
	}
}
