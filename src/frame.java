import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class frame extends JFrame implements ActionListener, KeyListener, NativeKeyListener {


	//color
	public static final Color white = new Color(255,255,255);
	public static final Color light_black = new Color(61,61,61);
	public static final Color black = new Color(0,0,0);
	public static final Color green = new Color(108,254,0);
	public static final Color blue = new Color(0,150,255);

	public static final Color background_color = light_black;
	public static final Color foreground_color = white;


	//buttons
	JButton exit =  new JButton(new ImageIcon(this.getClass().getResource(("delete-icon.png"))));
	JButton info =  new JButton(new ImageIcon(getClass().getResource(("Button-Info-icon.png"))));
	JButton clear =  new JButton("clear");
	JButton start =  new JButton("start");
	JButton stop =  new JButton("stop");

	//comboboxes
	String []delays = new String[] {"300000ms","240000ms","180000ms","120000ms","60000ms","30000ms","15000ms","10000ms","7500ms",
			"5000ms","2500ms","1000ms","500ms","300ms","150ms","100ms","50ms","30ms","10ms","1ms"};
	JComboBox<String> delay_box = new JComboBox<String>(delays);
	JComboBox<String> delay_box2 = new JComboBox<String>(delays);

	//checkboxes
	JCheckBox loop_clicks = new JCheckBox("Loop clicks");

	//textarea and scrollpane
	JTextArea coordinates_area = new JTextArea();

	JScrollPane sp1 = new JScrollPane(coordinates_area);




	//positions list
	ArrayList<ArrayList<Integer>> mouse_coordinates = new ArrayList<ArrayList<Integer>>();

	//thread for clicker
	clicker_thread ct; 

	//move frame
	int pX,pY;







	/*
	 * constructor of the frame 
	 * all functions that needed on start are called
	 */
	frame(){
		create_frame();

		native_key_listener();

		comboboxes();
		scrollpane();
		buttons();
		checknoxes();
		move_frame();
	}





	/*
	 * creates main frame
	 * sets colors size etc.
	 */
	void create_frame() {
		setBounds(100,100,200,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);
		setOpacity(0.7f);
		setVisible(true);
		setAlwaysOnTop(true);
		this.setLayout(null);
		this.getContentPane().setBackground(background_color);
		setFocusable(true);
		addKeyListener(this);
		setLocationRelativeTo(null);

		mouse_coordinates.add(new ArrayList<Integer>());
		mouse_coordinates.add(new ArrayList<Integer>());
	}


	/*
	 * combobox options
	 */
	void comboboxes() {
		add(delay_box);
		delay_box.setBounds(10,175,100,20);
		delay_box.setFont(new Font("arial",Font.BOLD,15));
		delay_box.setBackground(background_color);
		delay_box.setForeground(white);
		delay_box.setFocusable(false);
		delay_box.setSelectedIndex(11);

		add(delay_box2);
		delay_box2.setBounds(10,150,100,20);
		delay_box2.setFont(new Font("arial",Font.BOLD,15));
		delay_box2.setBackground(background_color);
		delay_box2.setForeground(white);
		delay_box2.setFocusable(false);
		delay_box2.setSelectedIndex(11);
		delay_box2.setEnabled(false);
	}


	/*
	 * checkbox options
	 */
	void checknoxes() {
		add(loop_clicks);
		loop_clicks.setBounds(10,125,90,20);
		loop_clicks.setBackground(background_color);
		loop_clicks.setForeground(white);
		loop_clicks.setActionCommand("1");
		loop_clicks.setFocusable(false);
		loop_clicks.addActionListener(this);	

		loop_clicks.setSelected(false);
	}


	/*
	 * scrollpane and text area options
	 */
	void scrollpane() {
		add(sp1);
		sp1.setBounds(10,20,180,100);
		sp1.setBackground(background_color);

		coordinates_area.setBackground(background_color);
		coordinates_area.setForeground(foreground_color);
		coordinates_area.setFont(new Font("arial",Font.BOLD,15));
		coordinates_area.setCaretColor(foreground_color);
		coordinates_area.setEditable(false);
		coordinates_area.setFocusable(false);
	}


	/*
	 * button options
	 */
	void buttons() {
		add(exit);
		exit.setBounds(175,2,17,17);
		exit.addActionListener(this);		
		exit.setFocusable(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);

		add(info);
		info.setBounds(150,2,17,17);
		info.addActionListener(this);		
		info.setFocusable(false);
		info.setContentAreaFilled(false);
		info.setBorderPainted(false);

		add(clear);
		clear.setBounds(120,175,70,20);
		clear.addActionListener(this);		
		clear.setFocusable(false);
		clear.setContentAreaFilled(false);
		clear.setForeground(foreground_color);

		add(start);
		start.setBounds(120,125,70,20);
		start.addActionListener(this);		
		start.setFocusable(false);
		start.setContentAreaFilled(false);
		start.setForeground(foreground_color);

		add(stop);
		stop.setBounds(120,150,70,20);
		stop.addActionListener(this);		
		stop.setFocusable(false);
		stop.setContentAreaFilled(false);
		stop.setForeground(foreground_color);
	}







	/*
	 * gets pointers x and y coordinates and adds them to mouse_coordinates also writes new coordinates to the text area
	 */
	void record_position() {

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		Integer x = (int) b.getX();
		Integer y = (int) b.getY();

		mouse_coordinates.get(0).add(x);
		mouse_coordinates.get(1).add(y);

		coordinates_area.setText(coordinates_area.getText() + "x: " + x + " y: " + y + "\n");

	}


	/*
	 * clear positions saved in arraylist
	 */
	void clear_positions_list() {
		mouse_coordinates.get(0).clear();
		mouse_coordinates.get(1).clear();
		coordinates_area.setText("");
	}


	/*
	 * checks mouse_coordinates list and 
	 * starts clicking thread by creating a new instance of it 
	 * sends mouse_coordinates list, delays from comboboxes and loop clicks information
	 * 
	 */
	void start_clicking(){
		if(!mouse_coordinates.isEmpty()) {
			ct = new clicker_thread(mouse_coordinates,getdelay(delay_box),getdelay(delay_box2),loop_clicks.isSelected());
			ct.start();
		}

	}


	/*
	 * stops clicker thread by setting terminate value in thread and using join it prevents deadlocks
	 */
	void stop_clicking(){

		ct.terminate();

		try {
			ct.join();
		}
		catch (InterruptedException e) {
			System.out.println("thread interupted");
		}

	}


	/*
	 * gets delay from comboboxes string
	 */
	int getdelay(JComboBox<String> cb ) {
		return Integer.valueOf(delays[cb.getSelectedIndex()].substring(0,delays[cb.getSelectedIndex()].length()-2));
	}


	/*
	 *prints saved coordinates 
	 */
	void print_mouse_coordinates(ArrayList<ArrayList<Integer>> mouse_coordinates) {
		for (int i = 0; i < mouse_coordinates.get(0).size(); i++) {
			System.out.print("x: " + mouse_coordinates.get(0).get(i));
			System.out.println(" y: " + mouse_coordinates.get(1).get(i));
		}
	}





	/*
	 * JOptionPane for information
	 * html used for better look 
	 * 
	 * function makes frame not always on top so JOptionPane can be on top of it after JOptionPane opened 
	 * makes frame always on top again 
	 */
	void info() {

		//html for bold and dots
		setAlwaysOnTop(false);

		String msg = "<html><ul><li>Shortcuts:<br/>"
				+ "'a' add mouse position to click locations list<br/>"
				+ "'s' start clicking<br/>"
				+ "'z' stop clicking<br/>"
				+ "'x' clear locations<br/>"
				+ "'esc' exit<br/><br/>"
				+ "<li>Author: <b>Can Kurt</b></ul></html>";

		JLabel label = new JLabel(msg);
		label.setFont(new Font("arial", Font.PLAIN, 15));

		JOptionPane.showMessageDialog(null, label ,"Info", JOptionPane.INFORMATION_MESSAGE);

		setAlwaysOnTop(true);
	}


	/*
	 * frame is undecorated so this method is required for moving the frame on drag
	 */
	void move_frame() {
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				pX=me.getX();
				pY=me.getY();
			}
		});

		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent me){
				setLocation(getLocation().x+me.getX()-pX,getLocation().y+me.getY()-pY);
			}
		});
	}


	/*
	 * activate native keylistener mouse listener and mousemotionlisteners
	 * disable logger of jnativehook
	 */
	void native_key_listener() {
		// Initialze native hook.
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);

		//GlobalScreen.addNativeMouseListener(this);

		//GlobalScreen.addNativeMouseMotionListener(this);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}





	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * handle button presses 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exit) {
			System.exit(0);
		}
		if(e.getSource() == clear) {
			clear_positions_list();
		}
		if(e.getSource() == start) {
			start_clicking();
		}
		if(e.getSource() == stop) {
			stop_clicking();
		}
		if(e.getSource() == info) {
			info();
		}
		if(e.getSource() == loop_clicks) {
			if(!loop_clicks.isSelected()) {
				delay_box2.setEnabled(false);
			}
			else {
				delay_box2.setEnabled(true);
			}
		}

	}


	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 * 
	 * regular key listener for exit key
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}





	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}





	/*
	 * (non-Javadoc)
	 * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.keyboard.NativeKeyEvent)
	 * 
	 * native key listener for better functionality
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(e.getKeyCode() == NativeKeyEvent.VC_A) {
			record_position();
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_S) {
			start_clicking();
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_Z) {
			stop_clicking();
		}
		if(e.getKeyCode() == NativeKeyEvent.VC_X) {
			clear_positions_list();
		}

	}





	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
