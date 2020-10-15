package ssedit.SSvisual;

import javax.swing.JButton;
import javax.swing.SwingWorker;

public class SSvisualWorker extends SwingWorker<Object, Object>{
	private JButton button;
	
	public SSvisualWorker(JButton button) {
		this.button = button;
	}
	
	@Override
	public Object doInBackground() throws InterruptedException {
		SSvisual sv = new SSvisual();
		return null;
	}
	
	@Override
	protected void done() {
		button.setEnabled(true);
	}
}
