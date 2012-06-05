package org.curious.neuro.thalamus;

public interface Conductance {
	public void simulate(float time, float voltage);
	public void undo();
	public float getCurrent(float voltage);
}
