package cat.atridas.antagonista;

import org.lwjgl.Sys;

public class Clock {
	public static final int WINDOW_LENGTH = 60;
	
	long lastTime;
	final long[] deltaTimes = new long[WINDOW_LENGTH];
	int current = 0;
	
	public Clock() {
		lastTime = Sys.getTime();
	}
	
	public float update() {
		long time = Sys.getTime();
		deltaTimes[current] = time - lastTime;
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
		
		return ((float) sum) / (total * Sys.getTimerResolution());
	}
}
