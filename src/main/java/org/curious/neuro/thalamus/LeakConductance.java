package org.curious.neuro.thalamus;

public class LeakConductance implements Conductance {
	private float conductance;
	private float restingVotlage;
	
	public LeakConductance(float conductance, float restingVoltage) {
		this.conductance = conductance;
		this.restingVotlage = restingVoltage;
	}
	
	public void simulate(float time, float voltage) {
		return;
	}
	
	public void undo() {
		return;
	}

	public float getCurrent(float voltage) {
		return conductance*(voltage - restingVotlage);
	}

}
