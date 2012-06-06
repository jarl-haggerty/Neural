package org.curious.neuro.thalamus;

import java.util.ArrayList;
import java.util.Collection;

public class HodgkinHuxley implements Neuron {
	private Collection<Conductance> conductances;
	private double stimulation, voltage, capacitance, leakConductance, leakReversalPotential, potassiumConductance, potassiumReversalPotential, sodiumConductance, sodiumReversalPotential, n, m, h;
	private boolean spiked;

	public HodgkinHuxley(double initialVoltage, double capacitance, double leakConductance, double leakReversalPotential, double potassiumConductance, double potassiumReversalPotential, double sodiumConductance, double sodiumReversalPotential) {
		this.voltage = initialVoltage;
		n = infinityN(voltage);
		m = infinityM(voltage);
		h = infinityH(voltage);
		this.capacitance = capacitance;
		this.leakConductance = leakConductance;
		this.leakReversalPotential = leakReversalPotential;
		this.potassiumConductance = potassiumConductance;
		this.potassiumReversalPotential = potassiumReversalPotential;
		this.sodiumConductance = sodiumConductance;
		this.sodiumReversalPotential = sodiumReversalPotential;
		stimulation = 0;
		spiked = false;
	}
	
	private static double alphaN(double voltage) {
		return .01f*(voltage + 55)/(1 - Math.exp(-(voltage + 55)/10));
	}
	
	private static double betaN(double voltage) {
		return .125f*Math.exp(-(voltage + 65)/80);
	}
	
	private static double infinityN(double voltage) {
		return alphaN(voltage)/(alphaN(voltage) + betaN(voltage));
	}
	
	private double integrateN(double time, double voltage) {
		double k1, k2, k3, k4;
		k1 = time*(alphaN(voltage)*(1 - n) - betaN(voltage)*n);
		k2 = time*(alphaN(voltage)*(1 - (n+k1/2)) - betaN(voltage)*(n+k1/2));
		k3 = time*(alphaN(voltage)*(1 - (n+k2/2)) - betaN(voltage)*(n+k2/2));
		k4 = time*(alphaN(voltage)*(1 - (n+k3)) - betaN(voltage)*(n+k3));
		return n + (k1 + 2*k2 + 2*k3 + k4)/6;
	}
	
	public double getN() {
		return n;
	}
	
	private static double alphaM(double voltage) {
		return .1f*(voltage + 40)/(1 - Math.exp(-(voltage + 40)/10));
	}
	
	private static double betaM(double voltage) {
		return 4*Math.exp(-(voltage + 65)/18);
	}
	
	private static double infinityM(double voltage) {
		return alphaM(voltage)/(alphaM(voltage) + betaM(voltage));
	}
	
	private double integrateM(double time, double voltage) {
		double k1, k2, k3, k4;
		k1 = time*(alphaM(voltage)*(1 - m) - betaM(voltage)*m);
		k2 = time*(alphaM(voltage)*(1 - (m+k1/2)) - betaM(voltage)*(m+k1/2));
		k3 = time*(alphaM(voltage)*(1 - (m+k2/2)) - betaM(voltage)*(m+k2/2));
		k4 = time*(alphaM(voltage)*(1 - (m+k3)) - betaM(voltage)*(m+k3));
		return m + (k1 + 2*k2 + 2*k3 + k4)/6;
	}
	
	public double getM() {
		return m;
	}
	
	private static double alphaH(double voltage) {
		return .07f*Math.exp(-(voltage + 65)/20);
	}
	
	private static double betaH(double voltage) {
		return 1/(1 + Math.exp(-(voltage + 35)/10));
	}
	
	private static double infinityH(double voltage) {
		return alphaH(voltage)/(alphaH(voltage) + betaH(voltage));
	}
	
	private double integrateH(double time, double voltage) {
		double k1, k2, k3, k4;
		k1 = time*(alphaH(voltage)*(1 - h) - betaH(voltage)*h);
		k2 = time*(alphaH(voltage)*(1 - (h+k1/2)) - betaH(voltage)*(h+k1/2));
		k3 = time*(alphaH(voltage)*(1 - (h+k2/2)) - betaH(voltage)*(h+k2/2));
		k4 = time*(alphaH(voltage)*(1 - (h+k3)) - betaH(voltage)*(h+k3));
		return h + (k1 + 2*k2 + 2*k3 + k4)/6;
	}
	
	public double getH() {
		return h;
	}
	
	private double getMembraneCurrent() {
		return getMembraneCurrent(voltage, n, m, h);
	}
	
	private double getMembraneCurrent(double voltage, double n, double m, double h) {
		return leakConductance*(voltage - leakReversalPotential) +
				potassiumConductance*n*n*n*n*(voltage - potassiumReversalPotential) +
				sodiumConductance*m*m*m*h*(voltage - sodiumReversalPotential);
	}

	public double getVoltage() {
		return voltage;
	}

	public boolean isSpiked() {
		return false;
	}

	public boolean simulate(double time) {
		double current, k1, k2, k3, k4, n, m, h;
		//System.out.println(voltage);
		
		k1 = time*-(getMembraneCurrent() + stimulation)/capacitance;
		
		n = integrateN(time/2, voltage + k1/2);
		m = integrateM(time/2, voltage + k1/2);
		h = integrateH(time/2, voltage + k1/2);
		k2 = time*-(getMembraneCurrent(voltage + k1/2, n, m, h) + stimulation)/capacitance;
		
		n = integrateN(time/2, voltage + k2/2);
		m = integrateM(time/2, voltage + k2/2);
		h = integrateH(time/2, voltage + k2/2);
		k3 = time*-(getMembraneCurrent(voltage + k2/2, n, m, h) + stimulation)/capacitance;
		
		n = integrateN(time, voltage + k3);
		m = integrateM(time, voltage + k3);
		h = integrateH(time, voltage + k3);
		k4 = time*-(getMembraneCurrent(voltage + k3, n, m, h) + stimulation)/capacitance;
		
		voltage += (k1 + 2*k2 + 2*k3 + k4)/6;
		stimulation = 0;
		this.n = integrateN(time, voltage + k3);
		this.m = integrateM(time, voltage + k3);
		this.h = integrateH(time, voltage + k3);
		
		return false;
	}

	public void setStimulation(double I) {
		stimulation = I;
	}

}
