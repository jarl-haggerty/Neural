package org.curious.neuro.thalamus;

public interface Neuron {
	public double getVoltage();
	public boolean isSpiked();
	public boolean simulate(double t);
	public void stimulate(double I);
}
