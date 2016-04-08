
package postprocessing.effects;


import postprocessing.PostProcessorEffect;

public abstract class Antialiasing extends PostProcessorEffect {

	public abstract void setViewportSize (int width, int height);
}
