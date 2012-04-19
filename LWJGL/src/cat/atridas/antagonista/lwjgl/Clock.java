package cat.atridas.antagonista.lwjgl;

import org.lwjgl.Sys;

/**
 * Clock class to control times in the engine.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 */
public final class Clock extends cat.atridas.antagonista.Clock {

  @Override
  protected long getTime() {
    return Sys.getTime();
  }

  @Override
  protected long getTimerResolution() {
    return Sys.getTimerResolution();
  }
}
