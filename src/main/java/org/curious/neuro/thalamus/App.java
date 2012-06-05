package org.curious.neuro.thalamus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class App {
	private static float time = 0, resolution = .025f, timing = 1,
			maxVoltage = 100, minVoltage = -100, stimulation = 0, width = 4100;

	public static void main(String[] args) throws Exception {
		//final Neuron neuron = new Izhikevich(-70, .2f * -70, .02f, .2f,
		//		-50, 2);
		//final Neuron neuron = new IntegrateAndFire(1e-1f, 10, -65, -50,
		//				-65);
		float restingPotential = -59.9f;
		final LeakConductance leak = new LeakConductance(.25f, -54.4f);
		final PotassiumConductance potassium = new PotassiumConductance(36, -77, restingPotential);
		final SodiumConductance sodium = new SodiumConductance(120, 50, restingPotential);
		
		LinkedList<Conductance> conductances = new LinkedList<Conductance>();
		conductances.add(leak);
		conductances.add(potassium);
		conductances.add(sodium);
//		final HodgkinHuxley neuron = new HodgkinHuxley(restingPotential, 1,
//				.25f, -54.4f,
//				36, -77,
//				120, 50);
		final HodgkinHuxleyWithPumps neuron = new HodgkinHuxleyWithPumps(restingPotential, -15, 1,
				.5, -59.9,
				36, -81.3,
				120, 51.5, 90.9, .1, .25, 150, 6, 20, 154, 293.15, 3e-15, 3e-15, 1.5, 1);

		final XYSeries chatteringSeries = new XYSeries("Chattering");
		for (int i = 0; i < width; i++) {
			chatteringSeries.add(i*resolution, minVoltage);
		}
		final XYSeries stimulationSeries = new XYSeries("Stimulation");
		for (int i = 0; i < width; i++) {
			stimulationSeries.add(i*resolution, 0);
		}
		XYSeriesCollection collection = new XYSeriesCollection();
		collection.addSeries(chatteringSeries);
		collection.addSeries(stimulationSeries);
		XYPlot plot = new XYPlot();
		plot.setDomainAxis(new NumberAxis("time (ms)"));
		plot.setRangeAxis(new NumberAxis("potential (mv)"));
		plot.setRenderer(new XYLineAndShapeRenderer(true, false));
		plot.setDataset(collection);
		ChartPanel chart = new ChartPanel(new JFreeChart(plot));
		// ChartPanel panel = new
		// ChartPanel(ChartFactory.createXYLineChart("Neuron", "time (ms)",
		// "potential(mv)", collection, PlotOrientation.VERTICAL, true, true,
		// false));
		
		final XYSeries nSeries = new XYSeries("n");
		final XYSeries mSeries = new XYSeries("m");
		final XYSeries hSeries = new XYSeries("h");
		final XYSeries mhSeries = new XYSeries("mh");
		for (int i = 0; i < width; i++) {
			nSeries.add(i*resolution, 0);
			mSeries.add(i*resolution, 0);
			hSeries.add(i*resolution, 0);
			mhSeries.add(i*resolution, 0);
		}
		collection = new XYSeriesCollection();
		collection.addSeries(nSeries);
		collection.addSeries(mSeries);
		collection.addSeries(hSeries);
		collection.addSeries(mhSeries);
		plot = new XYPlot();
		plot.setDomainAxis(new NumberAxis("time (ms)"));
		plot.setRangeAxis(new NumberAxis("value (mv)"));
		plot.setRenderer(new XYLineAndShapeRenderer(true, false));
		plot.setDataset(collection);
		plot.getRangeAxis().setLowerBound(0);
		plot.getRangeAxis().setUpperBound(1);
		ChartPanel activationChart = new ChartPanel(new JFreeChart(plot));

		final JFrame frame = new JFrame("Izhikevich");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(chart);
		panel.add(activationChart);
		frame.setContentPane(panel);
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		final Timer timer = new Timer((int) timing, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				float end = time + timing;
				while (time < end) {
					int index = (int) (time / resolution);
					chatteringSeries.updateByIndex(
							index % chatteringSeries.getItemCount(),
							neuron.getVoltage());
					if ((int) (time/50) % 3 == 0) {
						stimulationSeries.updateByIndex(index
								% chatteringSeries.getItemCount(), stimulation);
						neuron.stimulate(stimulation);
					} else {
						stimulationSeries.updateByIndex(index
								% chatteringSeries.getItemCount(), 0);
						neuron.stimulate(0);
					}
					nSeries.updateByIndex(index
							% nSeries.getItemCount(), neuron.getN());
					mSeries.updateByIndex(index
							% mSeries.getItemCount(), neuron.getM());
					hSeries.updateByIndex(index
							% hSeries.getItemCount(), neuron.getH());
					mhSeries.updateByIndex(index
							% mhSeries.getItemCount(), (float)Math.pow(neuron.getM(), 3)*neuron.getH());
					neuron.simulate(resolution);
					time += resolution;
				}
				frame.repaint();
				//System.out.println(Math.pow(sodium.activation, 3)*sodium.inactivation);
			}
		});
		
		frame.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyReleased(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
					if(timer.isRunning()) {
						timer.stop();
					} else {
						timer.start();
					}
				}
			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
