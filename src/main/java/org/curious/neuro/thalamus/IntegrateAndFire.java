package org.curious.neuro.thalamus;

public class IntegrateAndFire implements Neuron {
	private double timeConstant;
	private double restingPotential;
	private double stimulation;
	private double potential;
	private double resistance;
	private double area;
	private boolean spiked;
	private double threshold;
	private double reset;

	public IntegrateAndFire(double timeConstant, double resistance, double restingPotential, double threshold, double reset) {
		this.timeConstant = timeConstant;
		this.restingPotential = restingPotential;
		potential = restingPotential;
		this.resistance = resistance;
		this.area = area;
		this.threshold = threshold;
		this.reset = reset;
		spiked = false;
	}

	public double getVoltage() {
		return potential;
	}

	public boolean isSpiked() {
		return spiked;
	}
	
	public boolean simulate(double t) {
		double step = restingPotential-potential + resistance*1e6f*stimulation;
		System.out.println(step);
		potential += step/timeConstant*t*1e-3;
		stimulation = 0;
		
		spiked = potential > threshold;
		if(spiked) {
			potential = reset;
		}
		return spiked;
	}

	public void stimulate(double I) {
		this.stimulation = I;
	}
	
}
