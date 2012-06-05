package org.curious.neuro.thalamus;

public class PotassiumConductance implements Conductance {
	private float conductance;
	private float restingVoltage;
	public float activation;
	private Float oldActivation;
	
	public PotassiumConductance(float conductance, float restingVoltage, float initialVoltage) {
		this.conductance = conductance;
		this.restingVoltage = restingVoltage;
		oldActivation = null;
		activation = alpha(initialVoltage)/(alpha(initialVoltage) + beta(initialVoltage));
	}
	
	private float alpha(float voltage) {
		return .01f*(voltage + 55)/(1 - (float)Math.exp(-(voltage + 55)/10));
	}
	
	private float beta(float voltage) {
		return .125f*(float)Math.exp(-(voltage + 65)/80);
	}
	
	public void simulate(float time, float voltage) {
		oldActivation = activation;
		
		float k1, k2, k3, k4;
		
		k1 = time*(alpha(voltage)*(1 - activation) - beta(voltage)*activation);
		k2 = time*(alpha(voltage)*(1 - (activation+k1/2)) - beta(voltage)*(activation+k1/2));
		k3 = time*(alpha(voltage)*(1 - (activation+k2/2)) - beta(voltage)*(activation+k2/2));
		k4 = time*(alpha(voltage)*(1 - (activation+k3)) - beta(voltage)*(activation+k3));
		activation += (k1 + 2*k2 + 2*k3 + k4)/6;
		//System.out.println(voltage + " " + alpha(voltage) + " " + beta(voltage));
	}
	
	public void undo() {
		activation = oldActivation;
		oldActivation = null;
	}

	public float getCurrent(float voltage) {
		//System.out.println(conductance + " " + activation);
		return conductance*(float)Math.pow(activation, 4)*(voltage - restingVoltage);
	}
}
