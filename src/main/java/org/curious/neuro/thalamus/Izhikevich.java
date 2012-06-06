package org.curious.neuro.thalamus;

public class Izhikevich implements Neuron {
	private double v, u, a, b, c, d, I;
	private boolean spiked;
	
	public Izhikevich(double v, double u, double a, double b, double c, double d) {
		this.v = v;
		this.u = u;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		I = 0;
		spiked = false;
	}
	
	public boolean simulate(double t) {
		double deltaV = .04f*v*v + 5*v + 140 - u + I;
		double deltaU = a*(b*v - u);
		v += deltaV*t;
		u += deltaU*t;
		I = 0;
		
		if(v > 30) {
			v = c;
			u = u + d;
			spiked = true;
		} else {
			spiked = false;
		}
		return spiked;
	}

	public double getVoltage() {
		return v;
	}
	
	public boolean isSpiked() {
		return spiked;
	}

	public void setStimulation(double I) {
		this.I = I;
	}
}
