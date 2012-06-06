package org.curious.neuro.thalamus;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class App {
	private static double time = 20000, resolution = .001f, timing = 1,
			maxVoltage = 100, minVoltage = -100, stimulation = 0, width = 4100;
	private static HodgkinHuxleySettings settings;
	private static double writeResolution;

	private static ChartPanel getChartPanel(XYSeries... args) {
		XYSeriesCollection collection = new XYSeriesCollection();
		for (XYSeries arg : args) {
			collection.addSeries(arg);
		}
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(new NumberAxis("time (s)"));
		plot.setRangeAxis(new NumberAxis("potential (mv)"));
		plot.setRenderer(new XYLineAndShapeRenderer(true, false));
		plot.setDataset(collection);
		return new ChartPanel(new JFreeChart(plot));
	}
	
	private static ChartPanel getChartPanel(Iterable<XYSeries> args) {
		XYSeriesCollection collection = new XYSeriesCollection();
		for (XYSeries arg : args) {
			collection.addSeries(arg);
		}
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(new NumberAxis("time (s)"));
		plot.setRangeAxis(new NumberAxis("potential (mv)"));
		plot.setRenderer(new XYLineAndShapeRenderer(true, false));
		plot.setDataset(collection);
		return new ChartPanel(new JFreeChart(plot));
	}

	private static ChartPanel getControlStimulationPanel() {
		HodgkinHuxleyWithPumps neuron = new HodgkinHuxleyWithPumps(settings);

		XYSeries membranePotentialSeries = new XYSeries("Membrane Potential");
		XYSeries potassiumReversalPotentialSeries = new XYSeries(
				"Potassium Reversal Potential");
		XYSeries sodiumReversalPotentialSeries = new XYSeries(
				"Sodium Reversal Potential");

		double stimulationStart = 250, stimulationDuration = 333, stimulation = -12;
		for (int i = 0; i < 5000 / resolution; i++) {
			if (i % writeResolution == 0) {
				membranePotentialSeries.add(i * resolution / 1000,
						neuron.getVoltage());
				potassiumReversalPotentialSeries.add(i * resolution / 1000,
						neuron.getPotassiumReversalPotential());
				sodiumReversalPotentialSeries.add(i * resolution / 1000,
						neuron.getSodiumReversalPotential());
			}
			if (i * resolution > stimulationStart
					&& i * resolution < stimulationStart + stimulationDuration) {
				neuron.setStimulation(stimulation);
			} else {
				neuron.setStimulation(0);
			}
			neuron.simulate(resolution);
		}

		return getChartPanel(membranePotentialSeries,
				potassiumReversalPotentialSeries, sodiumReversalPotentialSeries);
	}

	private static ChartPanel getBlebbedPanel() {
		HodgkinHuxleyWithPumps neuron = new HodgkinHuxleyWithPumps(settings);

		XYSeries membranePotentialSeries = new XYSeries("Membrane Potential");
		XYSeries potassiumReversalPotentialSeries = new XYSeries(
				"Potassium Reversal Potential");
		XYSeries sodiumReversalPotentialSeries = new XYSeries(
				"Sodium Reversal Potential");

		double blebTime = 200, blebbing = 1, leftShift = 1.5;
		for (int i = 0; i < 20000 / resolution; i++) {
			if (i % writeResolution == 0) {
				membranePotentialSeries.add(i * resolution / 1000,
						neuron.getVoltage());
				potassiumReversalPotentialSeries.add(i * resolution / 1000,
						neuron.getPotassiumReversalPotential());
				sodiumReversalPotentialSeries.add(i * resolution / 1000,
						neuron.getSodiumReversalPotential());
			}
			if (i * resolution > blebTime) {
				neuron.setBlebbing(blebbing);
				neuron.setLeftShift(leftShift);
			} else {
				neuron.setBlebbing(0);
				neuron.setLeftShift(0);
			}
			neuron.simulate(resolution);
		}

		return getChartPanel(membranePotentialSeries,
				potassiumReversalPotentialSeries, sodiumReversalPotentialSeries);
	}

	public static ChartPanel getRateLeftShift(double stimulation) {
		double stimulationStart = 250, stimulationDuration = 5500, stimulationSettling = 500;

		Map<Integer, XYSeries> seriesMap = new HashMap<Integer, XYSeries>();
		seriesMap.put(2, new XYSeries("2"));
		seriesMap.put(5, new XYSeries("5"));
		seriesMap.put(10, new XYSeries("10"));
		seriesMap.put(15, new XYSeries("15"));
		seriesMap.put(20, new XYSeries("20"));
		seriesMap.put(25, new XYSeries("25"));
		seriesMap.put(30, new XYSeries("30"));

		for (Map.Entry<Integer, XYSeries> entry : seriesMap.entrySet()) {
			for (double blebbing = 0; blebbing < 1; blebbing += .01) {
				HodgkinHuxleyWithPumps neuron = new HodgkinHuxleyWithPumps(
						settings);
				neuron.setBlebbing(blebbing);
				neuron.setLeftShift(entry.getKey());
				int count = 0;
				for (int i = 0; i < (stimulationStart + stimulationDuration)
						/ resolution; i++) {
					if (i * resolution > stimulationStart) {
						neuron.setStimulation(stimulation);
						if (i * resolution > stimulationStart
								+ stimulationSettling
								&& neuron.isSpiked()) {
							count++;
						}
					} else {
						neuron.setStimulation(0);
					}
					neuron.simulate(resolution);
				}
				entry.getValue().add(blebbing, count * 1000
						/ (stimulationDuration - stimulationSettling));
			}
		}
		
		return getChartPanel(seriesMap.values());
	}
	
	public static ChartPanel getRateBlebbing(double stimulation) {
		double stimulationStart = 250, stimulationDuration = 5500, stimulationSettling = 500;

		Map<BigDecimal, XYSeries> seriesMap = new HashMap<BigDecimal, XYSeries>();
		seriesMap.put(new BigDecimal("1"), new XYSeries("1"));
		seriesMap.put(new BigDecimal(".8"), new XYSeries(".8"));
		seriesMap.put(new BigDecimal(".6"), new XYSeries(".6"));
		seriesMap.put(new BigDecimal(".3"), new XYSeries(".3"));
		seriesMap.put(new BigDecimal(".2"), new XYSeries(".2"));
		seriesMap.put(new BigDecimal(".1"), new XYSeries(".1"));
		seriesMap.put(new BigDecimal(".05"), new XYSeries(".05"));
		seriesMap.put(new BigDecimal("0"), new XYSeries("0"));

		for (Map.Entry<BigDecimal, XYSeries> entry : seriesMap.entrySet()) {
			for (double blebbing = 0; blebbing < 1; blebbing += .01) {
				HodgkinHuxleyWithPumps neuron = new HodgkinHuxleyWithPumps(
						settings);
				neuron.setBlebbing(blebbing);
				neuron.setLeftShift(entry.getKey().doubleValue());
				int count = 0;
				for (int i = 0; i < (stimulationStart + stimulationDuration)
						/ resolution; i++) {
					if (i * resolution > stimulationStart) {
						neuron.setStimulation(stimulation);
						if (i * resolution > stimulationStart
								+ stimulationSettling
								&& neuron.isSpiked()) {
							count++;
						}
					} else {
						neuron.setStimulation(0);
					}
					neuron.simulate(resolution);
				}
				entry.getValue().add(blebbing, count * 1000
						/ (stimulationDuration - stimulationSettling));
			}
		}
		
		return getChartPanel(seriesMap.values());
	}

	public static void main(String[] args) throws Exception {
		resolution = .001;
		writeResolution = .1 / resolution;

		settings = new HodgkinHuxleySettings();
		settings.voltage = -59.9;
		settings.threshold = -15;
		settings.capacitance = 1;
		settings.leakConductance = .5;
		settings.leakReversalPotential = -59.9;
		settings.potassiumConductance = 36;
		settings.potassiumReversalPotential = -81.3;
		settings.sodiumConductance = 120;
		settings.sodiumReversalPotential = 51.5;
		settings.maxPumpCurrent = 90.9;
		settings.potassiumLeakConductance = .1;
		settings.sodiumLeakConductance = .25;
		settings.innerPotassiumConcentration = 150;
		settings.outerPotassiumConcentration = 6;
		settings.innerSodiumConcentration = 20;
		settings.outerSodiumConcentration = 154;
		settings.tempurature = 293.15;
		settings.innerVolume = 3e-15;
		settings.outerVolume = 3e-15;
		settings.surfaceArea = 6e-8;

		ChartPanel controlChart = getControlStimulationPanel();
		ChartPanel blebbedChart = getBlebbedPanel();
		ChartPanel rateSpontaneousBlebbing = getRateBlebbing(0);
		ChartPanel rateSpontaneousLeftShift = getRateLeftShift(0);
		ChartPanel rateStimulatedBlebbing = getRateBlebbing(stimulation);
		ChartPanel rateStimulatedLeftShift = getRateLeftShift(stimulation);

		JPanel ratePanel = new JPanel();
		ratePanel.setLayout(new BoxLayout(ratePanel, BoxLayout.LINE_AXIS));
		ratePanel.add(rateSpontaneousBlebbing);
		ratePanel.add(rateSpontaneousLeftShift);
		ratePanel.add(rateStimulatedBlebbing);
		ratePanel.add(rateStimulatedLeftShift);

		JFrame frame = new JFrame("Experiments");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(controlChart);
		panel.add(blebbedChart);
		panel.add(ratePanel);
		frame.setContentPane(panel);
		Rectangle maxSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		frame.setSize(maxSize.width, maxSize.height);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		/*
		 * final XYSeries chatteringSeries = new XYSeries("Chattering"); for
		 * (int i = 0; i < width; i++) { chatteringSeries.add(i*resolution,
		 * minVoltage); } final XYSeries stimulationSeries = new
		 * XYSeries("Stimulation"); for (int i = 0; i < width; i++) {
		 * stimulationSeries.add(i*resolution, 0); } XYSeriesCollection
		 * collection = new XYSeriesCollection();
		 * collection.addSeries(chatteringSeries);
		 * collection.addSeries(stimulationSeries); XYPlot plot = new XYPlot();
		 * plot.setDomainAxis(new NumberAxis("time (ms)"));
		 * plot.setRangeAxis(new NumberAxis("potential (mv)"));
		 * plot.setRenderer(new XYLineAndShapeRenderer(true, false));
		 * plot.setDataset(collection); ChartPanel chart = new ChartPanel(new
		 * JFreeChart(plot)); // ChartPanel panel = new //
		 * ChartPanel(ChartFactory.createXYLineChart("Neuron", "time (ms)", //
		 * "potential(mv)", collection, PlotOrientation.VERTICAL, true, true, //
		 * false));
		 * 
		 * final XYSeries nSeries = new XYSeries("n"); final XYSeries mSeries =
		 * new XYSeries("m"); final XYSeries hSeries = new XYSeries("h"); final
		 * XYSeries mhSeries = new XYSeries("mh"); for (int i = 0; i < width;
		 * i++) { nSeries.add(i*resolution, 0); mSeries.add(i*resolution, 0);
		 * hSeries.add(i*resolution, 0); mhSeries.add(i*resolution, 0); }
		 * collection = new XYSeriesCollection(); collection.addSeries(nSeries);
		 * collection.addSeries(mSeries); collection.addSeries(hSeries);
		 * collection.addSeries(mhSeries); plot = new XYPlot();
		 * plot.setDomainAxis(new NumberAxis("time (ms)"));
		 * plot.setRangeAxis(new NumberAxis("value (mv)")); plot.setRenderer(new
		 * XYLineAndShapeRenderer(true, false)); plot.setDataset(collection);
		 * plot.getRangeAxis().setLowerBound(0);
		 * plot.getRangeAxis().setUpperBound(1); ChartPanel activationChart =
		 * new ChartPanel(new JFreeChart(plot));
		 * 
		 * final JFrame frame = new JFrame("Izhikevich"); JPanel panel = new
		 * JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		 * panel.add(chart); panel.add(activationChart);
		 * frame.setContentPane(panel); frame.setSize(1000, 600);
		 * frame.setLocationRelativeTo(null);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame.setVisible(true);
		 */

		/*
		 * final Timer timer = new Timer((int) timing, new ActionListener() {
		 * public void actionPerformed(ActionEvent arg0) { float end = time +
		 * timing; while (time < end) { int index = (int) (time / resolution);
		 * chatteringSeries.updateByIndex( index %
		 * chatteringSeries.getItemCount(), neuron.getVoltage()); if ((int)
		 * (time/50) % 3 == 0) { stimulationSeries.updateByIndex(index %
		 * chatteringSeries.getItemCount(), stimulation);
		 * neuron.stimulate(stimulation); } else {
		 * stimulationSeries.updateByIndex(index %
		 * chatteringSeries.getItemCount(), 0); neuron.stimulate(0); }
		 * nSeries.updateByIndex(index % nSeries.getItemCount(), neuron.getN());
		 * mSeries.updateByIndex(index % mSeries.getItemCount(), neuron.getM());
		 * hSeries.updateByIndex(index % hSeries.getItemCount(), neuron.getH());
		 * mhSeries.updateByIndex(index % mhSeries.getItemCount(),
		 * (float)Math.pow(neuron.getM(), 3)*neuron.getH());
		 * neuron.simulate(resolution); time += resolution; } frame.repaint();
		 * //System.out.println(Math.pow(sodium.activation,
		 * 3)*sodium.inactivation); } });
		 * 
		 * frame.addKeyListener(new KeyListener() {
		 * 
		 * public void keyPressed(KeyEvent arg0) { // TODO Auto-generated method
		 * stub
		 * 
		 * }
		 * 
		 * public void keyReleased(KeyEvent arg0) { if(arg0.getKeyCode() ==
		 * KeyEvent.VK_SPACE) { if(timer.isRunning()) { timer.stop(); } else {
		 * timer.start(); } } }
		 * 
		 * public void keyTyped(KeyEvent arg0) { // TODO Auto-generated method
		 * stub
		 * 
		 * }
		 * 
		 * });
		 */
	}
}
