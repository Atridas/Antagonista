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
  /**
   * Size of the buffer used to absorb spikes.
   * @since 0.1
   */
	public static final int WINDOW_LENGTH = 5;
	
	/**
	 * Influence of the previous drift in the produced delta time.
	 * @since 0.2
	 */
	public static final float DRIFT_INFLUENCE = 0.01f;
	
	/**
	 * Delta time objectives. The engine will try to accomplish a frame duration of this values.
	 * @since 0.2
	 */
	public static final float MS_OBJECTIVES[] = {1000f/60, 1000f/30, 1000f/20, 1000f/10};
	
	/**
	 * Timer resolution.
	 * @since 0.2
	 */
	public static final long timerResolution = Sys.getTimerResolution();
	
	/**
	 * Last instance of a Delta time counter;
	 * @since 0.1
	 */
	DeltaTime lastDeltaTime = new DeltaTime();
	/**
	 * Last clock time;
	 * @since 0.1
	 */
	long lastTime;
	/**
	 * Array of delta times. Used to absorb spike frames (frames with an unusual duration).
	 * @since 0.1
	 */
	final long[] deltaTimes = new long[WINDOW_LENGTH];
	/**
	 * Current buffer position.
	 * @since 0.1
	 */
	int current = 0;
	
	/**
	 * Measures the drift from the mesured time to the time obtained adding all produced delta times.
	 * @since 0.2
	 */
	float drift = 0;
	
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
	public synchronized DeltaTime update() {
		long time = Sys.getTime();
		long realDeltaTime = deltaTimes[current] = time - lastTime;
    
		
		float dtMilis = (1000f * realDeltaTime) / timerResolution;
		for(int i = 0; i < MS_OBJECTIVES.length; ++i) {
		  float dist = MS_OBJECTIVES[i] - dtMilis;
		  if(dist >= 1f) {
		    try {
          wait((long)dist);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
		    time += (long)(dist * timerResolution / 1000.f);
		    realDeltaTime = deltaTimes[current] = time - lastTime;
		    break;
		  }
		}
		
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
		
		float dt = ((float) sum) / (total * timerResolution);
		
		dt -= drift * DRIFT_INFLUENCE;
		
		drift += dt - realDeltaTime / 1000.f;
		
		lastDeltaTime = new DeltaTime(dt, realDeltaTime);
		return lastDeltaTime;
	}
	
	public void reset() {
	  lastTime = Sys.getTime();
    for(int i = 0; i < WINDOW_LENGTH; ++i) {
      deltaTimes[i] = 0;
    }
    current = 0;
    lastDeltaTime = new DeltaTime();
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
	public class DeltaTime implements Comparable<DeltaTime> {
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
	   * Exact time in ticks since the creation of this clock.
     * @since 0.2
	   */
    private final long  timeTicksSinceStart;
    /**
     * Frame numer. This variable contains the number of times wich the <code>update</code> function
     * of the corresponding clock has been called.
     * @since 0.1
     */
    public final long  frame;
    
    private DeltaTime() {
      dt = 0;
      fps = 0;
      timeTicksSinceStart = 0;
      frame = 0;
    }
    
    private DeltaTime(float _dt, long _realDTTicks) {
      dt = _dt;
      fps = (int)(1 / dt);
      timeTicksSinceStart = lastDeltaTime.timeTicksSinceStart + _realDTTicks;
      frame = lastDeltaTime.frame + 1;
    }
    
    /**
     * Gets the time in milliseconds since the last reset of the clock.
     * 
     * @return the time in milliseconds.
     * @since 0.2
     */
    public long getTimeMilisSinceStart() {
      return timeTicksSinceStart * 1000 / timerResolution;
    }
    
    @Override
    public String toString() {
      return timeTicksSinceStart + "ms, " + fps + "FPS";
    }

    @Override
    public int compareTo(DeltaTime o) {
      int c = Long.compare(timeTicksSinceStart, o.timeTicksSinceStart);
      return c;
    }
    
    /**
     * Returns true if this delta time object was created before the object passed as
     * parameter.
     * 
     * @param o other object to compare.
     * @return true if this object is older.
     */
    public boolean isOltherThan(DeltaTime o) {
      return compareTo(o) < 0;
    }

    /**
     * Returns true if this delta time object was created after the object passed as
     * parameter.
     * 
     * @param o other object to compare.
     * @return true if this object is newer.
     */
    public boolean isNewerThan(DeltaTime o) {
      return compareTo(o) > 0;
    }
	}
}
