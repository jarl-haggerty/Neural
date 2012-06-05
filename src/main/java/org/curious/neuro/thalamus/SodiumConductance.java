package org.curious.neuro.thalamus;

public class SodiumConductance implements Conductance {
	private float conductance;
	private float restingVoltage;
	public float activation;
	public float inactivation;
	private Float oldActivation;
	private Float oldInactivation;
	
	public SodiumConductance(float conductance, float restingVoltage, float initialVoltage) {
		this.conductance = conductance;
		this.restingVoltage = restingVoltage;
		oldActivation = null;
		oldInactivation = null;
		activation = alphaActivation(initialVoltage)/(alphaActivation(initialVoltage) + betaActivation(initialVoltage));
		inactivation = alphaInactivation(initialVoltage)/(alphaInactivation(initialVoltage) + betaInactivation(initialVoltage));
	}
	
	private float alphaActivation(float voltage) {
		return .1f*(voltage + 40)/(1 - (float)Math.exp(-(voltage + 40)/10));
	}
	
	private float betaActivation(float voltage) {
		return 4*(float)Math.exp(-(voltage + 65)/18);
	}
	
	private float alphaInactivation(float voltage) {
		return .07f*(float)Math.exp(-(voltage + 65)/20);
	}
	
	private float betaInactivation(float voltage) {
		return 1/(1 + (float)Math.exp(-(voltage + 35)/10));
	}
	
	public void simulate(float time, float voltage) {
		//System.out.println(alphaActivation(voltage) + " " + betaActivation(voltage) + " " + activation);
		oldActivation = activation;
		oldInactivation = inactivation;
		
		float k1, k2, k3, k4;
		
		k1 = time*(alphaActivation(voltage)*(1 - activation) - betaActivation(voltage)*activation);
		k2 = time*(alphaActivation(voltage)*(1 - (activation+k1/2)) - betaActivation(voltage)*(activation+k1/2));
		k3 = time*(alphaActivation(voltage)*(1 - (activation+k2/2)) - betaActivation(voltage)*(activation+k2/2));
		k4 = time*(alphaActivation(voltage)*(1 - (activation+k3)) - betaActivation(voltage)*(activation+k3));
		activation += (k1 + 2*k2 + 2*k3 + k4)/6;
		
		k1 = time*(alphaInactivation(voltage)*(1 - inactivation) - betaInactivation(voltage)*inactivation);
		k2 = time*(alphaInactivation(voltage)*(1 - (inactivation+k1/2)) - betaInactivation(voltage)*(inactivation+k1/2));
		k3 = time*(alphaInactivation(voltage)*(1 - (inactivation+k2/2)) - betaInactivation(voltage)*(inactivation+k2/2));
		k4 = time*(alphaInactivation(voltage)*(1 - (inactivation+k3)) - betaInactivation(voltage)*(inactivation+k3));
		inactivation += (k1 + 2*k2 + 2*k3 + k4)/6;
	}
	
	public void undo() {
		activation = oldActivation;
		inactivation = oldInactivation;
		oldActivation = null;
		oldInactivation = null;
	}

	public float getCurrent(float voltage) {
		//System.out.println(conductance + " " + activation + " " + inactivation);
		return conductance*(float)Math.pow(activation, 3)*inactivation*(voltage - restingVoltage);
	}
}
