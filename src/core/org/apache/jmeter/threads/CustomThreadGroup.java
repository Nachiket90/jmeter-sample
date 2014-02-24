package org.apache.jmeter.threads;

import java.io.Serializable;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.math.StatCalculatorLong;
import org.apache.log.Logger;


public class CustomThreadGroup extends ThreadGroup implements SampleListener, Serializable, TestListener {
	
	private static final Logger log = LoggingManager.getLoggerForClass();

	public double sd;
	public double mean;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 240L;

	public CustomThreadGroup()	{
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

	@Override
	public void testStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testStarted(String host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testEnded(String host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testIterationStart(LoopIterationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
