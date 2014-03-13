package org.apache.jmeter.reporters.gui;


import java.awt.BorderLayout;

import org.apache.jmeter.reporters.CustomReporter;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

public class CustomReporterGui extends AbstractListenerGui {

    private static final long serialVersionUID = 240L;

    public CustomReporterGui() {
		// TODO Auto-generated constructor stub
    	super();
        init();
	}
        
    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);
    }

	@Override
	public String getLabelResource() {
		// TODO Auto-generated method stub
		return "custom_reporter_title";
	}

	public void configure(TestElement el) {
        super.configure(el);
    }
	
	@Override
	public TestElement createTestElement() {
		// TODO Auto-generated method stub
		CustomReporter customreporter = new CustomReporter();
        modifyTestElement(customreporter);
        return customreporter;
	}

	@Override
	public void modifyTestElement(TestElement customreporter) {
		// TODO Auto-generated method stub
		super.configureTestElement(customreporter);
		
	}
    
}