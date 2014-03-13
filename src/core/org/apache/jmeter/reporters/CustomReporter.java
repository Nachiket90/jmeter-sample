package org.apache.jmeter.reporters;

import java.io.Serializable;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.math.StatCalculatorLong;
import org.apache.log.Logger;


public class CustomReporter extends AbstractTestElement implements SampleListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double sd;
	public double mean;
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	public CustomReporter() {
		super();
	}
	@Override
	public void sampleOccurred(SampleEvent e) {
		// TODO Auto-generated method stub
		
		//get sample result into a variable 
				SampleResult s = e.getResult();
				
				//calculate stats 
				StatCalculatorLong calculator = new StatCalculatorLong();
				
				//this block is synchronized as at a time all samples should not
				//update calculated stats.
				synchronized (calculator) {
					calculator.addValue(s.getTime(), s.getSampleCount());
		            calculator.addBytes(s.getBytes());
		          
		            sd=calculator.getStandardDeviation();
		            mean=calculator.getMean();
		            log.info("CustomLog : standard deviation:- " + sd + "mean:- " + mean + "total count of samples:- " + s.getSampleCount() + "sample label:- " + s.getSampleLabel());
					
				}
				
				/*
				 * write logic for calcuation of standard deviation 
				 * 
				 * */
		
	}

	@Override
	public void sampleStarted(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sampleStopped(SampleEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
