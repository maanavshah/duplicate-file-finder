import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Main {

	public static void main(String[] args) {
		launchFinder();
	}

	private static void launchFinder() {
		JFrame frame = new JFrame("Duplicate Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.add(new Panel());
		frame.setVisible(true);
		frame.setSize(700, 500);
		frame.setLocationRelativeTo(null);

	}

}
