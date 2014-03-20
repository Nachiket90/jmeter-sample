package org.apache.jmeter.threads.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.gui.action.AddToTree;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.JDateField;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.reporters.CustomReporter;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.LongProperty;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.CustomThreadGroup;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class CustomThreadGroupGui extends AbstractThreadGroupGui implements ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 240L;
	
	private LoopControlPanel loopPanel;

    private VerticalPanel mainPanel,looperPanel;

    private static final String THREAD_NAME = "Thread Field";

    private static final String RAMP_NAME = "Ramp Up Field";

    private JTextField threadInput;

    private JTextField rampInput;

    private JDateField start;
    
    private final boolean showDelayedStart;

    private JCheckBox delayedStart;

    private JCheckBox scheduler;
    
    private JTextField delay; // Relative start-up time
    
	
	public CustomThreadGroupGui() {
		// TODO Auto-generated constructor stub
		this.showDelayedStart = true;
    	initCustom();
    	initCustomGui(); //not passing loopcount for inifinite run
	}
	
	public String getLabelResource() {
        return "custom_thread_group_title"; // $NON-NLS-1$

    }
	public TestElement createTestElement() {
        CustomThreadGroup tg = new CustomThreadGroup();
        modifyTestElement(tg);
        CustomReporter cr=new CustomReporter();
        modifyTestElement(cr);
        JMeterTreeModel jm = new JMeterTreeModel();
        HashTree jmtree = jm.getTestPlan();
        jmtree.add(tg);
        jmtree.add(cr);
        return tg;
    }

	/**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
	@Override
	public void modifyTestElement(TestElement tg) {
		// TODO Auto-generated method stub
		 super.configureTestElement(tg);
	        if (tg instanceof AbstractThreadGroup) {
	            ((AbstractThreadGroup) tg).setSamplerController((LoopController) loopPanel.createTestElement());
	        }

	        tg.setProperty(AbstractThreadGroup.NUM_THREADS, threadInput.getText());
	        tg.setProperty(ThreadGroup.RAMP_TIME, rampInput.getText());
	        tg.setProperty(new LongProperty(ThreadGroup.START_TIME, start.getDate().getTime()));
	        if (showDelayedStart) {
	            tg.setProperty(ThreadGroup.DELAYED_START, delayedStart.isSelected(), false);
	        }
	        tg.setProperty(new BooleanProperty(ThreadGroup.SCHEDULER, scheduler.isSelected()));
	        tg.setProperty(ThreadGroup.DELAY, delay.getText());
		
	}

	 public void configure(TestElement tg) {
	        super.configure(tg);
	        threadInput.setText(tg.getPropertyAsString(AbstractThreadGroup.NUM_THREADS));
	        rampInput.setText(tg.getPropertyAsString(ThreadGroup.RAMP_TIME));
	        loopPanel.configure((TestElement) tg.getProperty(AbstractThreadGroup.MAIN_CONTROLLER).getObjectValue());
	        if (showDelayedStart) {
	            delayedStart.setSelected(tg.getPropertyAsBoolean(ThreadGroup.DELAYED_START));
	        }
	        scheduler.setSelected(tg.getPropertyAsBoolean(ThreadGroup.SCHEDULER));

	        if (scheduler.isSelected()) {
	            mainPanel.setVisible(true);
	        } else {
	            mainPanel.setVisible(false);
	        }

	        // Check if the property exists
	        String s = tg.getPropertyAsString(ThreadGroup.START_TIME);
	        if (s.length() == 0) {// Must be an old test plan
	            start.setDate(new Date());
	        } else {
	            start.setDate(new Date(tg.getPropertyAsLong(ThreadGroup.START_TIME)));
	        }
	        delay.setText(tg.getPropertyAsString(ThreadGroup.DELAY));
	    }

	    @Override
	    public void itemStateChanged(ItemEvent ie) {
	        if (ie.getItem().equals(scheduler)) {
	            if (scheduler.isSelected()) {
	                mainPanel.setVisible(true);
	            } else {
	                mainPanel.setVisible(false);
	            }
	        }
	    }
	    
	    private JPanel createCustomControllerPanel() {
	        loopPanel = new LoopControlPanel(false);
	        LoopController looper = (LoopController) loopPanel.createTestElement();
	        looper.setLoops(-1);
	        loopPanel.configure(looper);
	        return loopPanel;
	    }
	    
	    private JPanel createStartTimePanel() {
	        JPanel panel = new JPanel(new BorderLayout(5, 0));
	        JLabel label = new JLabel(JMeterUtils.getResString("starttime")); //$NON-NLS-1$
	        panel.add(label, BorderLayout.WEST);
	        start = new JDateField();
	        panel.add(start, BorderLayout.CENTER);
	        return panel;
	    }

	    private JPanel createDelayPanel() {
	        JPanel panel = new JPanel(new BorderLayout(5, 0));
	        JLabel label = new JLabel(JMeterUtils.getResString("delay")); // $NON-NLS-1$
	        panel.add(label, BorderLayout.WEST);
	        delay = new JTextField();
	        panel.add(delay, BorderLayout.CENTER);
	        return panel;
	    }

	    public void clearGui(){
	        super.clearGui();
	        initCustomGui();
	    }

	    private void initCustomGui(){
	    	threadInput.setText("1"); // $NON-NLS-1$
	        rampInput.setText("1"); // $NON-NLS-1$
	        looperPanel.setEnabled(false);
	        looperPanel.setVisible(false);
	        if (showDelayedStart) {
	            delayedStart.setSelected(false);
	        }
	        scheduler.setSelected(false);
	        Date today = new Date();
	        start.setDate(today);
	        delay.setText(""); // $NON-NLS-1$
	    }
	    
	    private void initCustom() {
	    	// THREAD PROPERTIES
	        VerticalPanel threadPropsPanel = new VerticalPanel();
	        threadPropsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
	                JMeterUtils.getResString("thread_properties"))); // $NON-NLS-1$

	        // NUMBER OF THREADS
	        JPanel threadPanel = new JPanel(new BorderLayout(5, 0));

	        JLabel threadLabel = new JLabel(JMeterUtils.getResString("number_of_threads")); // $NON-NLS-1$
	        threadPanel.add(threadLabel, BorderLayout.WEST);

	        threadInput = new JTextField(5);
	        threadInput.setName(THREAD_NAME);
	        threadLabel.setLabelFor(threadInput);
	        threadPanel.add(threadInput, BorderLayout.CENTER);

	        threadPropsPanel.add(threadPanel);

	        // RAMP-UP
	        JPanel rampPanel = new JPanel(new BorderLayout(5, 0));
	        JLabel rampLabel = new JLabel(JMeterUtils.getResString("ramp_up")); // $NON-NLS-1$
	        rampPanel.add(rampLabel, BorderLayout.WEST);

	        rampInput = new JTextField(5);
	        rampInput.setName(RAMP_NAME);
	        rampLabel.setLabelFor(rampInput);
	        rampPanel.add(rampInput, BorderLayout.CENTER);

	        threadPropsPanel.add(rampPanel);

	        // LOOP COUNT
	        looperPanel = new VerticalPanel();
	        
	        looperPanel.add(createCustomControllerPanel());
	        threadPropsPanel.add(looperPanel);
	        
	        // mainPanel.add(threadPropsPanel, BorderLayout.NORTH);
	        // add(mainPanel, BorderLayout.CENTER);

	        if (showDelayedStart) {
	            delayedStart = new JCheckBox(JMeterUtils.getResString("delayed_start")); // $NON-NLS-1$
	            threadPropsPanel.add(delayedStart);
	        }
	        scheduler = new JCheckBox(JMeterUtils.getResString("scheduler")); // $NON-NLS-1$
	        scheduler.addItemListener(this);
	        threadPropsPanel.add(scheduler);
	        mainPanel = new VerticalPanel();
	        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
	                JMeterUtils.getResString("scheduler_configuration"))); // $NON-NLS-1$
	        mainPanel.add(createStartTimePanel());
	        mainPanel.add(createDelayPanel());	        
	        mainPanel.setVisible(false);
	        VerticalPanel intgrationPanel = new VerticalPanel();
	        intgrationPanel.add(threadPropsPanel);
	        intgrationPanel.add(mainPanel);
	        add(intgrationPanel, BorderLayout.CENTER);
	    }
	    
	    
}
