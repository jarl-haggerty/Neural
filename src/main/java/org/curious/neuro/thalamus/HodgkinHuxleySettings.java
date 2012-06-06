package org.curious.neuro.thalamus;

public class HodgkinHuxleySettings {
	public double voltage, capacitance, leakConductance,
			leakReversalPotential, potassiumConductance,
			potassiumReversalPotential, sodiumConductance,
			sodiumReversalPotential, maxPumpCurrent,
			innerPotassiumConcentration, outerPotassiumConcentration,
			innerSodiumConcentration, outerSodiumConcentration, surfaceArea,
			innerVolume, outerVolume, tempurature;
	public double threshold;
	public double potassiumLeakConductance;
	public double sodiumLeakConductance;
	public double blebbing;
	public double leftShift;
	
	public HodgkinHuxleySettings() {
		maxPumpCurrent = 0;
		potassiumLeakConductance = 0;
		sodiumLeakConductance = 0;
		blebbing = 0;
		leftShift = 0;
	}
}
