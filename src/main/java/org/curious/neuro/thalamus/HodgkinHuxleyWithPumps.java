package org.curious.neuro.thalamus;

import java.util.ArrayList;
import java.util.Collection;

public class HodgkinHuxleyWithPumps implements Neuron {
	private static final double potassiumDissociationConstant = 3.5f,
			sodiumDissociationConstant = 10, faradayConstant = 96485.3399f,
			gasConstant = 8.314472;
	private double stimulation, voltage, capacitance, leakConductance,
			leakReversalPotential, potassiumConductance,
			potassiumReversalPotential, sodiumConductance,
			sodiumReversalPotential, maxPumpCurrent,
			innerPotassiumConcentration, outerPotassiumConcentration,
			innerSodiumConcentration, outerSodiumConcentration, surfaceArea,
			innerVolume, outerVolume, tempurature, n, m, h, blebbedN, blebbedM, blebbedH;
	private boolean spiked, aboveThreshold;
	private double threshold;
	private double potassiumLeakConductance;
	private double sodiumLeakConductance;
	private double leftShift;
	private double blebbing;
	
	public HodgkinHuxleyWithPumps() {
		this.leftShift = 0;
		this.blebbing = 0;
		stimulation = 0;
		spiked = false;
		aboveThreshold = false;
	}
	
	public HodgkinHuxleyWithPumps(HodgkinHuxleySettings settings) {
		this.voltage = settings.voltage;
		this.threshold = settings.threshold;
		this.capacitance = settings.capacitance;
		this.leakConductance = settings.leakConductance;
		this.leakReversalPotential = settings.leakReversalPotential;
		this.potassiumConductance = settings.potassiumConductance;
		this.potassiumReversalPotential = settings.potassiumReversalPotential;
		this.potassiumLeakConductance = settings.potassiumLeakConductance;
		this.innerPotassiumConcentration = settings.innerPotassiumConcentration;
		this.outerPotassiumConcentration = settings.outerPotassiumConcentration;
		this.innerSodiumConcentration = settings.innerSodiumConcentration;
		this.outerSodiumConcentration = settings.outerSodiumConcentration;
		this.sodiumConductance = settings.sodiumConductance;
		this.sodiumReversalPotential = settings.sodiumReversalPotential;
		this.sodiumLeakConductance = settings.sodiumLeakConductance;
		this.maxPumpCurrent = settings.maxPumpCurrent;
		this.tempurature = settings.tempurature;
		this.innerVolume = settings.innerVolume;
		this.outerVolume = settings.outerVolume;
		this.surfaceArea = settings.surfaceArea;
		this.leftShift = settings.leftShift;
		this.blebbing = settings.blebbing;
		resetActivationVariables();
		stimulation = 0;
		spiked = false;
		aboveThreshold = false;
	}

	public HodgkinHuxleyWithPumps(double initialVoltage, double threshold,
			double capacitance, double leakConductance,
			double leakReversalPotential, double potassiumConductance,
			double potassiumReversalPotential, double sodiumConductance,
			double sodiumReversalPotential, double maxPumpCurrent,
			double potassiumLeakConductance, double sodiumLeakConductance,
			double innerPotassiumConcentration,
			double outerPotassiumConcentration,
			double innerSodiumConcentration, double outerSodiumConcentration,
			double tempurature, double innerVolume, double outerVolume, double surfaceArea, double leftShift, double blebbing) {
		this.voltage = initialVoltage;
		this.threshold = threshold;
		this.capacitance = capacitance;
		this.leakConductance = leakConductance;
		this.leakReversalPotential = leakReversalPotential;
		this.potassiumConductance = potassiumConductance;
		this.potassiumReversalPotential = potassiumReversalPotential;
		this.potassiumLeakConductance = potassiumLeakConductance;
		this.innerPotassiumConcentration = innerPotassiumConcentration;
		this.outerPotassiumConcentration = outerPotassiumConcentration;
		this.innerSodiumConcentration = innerSodiumConcentration;
		this.outerSodiumConcentration = outerSodiumConcentration;
		this.sodiumConductance = sodiumConductance;
		this.sodiumReversalPotential = sodiumReversalPotential;
		this.sodiumLeakConductance = sodiumLeakConductance;
		this.maxPumpCurrent = maxPumpCurrent;
		this.tempurature = tempurature;
		this.innerVolume = innerVolume;
		this.outerVolume = outerVolume;
		this.surfaceArea = surfaceArea;
		this.leftShift = leftShift;
		resetActivationVariables();
		this.blebbing = blebbing;
		stimulation = 0;
		spiked = false;
		aboveThreshold = false;
	}
	
	public void resetActivationVariables() {
		n = infinityN(voltage);
		m = infinityM(voltage);
		h = infinityH(voltage);
		blebbedN = infinityN(voltage + leftShift);
		blebbedM = infinityM(voltage + leftShift);
		blebbedH = infinityH(voltage + leftShift);
	}
	
	public double getPotassiumReversalPotential() {
		return potassiumReversalPotential;
	}
	
	public double getSodiumReversalPotential() {
		return sodiumReversalPotential;
	}
	
	public double getCapacitance() {
		return capacitance;
	}

	public void setCapacitance(double capacitance) {
		this.capacitance = capacitance;
	}

	public double getLeakConductance() {
		return leakConductance;
	}

	public void setLeakConductance(double leakConductance) {
		this.leakConductance = leakConductance;
	}

	public double getLeakReversalPotential() {
		return leakReversalPotential;
	}

	public void setLeakReversalPotential(double leakReversalPotential) {
		this.leakReversalPotential = leakReversalPotential;
	}

	public double getPotassiumConductance() {
		return potassiumConductance;
	}

	public void setPotassiumConductance(double potassiumConductance) {
		this.potassiumConductance = potassiumConductance;
	}

	public double getSodiumConductance() {
		return sodiumConductance;
	}

	public void setSodiumConductance(double sodiumConductance) {
		this.sodiumConductance = sodiumConductance;
	}

	public double getMaxPumpCurrent() {
		return maxPumpCurrent;
	}

	public void setMaxPumpCurrent(double maxPumpCurrent) {
		this.maxPumpCurrent = maxPumpCurrent;
	}

	public double getInnerPotassiumConcentration() {
		return innerPotassiumConcentration;
	}

	public void setInnerPotassiumConcentration(double innerPotassiumConcentration) {
		this.innerPotassiumConcentration = innerPotassiumConcentration;
	}

	public double getOuterPotassiumConcentration() {
		return outerPotassiumConcentration;
	}

	public void setOuterPotassiumConcentration(double outerPotassiumConcentration) {
		this.outerPotassiumConcentration = outerPotassiumConcentration;
	}

	public double getInnerSodiumConcentration() {
		return innerSodiumConcentration;
	}

	public void setInnerSodiumConcentration(double innerSodiumConcentration) {
		this.innerSodiumConcentration = innerSodiumConcentration;
	}

	public double getOuterSodiumConcentration() {
		return outerSodiumConcentration;
	}

	public void setOuterSodiumConcentration(double outerSodiumConcentration) {
		this.outerSodiumConcentration = outerSodiumConcentration;
	}

	public double getSurfaceArea() {
		return surfaceArea;
	}

	public void setSurfaceArea(double surfaceArea) {
		this.surfaceArea = surfaceArea;
	}

	public double getInnerVolume() {
		return innerVolume;
	}

	public void setInnerVolume(double innerVolume) {
		this.innerVolume = innerVolume;
	}

	public double getOuterVolume() {
		return outerVolume;
	}

	public void setOuterVolume(double outerVolume) {
		this.outerVolume = outerVolume;
	}

	public double getTempurature() {
		return tempurature;
	}

	public void setTempurature(double tempurature) {
		this.tempurature = tempurature;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getPotassiumLeakConductance() {
		return potassiumLeakConductance;
	}

	public void setPotassiumLeakConductance(double potassiumLeakConductance) {
		this.potassiumLeakConductance = potassiumLeakConductance;
	}

	public double getSodiumLeakConductance() {
		return sodiumLeakConductance;
	}

	public void setSodiumLeakConductance(double sodiumLeakConductance) {
		this.sodiumLeakConductance = sodiumLeakConductance;
	}

	public double getLeftShift() {
		return leftShift;
	}

	public double getBlebbing() {
		return blebbing;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
		resetActivationVariables();
	}

	public void setPotassiumReversalPotential(double potassiumReversalPotential) {
		this.potassiumReversalPotential = potassiumReversalPotential;
	}

	public void setSodiumReversalPotential(double sodiumReversalPotential) {
		this.sodiumReversalPotential = sodiumReversalPotential;
	}

	public void setBlebbing(double blebbing) {
		this.blebbing = blebbing;
	}
	
	public void setLeftShift(double leftShift) {
		this.leftShift = leftShift;
	}

	private static double alphaN(double voltage) {
		return .01f * (voltage + 55) / (1 - Math.exp(-(voltage + 55) / 10));
	}

	private static double betaN(double voltage) {
		return .125f * Math.exp(-(voltage + 65) / 80);
	}

	private static double infinityN(double voltage) {
		return alphaN(voltage) / (alphaN(voltage) + betaN(voltage));
	}

	private double derivativeN(double potential, double n) {
		return alphaN(potential) * (1 - n) - betaN(potential) * n;
	}

	public double getN() {
		return n;
	}

	private static double alphaM(double voltage) {
		return .1f * (voltage + 40) / (1 - Math.exp(-(voltage + 40) / 10));
	}

	private static double betaM(double voltage) {
		return 4 * Math.exp(-(voltage + 65) / 18);
	}

	private static double infinityM(double voltage) {
		return alphaM(voltage) / (alphaM(voltage) + betaM(voltage));
	}

	private double derivativeM(double potential, double m) {
		return alphaM(potential) * (1 - m) - betaM(potential) * m;
	}

	public double getM() {
		return m;
	}

	private static double alphaH(double voltage) {
		return .07f * Math.exp(-(voltage + 65) / 20);
	}

	private static double betaH(double voltage) {
		return 1 / (1 + Math.exp(-(voltage + 35) / 10));
	}

	private static double infinityH(double voltage) {
		return alphaH(voltage) / (alphaH(voltage) + betaH(voltage));
	}

	private double derivativeH(double potential, double h) {
		return alphaH(potential) * (1 - h) - betaH(potential) * h;
	}

	public double getH() {
		return h;
	}

	public double getLeakCurrent(double voltage) {
		return leakConductance * (voltage - leakReversalPotential);
	}

	public double getPotassiumCurrent(double voltage, double n,
			double reversalPotential) {
		return potassiumConductance * n * n * n * n
				* (voltage - reversalPotential);
	}

	public double getSodiumCurrent(double voltage, double m, double h, double blebbedM, double blebbedH,
			double reversalPotential) {
		return sodiumConductance * (m * m * m * h * (1 - blebbing) + blebbedM * blebbedM * blebbedM * blebbedH * blebbing)
				* (voltage - reversalPotential);
	}

	public double getPumpBaseCurrent(double outerPotassiumConcentration,
			double innerSodiumConcentration) {
		return maxPumpCurrent
				* Math.pow(1 + potassiumDissociationConstant
						/ outerPotassiumConcentration, -2)
				* Math.pow(1 + sodiumDissociationConstant
						/ innerSodiumConcentration, -3);
	}

	public double getPotassiumPumpLeakCurrent(double voltage,
			double reversalPotential) {
		return potassiumLeakConductance * (voltage - reversalPotential);
	}

	public double getSodiumPumpLeakCurrent(double voltage,
			double reversalPotential) {
		return sodiumLeakConductance * (voltage - reversalPotential);
	}

	private double getTotalPotassiumCurrent(double voltage, double n,
			double reversalPotential, double pumpBaseCurrent) {
		return getPotassiumCurrent(voltage, n, reversalPotential) - 2
				* pumpBaseCurrent
				+ getPotassiumPumpLeakCurrent(voltage, reversalPotential);
	}

	private double getTotalSodiumCurrent(double voltage, double m, double h, double blebbedM, double blebbedH,
			double reversalPotential, double pumpBaseCurrent) {
		return getSodiumCurrent(voltage, m, h, blebbedM, blebbedH, reversalPotential) + 3
				* pumpBaseCurrent
				+ getSodiumPumpLeakCurrent(voltage, reversalPotential);
	}

	private double derivativeInnerConcentration(double current) {
		return -1e-6 * current * surfaceArea / faradayConstant / innerVolume;
	}
	
	private double derivativeOuterConcentration(double current) {
		return 1e-6 * current * surfaceArea / faradayConstant / outerVolume;
	}

	private double calculateReversalPotential(double innerConcentration,
			double outerConcentration) {
		return -gasConstant * this.tempurature / faradayConstant * 1000
				* Math.log(innerConcentration / outerConcentration);
	}

	public double getVoltage() {
		return voltage;
	}

	public boolean isSpiked() {
		return spiked;
	}

	public boolean simulate(double time) {
		double pumpBaseCurrent;
		double potassiumReversalPotential, sodiumReversalPotential;
		double potassiumCurrent, sodiumCurrent, leakCurrent, totalCurrent;
		double nK1, nK2, nK3, nK4;
		double mK1, mK2, mK3, mK4;
		double hK1, hK2, hK3, hK4;
		double blebbedNK1, blebbedNK2, blebbedNK3, blebbedNK4;
		double blebbedMK1, blebbedMK2, blebbedMK3, blebbedMK4;
		double blebbedHK1, blebbedHK2, blebbedHK3, blebbedHK4;
		double innerPotassiumConcentrationK1, innerPotassiumConcentrationK2, innerPotassiumConcentrationK3, innerPotassiumConcentrationK4;
		double outerPotassiumConcentrationK1, outerPotassiumConcentrationK2, outerPotassiumConcentrationK3, outerPotassiumConcentrationK4;
		double innerSodiumConcentrationK1, innerSodiumConcentrationK2, innerSodiumConcentrationK3, innerSodiumConcentrationK4;
		double outerSodiumConcentrationK1, outerSodiumConcentrationK2, outerSodiumConcentrationK3, outerSodiumConcentrationK4;
		double voltageK1, voltageK2, voltageK3, voltageK4;

		potassiumReversalPotential = calculateReversalPotential(
				this.innerPotassiumConcentration,
				this.outerPotassiumConcentration);
		sodiumReversalPotential = calculateReversalPotential(
				this.innerSodiumConcentration, this.outerSodiumConcentration);
		pumpBaseCurrent = getPumpBaseCurrent(this.outerPotassiumConcentration,
				this.innerSodiumConcentration);
		potassiumCurrent = getTotalPotassiumCurrent(voltage, n,
				potassiumReversalPotential, pumpBaseCurrent);
		sodiumCurrent = getTotalSodiumCurrent(voltage, m, h, blebbedM, blebbedH,
				sodiumReversalPotential, pumpBaseCurrent);
		leakCurrent = getLeakCurrent(voltage);
		totalCurrent = potassiumCurrent + sodiumCurrent + leakCurrent + stimulation;
		nK1 = time * derivativeN(voltage, n);
		mK1 = time * derivativeM(voltage, m);
		hK1 = time * derivativeH(voltage, h);
		blebbedNK1 = time * derivativeN(voltage + leftShift, blebbedN);
		blebbedMK1 = time * derivativeM(voltage + leftShift, blebbedM);
		blebbedHK1 = time * derivativeH(voltage + leftShift, blebbedH);
		innerPotassiumConcentrationK1 = time
				* derivativeInnerConcentration(potassiumCurrent);
		outerPotassiumConcentrationK1 = time
				* derivativeOuterConcentration(potassiumCurrent);
		innerSodiumConcentrationK1 = time
				* derivativeInnerConcentration(sodiumCurrent);
		outerSodiumConcentrationK1 = time
				* derivativeOuterConcentration(sodiumCurrent);
		voltageK1 = time
				* (-totalCurrent / capacitance);
		
		potassiumReversalPotential = calculateReversalPotential(
				this.innerPotassiumConcentration + innerPotassiumConcentrationK1/2,
				this.outerPotassiumConcentration + outerPotassiumConcentrationK1/2);
		sodiumReversalPotential = calculateReversalPotential(
				this.innerSodiumConcentration + innerSodiumConcentrationK1/2, this.outerSodiumConcentration + outerSodiumConcentrationK1/2);
		pumpBaseCurrent = getPumpBaseCurrent(this.outerPotassiumConcentration + outerPotassiumConcentrationK1/2,
				this.innerSodiumConcentration + innerSodiumConcentrationK1/2);
		potassiumCurrent = getTotalPotassiumCurrent(voltage + voltageK1/2, n + nK1/2,
				potassiumReversalPotential, pumpBaseCurrent);
		sodiumCurrent = getTotalSodiumCurrent(voltage + voltageK1/2, m + mK1/2, h + hK1/2, blebbedM + blebbedMK1/2, blebbedH + blebbedHK1/2,
				sodiumReversalPotential, pumpBaseCurrent);
		leakCurrent = getLeakCurrent(voltage + voltageK1/2);
		totalCurrent = potassiumCurrent + sodiumCurrent + leakCurrent + stimulation;
		nK2 = time * derivativeN(voltage + voltageK1/2, n + nK1/2);
		mK2 = time * derivativeM(voltage + voltageK1/2, m + mK1/2);
		hK2 = time * derivativeH(voltage + voltageK1/2, h + hK1/2);
		blebbedNK2 = time * derivativeN(voltage + leftShift + voltageK1/2, blebbedN + blebbedNK1/2);
		blebbedMK2 = time * derivativeM(voltage + leftShift + voltageK1/2, blebbedM + blebbedMK1/2);
		blebbedHK2 = time * derivativeH(voltage + leftShift + voltageK1/2, blebbedH + blebbedHK1/2);
		innerPotassiumConcentrationK2 = time
				* derivativeInnerConcentration(potassiumCurrent);
		outerPotassiumConcentrationK2 = time
				* derivativeOuterConcentration(potassiumCurrent);
		innerSodiumConcentrationK2 = time
				* derivativeInnerConcentration(sodiumCurrent);
		outerSodiumConcentrationK2 = time
				* derivativeOuterConcentration(sodiumCurrent);
		voltageK2 = time
				* (-totalCurrent / capacitance);
		
		potassiumReversalPotential = calculateReversalPotential(
				this.innerPotassiumConcentration + innerPotassiumConcentrationK2/2,
				this.outerPotassiumConcentration + outerPotassiumConcentrationK2/2);
		sodiumReversalPotential = calculateReversalPotential(
				this.innerSodiumConcentration + innerSodiumConcentrationK2/2, this.outerSodiumConcentration + outerSodiumConcentrationK2/2);
		pumpBaseCurrent = getPumpBaseCurrent(this.outerPotassiumConcentration + outerPotassiumConcentrationK2/2,
				this.innerSodiumConcentration + innerSodiumConcentrationK2/2);
		potassiumCurrent = getTotalPotassiumCurrent(voltage + voltageK2/2, n + nK2/2,
				potassiumReversalPotential, pumpBaseCurrent);
		sodiumCurrent = getTotalSodiumCurrent(voltage + voltageK2/2, m + mK2/2, h + hK2/2, blebbedM + blebbedMK2/2, blebbedH + blebbedHK2/2,
				sodiumReversalPotential, pumpBaseCurrent);
		leakCurrent = getLeakCurrent(voltage + voltageK2/2);
		totalCurrent = potassiumCurrent + sodiumCurrent + leakCurrent + stimulation;
		nK3 = time * derivativeN(voltage + voltageK2/2, n + nK2/2);
		mK3 = time * derivativeM(voltage + voltageK2/2, m + mK2/2);
		hK3 = time * derivativeH(voltage + voltageK2/2, h + hK2/2);
		blebbedNK3 = time * derivativeN(voltage + leftShift + voltageK2/2, blebbedN + blebbedNK2/2);
		blebbedMK3 = time * derivativeM(voltage + leftShift + voltageK2/2, blebbedM + blebbedMK2/2);
		blebbedHK3 = time * derivativeH(voltage + leftShift + voltageK2/2, blebbedH + blebbedHK2/2);
		innerPotassiumConcentrationK3 = time
				* derivativeInnerConcentration(potassiumCurrent);
		outerPotassiumConcentrationK3 = time
				* derivativeOuterConcentration(potassiumCurrent);
		innerSodiumConcentrationK3 = time
				* derivativeInnerConcentration(sodiumCurrent);
		outerSodiumConcentrationK3 = time
				* derivativeOuterConcentration(sodiumCurrent);
		voltageK3 = time
				* (-totalCurrent / capacitance);
		
		potassiumReversalPotential = calculateReversalPotential(
				this.innerPotassiumConcentration + innerPotassiumConcentrationK3,
				this.outerPotassiumConcentration + outerPotassiumConcentrationK3);
		sodiumReversalPotential = calculateReversalPotential(
				this.innerSodiumConcentration + innerSodiumConcentrationK3, this.outerSodiumConcentration + outerSodiumConcentrationK3);
		pumpBaseCurrent = getPumpBaseCurrent(this.outerPotassiumConcentration + outerPotassiumConcentrationK3,
				this.innerSodiumConcentration + innerSodiumConcentrationK3);
		potassiumCurrent = getTotalPotassiumCurrent(voltage + voltageK3, n + nK3,
				potassiumReversalPotential, pumpBaseCurrent);
		sodiumCurrent = getTotalSodiumCurrent(voltage + voltageK3, m + mK3, h + hK3, blebbedM + blebbedMK3, blebbedH + blebbedHK3,
				sodiumReversalPotential, pumpBaseCurrent);
		leakCurrent = getLeakCurrent(voltage + voltageK3);
		totalCurrent = potassiumCurrent + sodiumCurrent + leakCurrent + stimulation;
		nK4 = time * derivativeN(voltage + voltageK3, n + nK3);
		mK4 = time * derivativeM(voltage + voltageK3, m + mK3);
		hK4 = time * derivativeH(voltage + voltageK3, h + hK3);
		blebbedNK4 = time * derivativeN(voltage + leftShift + voltageK3, blebbedN + blebbedNK3);
		blebbedMK4 = time * derivativeM(voltage + leftShift + voltageK3, blebbedM + blebbedMK3);
		blebbedHK4 = time * derivativeH(voltage + leftShift + voltageK3, blebbedH + blebbedHK3);
		innerPotassiumConcentrationK4 = time
				* derivativeInnerConcentration(potassiumCurrent);
		outerPotassiumConcentrationK4 = time
				* derivativeOuterConcentration(potassiumCurrent);
		innerSodiumConcentrationK4 = time
				* derivativeInnerConcentration(sodiumCurrent);
		outerSodiumConcentrationK4 = time
				* derivativeOuterConcentration(sodiumCurrent);
		voltageK4 = time
				* (-totalCurrent / capacitance);
		
		n += (nK1 + nK2 + nK3 + nK4)/6;
		m += (mK1 + mK2 + mK3 + mK4)/6;
		h += (hK1 + hK2 + hK3 + hK4)/6;
		blebbedN += (blebbedNK1 + blebbedNK2 + blebbedNK3 + blebbedNK4)/6;
		blebbedM += (blebbedMK1 + blebbedMK2 + blebbedMK3 + blebbedMK4)/6;
		blebbedH += (blebbedHK1 + blebbedHK2 + blebbedHK3 + blebbedHK4)/6;
		innerPotassiumConcentration += (innerPotassiumConcentrationK1 + innerPotassiumConcentrationK2 + innerPotassiumConcentrationK3 + innerPotassiumConcentrationK4)/6;
		outerPotassiumConcentration += (outerPotassiumConcentrationK1 + outerPotassiumConcentrationK2 + outerPotassiumConcentrationK3 + outerPotassiumConcentrationK4)/6;
		innerSodiumConcentration += (innerSodiumConcentrationK1 + innerSodiumConcentrationK2 + innerSodiumConcentrationK3 + innerSodiumConcentrationK4)/6;
		outerSodiumConcentration += (outerSodiumConcentrationK1 + outerSodiumConcentrationK2 + outerSodiumConcentrationK3 + outerSodiumConcentrationK4)/6;
		voltage += (voltageK1 + voltageK2 + voltageK3 + voltageK4)/6;
		this.potassiumReversalPotential = calculateReversalPotential(innerPotassiumConcentration, outerPotassiumConcentration);
		this.sodiumReversalPotential = calculateReversalPotential(innerSodiumConcentration, outerSodiumConcentration);

		if (!aboveThreshold) {
			spiked = voltage > threshold;
			aboveThreshold = spiked;
		} else {
			spiked = false;
			aboveThreshold = voltage > threshold;
		}

		return spiked;
	}

	public void setStimulation(double stimulation) {
		this.stimulation = stimulation;
	}

	public double getStimulation() {
		return stimulation;
	}
}
