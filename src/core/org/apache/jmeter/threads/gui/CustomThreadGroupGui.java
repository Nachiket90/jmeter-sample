package org.apache.jmeter.threads.gui;

import java.awt.event.ItemListener;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.CustomThreadGroup;

public class CustomThreadGroupGui extends ThreadGroupGui implements ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 240L;
	
	public CustomThreadGroupGui() {
		// TODO Auto-generated constructor stub
		super(false);
	}
	
	public String getLabelResource() {
        return "custom_thread_group_title"; // $NON-NLS-1$

    }
	public TestElement createTestElement() {
        CustomThreadGroup tg = new CustomThreadGroup();
        modifyTestElement(tg);
        return tg;
    }

}
