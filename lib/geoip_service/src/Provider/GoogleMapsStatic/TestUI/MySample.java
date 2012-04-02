package Provider.GoogleMapsStatic.TestUI;

import Provider.GoogleMapsStatic.*;
import Task.*;
import Task.Manager.*;
import Task.ProgressMonitor.*;
import Task.Support.CoreSupport.*;
import Task.Support.GUISupport.*;
import com.jgoodies.forms.factories.*;
import info.clearthought.layout.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import javax.swing.text.Position;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.text.*;
import java.util.concurrent.*;

/**
 * MapLookup
 * <p>
 * http://code.google.com/apis/maps/documentation/staticmaps/index.html
 * </p>
 * 
 * 
 * Student Name: 	Juan Pablo Molina Matute 
 * Student email: 	jpmolinamatute@learn.senecac.on.ca 
 * Student UserID: 	jpmolinamatute 
 * Student Number: 	045 445 087
 * 
 * 
 * Student Name: 	Duc Giang 
 * Student email: 	dhgiang@learn.senecac.on.ca 
 * Student userID:	dhgiang
 * Student number: 	071 408 100
 * 
 * Note: Some modifications were made to the original author's codes to enhance features of the API
 * 
 * @author "Nazmul Idris" -- original
 * 
 * 
 *         {@link #main(String[] args) [main]} {@link #MySample() [MySample]}
 *         {@link #initComponents() [initComponents]} {@link #doInit() [doInit]}
 *         {@link #startTaskAction() [startTaskAction]} {@link #quitProgram()
 *         [quitProgram]} {@link #setNorth() [setNorth]} {@link #setSouth()
 *         [setSouth]} {@link #setEast() [setEast]} {@link #setWest() [setWest]}
 *         {@link #setNorthwest() [setNorthwest]} {@link #setSouthwest()
 *         [setSouthwest]} {@link #setSoutheast() [setSoutheast]}
 *         {@link #setChange() [setChange]} {@link #keyPressed(KeyEvent arg0)
 *         [keyPressed]} {@link #actionPerformed(ActionEvent arg0)
 *         [actionPerformed]} {@link #keyReleased(KeyEvent arg0) [keyReleased]}
 *         {@link #keyTyped(KeyEvent arg0) [keyTyped]} {@link #_setupTask()
 *         [_setupTask]} {@link #_displayImgInFrame() [_displayImgInFrame]}
 *         {@link #sout(final String s) [sout]}
 *         {@link #_initHook(SwingUIHookAdapter hook) [_initHook]}
 */

public class MySample extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// data members
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	/** reference to task */
	private SimpleTask<ByteBuffer> _task;
	/** this might be null. holds the image to display in a popup */
	private BufferedImage _img;
	private double chgLatitud;
	private double chgLongitude;
	private JButton btnGetMap;
	private JButton btnQuit;
	private JCheckBox checkboxRecvStatus;
	private JCheckBox checkboxSendStatus;

	/**
	 * @author Dhgiang
	 * Added a drop down combo box to allow user to select preset cities
	 */
	private JComboBox<Object> cities;

	private JLabel imgLbl;
	
	/***
	 * @author jpmolinamatute 
	 * added a new panel called controlPanel to allow map be 
	 * viewed from centre of the content Panel as oppose to sepearate window
	 */
	private JPanel controlPanel;

	private JProgressBar progressBar;
	private JSlider slider;
	private JTextArea ttaStatus;
	private JTextField ttfSizeW;
	private JTextField ttfLat;
	private JTextField ttfSizeH;
	private JTextField ttfLon;
	private JTextField ttfZoom;
	private JTextField ttfProgressMsg;

	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	// main method...
	// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXsXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	/**
	 * @author Nazmul
	 * @param args
	 */
	public static void main(String[] args) {
		Utils.createInEDT(MySample.class);
	}

	/**
	 * @author Nazmul
	 */
	private void doInit() {
		GUIUtils.setAppIcon(this, "burn.png");
		GUIUtils.centerOnScreen(this);
		setVisible(true);

		int W = 28, H = W;
		boolean blur = false;
		float alpha = .7f;

		try {
			btnGetMap.setIcon(ImageUtils.loadScaledBufferedIcon("ok1.png", W,
					H, blur, alpha));
			btnQuit.setIcon(ImageUtils.loadScaledBufferedIcon("charging.png",
					W, H, blur, alpha));
		} catch (Exception e) {
			System.out.println(e);
		}

		_setupTask();
	}

	/**
	 * create a test task and wire it up with a task handler that dumps output
	 * to the textarea
	 * 
	 * @author "Nazmul
	 */
	private void _setupTask() {

		TaskExecutorIF<ByteBuffer> functor = new TaskExecutorAdapter<ByteBuffer>() {
			@Override
			public ByteBuffer doInBackground(Future<ByteBuffer> swingWorker,
					SwingUIHookAdapter hook) throws Exception {

				_initHook(hook);

				// set the license key
				MapLookup
						.setLicenseKey("AIzaSyBGFBFwiI25og60Qc6ezdqfNcZFULzYW3o");
				// get the uri for the static map
				String uri = MapLookup.getMap(
						Double.parseDouble(ttfLat.getText()),
						Double.parseDouble(ttfLon.getText()),
						Integer.parseInt(ttfSizeW.getText()),
						Integer.parseInt(ttfSizeH.getText()),
						Integer.parseInt(ttfZoom.getText()));
				sout("Google Maps URI=" + uri);

				// get the map from Google
				GetMethod get = new GetMethod(uri);
				new HttpClient().executeMethod(get);

				ByteBuffer data = HttpUtils.getMonitoredResponse(hook, get);

				try {
					_img = ImageUtils.toCompatibleImage(ImageIO.read(data
							.getInputStream()));
					sout("converted downloaded data to image...");
				} catch (Exception e) {
					_img = null;
					sout("The URI is not an image. Data is downloaded, can't display it as an image.");
				}

				return data;
			}

			@Override
			public String getName() {
				return _task.getName();
			}
		};

		_task = new SimpleTask<ByteBuffer>(new TaskManager(), functor,
				"HTTP GET Task", "Download an image from a URL",
				AutoShutdownSignals.Daemon);

		_task.setTaskHandler(new SimpleTaskHandler<ByteBuffer>() {
			@Override
			public void beforeStart(AbstractTask task) {
				sout(":: taskHandler - beforeStart");
			}

			@Override
			public void started(AbstractTask task) {
				sout(":: taskHandler - started ");
			}

			/**
			 * {@link SampleApp#_initHook} adds the task status listener, which
			 * is removed here
			 */
			@Override
			public void stopped(long time, AbstractTask task) {
				sout(":: taskHandler [" + task.getName() + "]- stopped");
				sout(":: time = " + time / 1000f + "sec");
				task.getUIHook().clearAllStatusListeners();
			}

			@Override
			public void interrupted(Throwable e, AbstractTask task) {
				sout(":: taskHandler [" + task.getName() + "]- interrupted - "
						+ e.toString());
			}

			@Override
			public void ok(ByteBuffer value, long time, AbstractTask task) {
				sout(":: taskHandler [" + task.getName() + "]- ok - size="
						+ (value == null ? "null" : value.toString()));
				if (_img != null) {
					_displayImgInFrame();
				}
			}

			@Override
			public void error(Throwable e, long time, AbstractTask task) {
				sout(":: taskHandler [" + task.getName() + "]- error - "
						+ e.toString());
			}

			@Override
			public void cancelled(long time, AbstractTask task) {
				sout(" :: taskHandler [" + task.getName() + "]- cancelled");
			}
		});
	}

	/**
	 * @author Nazmul
	 * @param hook
	 * @return
	 */
	private SwingUIHookAdapter _initHook(SwingUIHookAdapter hook) {
		hook.enableRecieveStatusNotification(checkboxRecvStatus.isSelected());
		hook.enableSendStatusNotification(checkboxSendStatus.isSelected());

		hook.setProgressMessage(ttfProgressMsg.getText());

		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUIHookAdapter.PropertyList type = ProgressMonitorUtils
						.parseTypeFrom(evt);
				int progress = ProgressMonitorUtils.parsePercentFrom(evt);
				String msg = ProgressMonitorUtils.parseMessageFrom(evt);

				progressBar.setValue(progress);
				progressBar.setString(type.toString());

				sout(msg);
			}
		};

		hook.addRecieveStatusListener(listener);
		hook.addSendStatusListener(listener);
		hook.addUnderlyingIOStreamInterruptedOrClosed(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				sout(evt.getPropertyName() + " fired!!!");
			}
		});

		return hook;
	}

	/**
	 * @author Nazmul
	 */
	private void _displayImgInFrame() {

		imgLbl.setIcon(new ImageIcon(_img));

		imgLbl.setToolTipText(MessageFormat.format(
				"<html>Image downloaded from URI<br>size: w={0}, h={1}</html>",
				_img.getWidth(), _img.getHeight()));
	}

	/**
	 * @author Nazmul
	 * 
	 *         simply dump status info to the textarea
	 */
	private void sout(final String s) {
		Runnable soutRunner = new Runnable() {
			@Override
			public void run() {
				if (ttaStatus.getText().equals("")) {
					ttaStatus.setText(s);
				} else {
					ttaStatus.setText(ttaStatus.getText() + "\n" + s);
				}
			}
		};

		if (ThreadUtils.isInEDT()) {
			soutRunner.run();
		} else {
			SwingUtilities.invokeLater(soutRunner);
		}
	}

	/**
	 * @author Nazmul
	 */

	private void startTaskAction() {
		try {
			_task.execute();
			controlPanel.requestFocus(); // @ jpmolinamatute -- used controlPanel instead of contentPanel
		} catch (TaskException e) {
			sout(e.getMessage());
		}
	}

	/**
	 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 * 
	 * @author Nazmul
	 * 
	 * modified by: dhgiang Friday, March 30th 15:38 PM EST
	 * note: suggested to my partner Juan to have the app load map right away instead of having user to click get map button
	 * 
	 * constructor
	 * 
	 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 */

	public MySample() {
		initComponents();
		doInit();
		startTaskAction();  // Added this in so that the user does not have to click on Get Map once program loads
							// The map will automatically display once the program runs.
	}

	/**
	 * @author Nazmul
	 */

	private void quitProgram() {
		_task.shutdown();
		System.exit(0);
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward north
	 */
	private void setNorth() {
		String var1 = ttfLat.getText();
		Double latitude = Double.valueOf(var1.trim()).doubleValue();

		latitude += chgLatitud;
		if (latitude > 90.00) {
			latitude = 90.00;
		}

		ttfLat.setText(latitude.toString());
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward south
	 */
	private void setSouth() {
		String var1 = ttfLat.getText();
		Double latitude = Double.valueOf(var1.trim()).doubleValue();

		latitude -= chgLatitud;
		if (latitude < -90.00) {
			latitude = -90.00;
		}

		ttfLat.setText(latitude.toString());
	}

	/**
	 * @author jpmolinamatute
	 * 
	 *  this method is going to change the Longitude toward east
	 */
	private void setEast() {
		String var1 = ttfLon.getText();
		Double longitude = Double.valueOf(var1.trim()).doubleValue();

		longitude += chgLongitude;
		if (longitude > 180.00) {
			longitude = 180.00;
		}

		ttfLon.setText(longitude.toString());
	}

	/**
	 * @author jpmolinamatute
	 * 
	 *  this method is going to change the Longitude toward west
	 */
	private void setWest() {
		String var1 = ttfLon.getText();
		Double longitude = Double.valueOf(var1.trim()).doubleValue();

		longitude -= chgLongitude;
		if (longitude < -180.00) {
			longitude = -180.00;
		}

		ttfLon.setText(longitude.toString());
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward north
	 * this method is going to change the Longitude toward west
	 */
	private void setNorthwest() {
		setNorth();
		setWest();
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward north
	 * this method is going to change the Longitude toward east
	 */
	private void setNortheast() {
		setNorth();
		setEast();
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward south
	 * this method is going to change the Longitude toward west
	 */
	private void setSouthwest() {
		setSouth();
		setWest();
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is going to change the Latitude toward south
	 * this method is going to change the Longitude toward east
	 */
	private void setSoutheast() {
		setSouth();
		setEast();
	}

	/**
	 * @author jpmolinamatute
	 * 
	 * this method is set the rules for how much Latitude and Longitude are going to change according to the zoom
	 */
	private void setChange() {
		String var1 = ttfZoom.getText();
		Integer zoom = Integer.valueOf(var1.trim()).intValue();
		if (zoom >= 1 && zoom < 4) {
			chgLatitud = 20.00;
			chgLongitude = chgLatitud * 2;

		} else if (zoom >= 4 && zoom < 7) {
			chgLatitud = 10.00;
			chgLongitude = chgLatitud * 2;

		} else if (zoom >= 7 && zoom < 10) {
			chgLatitud = 5.00;
			chgLongitude = chgLatitud * 2;

		} else if (zoom >= 10 && zoom < 13) {
			chgLatitud = 2.50;
			chgLongitude = chgLatitud * 2;

		} else if (zoom >= 13 && zoom < 16) {
			chgLatitud = 1.25;
			chgLongitude = chgLatitud * 2;

		} else if (zoom >= 16 && zoom < 20) {
			chgLatitud = 0.75;
			chgLongitude = chgLatitud * 2;

		}
	}

	/**
	 * @author Nazmul
	 */
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		ttfSizeW = new JTextField("512");
		ttfLat = new JTextField("45.5");
		btnGetMap = new JButton("Get Map");
		btnQuit = new JButton("Quit");
		ttfSizeH = new JTextField("512");
		ttfLon = new JTextField("-73.55");
		ttfZoom = new JTextField("14");
		ttaStatus = new JTextArea();
		checkboxRecvStatus = new JCheckBox();
		checkboxSendStatus = new JCheckBox();
		ttfProgressMsg = new JTextField();
		progressBar = new JProgressBar();
		imgLbl = new JLabel();
		
		/***
		 * @author Dhgiang, jpmolinamatute
		 * Created a slider, zoom in/out buttons, conbo box (drop down listbox) for city selection,
		 * a panel to group the zoom buttons and the slider bar
		 */
		slider = new JSlider(0, 19, 14);
		controlPanel = new JPanel(new GridBagLayout());							// controlPanel was created by jpmolinamatute 
		cities = new JComboBox<Object>(new String[] { "Montreal", "Toronto",	// the place setting where the combo box is now used to be a text field
				"Vancouver", "New York City", "Caracas", "Hong Kong" });		// for license key, but it was removed to accommodate space for combo box 
																				// @author Dhgiang

		JPanel panel1 = new JPanel();
		JPanel contentPanel = new JPanel();
		JPanel btnPanel = new JPanel();
		JPanel dialogPane = new JPanel();
		
		
		JLabel label1 = new JLabel("Select City");	// this used to be the label for license key, it was changed to label as 'select city'
		JLabel label2 = new JLabel("Size Width");
		JLabel label3 = new JLabel("Size Height");
		JLabel label4 = new JLabel("Latitude");
		JLabel label5 = new JLabel("Longitude");
		JLabel label6 = new JLabel("Zoom");
		
		JButton btnZoomIn = new JButton("-");
		JButton btnZoomOut = new JButton("+");
		
		/***
		 * @author jpmolinamatute
		 * Created 8 cardinal points: N,NE,NW; S,SE,SW; E, W; 
		 */
		JButton btnSE = new JButton("SE");
		JButton btnS = new JButton("S");
		JButton btnSW = new JButton("SW");
		JButton btnE = new JButton("E");
		JButton btnW = new JButton("W");
		JButton btnNE = new JButton("NE");
		JButton btnN = new JButton("N");
		JButton btnNW = new JButton("NW");

		/*** 
		 * @author JPMolinaMatute
		 * Creaetd a spaceControl GridBagConstraints object to manage 
		 * the cardinal points, and add KeyListener so users can use the
		 * 8 cardinal keys on the num pad; initialize dimensions
		 * 
		 */
		GridBagConstraints spaceControl = new GridBagConstraints();
		spaceControl.insets = new Insets(5, 5, 5, 5);
		controlPanel.addKeyListener(this);						
		controlPanel.setSize(new Dimension(512, 512));
		// ---- My Combo Boxes for different city coordinates ----//

		// ======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Google Static Maps");
		setIconImage(null);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setOpaque(false);
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setOpaque(false);
				contentPanel.setLayout(new TableLayout(new double[][] {
						{ TableLayoutConstants.FILL },
						{ TableLayoutConstants.PREFERRED,
								TableLayoutConstants.FILL,
								TableLayoutConstants.PREFERRED } }));
				((TableLayout) contentPanel.getLayout()).setHGap(5);
				((TableLayout) contentPanel.getLayout()).setVGap(5);

				// ======== panel1 ========
				{
					panel1.setOpaque(false);
					panel1.setBorder(new CompoundBorder(new TitledBorder(
							"Configure the inputs to Google Static Maps"),
							Borders.DLU2_BORDER));
					panel1.setLayout(new TableLayout(new double[][] {
							{ 0.17, 0.17, 0.17, 0.17, 0.05,
									TableLayoutConstants.FILL },
							{ TableLayoutConstants.PREFERRED,
									TableLayoutConstants.PREFERRED,
									TableLayoutConstants.PREFERRED } }));
					((TableLayout) panel1.getLayout()).setHGap(5);
					((TableLayout) panel1.getLayout()).setVGap(5);

					label1.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label1, new TableLayoutConstraints(0, 2, 0, 2,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));
					// ---- label2 ----
					label2.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label2, new TableLayoutConstraints(0, 0, 0, 0,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- ttfSizeW ----

					panel1.add(ttfSizeW, new TableLayoutConstraints(1, 0, 1, 0,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- label4 ----

					label4.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label4, new TableLayoutConstraints(2, 0, 2, 0,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- ttfLat ----

					panel1.add(ttfLat, new TableLayoutConstraints(3, 0, 3, 0,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- btnGetMap ----
					btnGetMap.setHorizontalAlignment(SwingConstants.LEFT);
					btnGetMap.setMnemonic('G');
					btnGetMap.setActionCommand("getMap");
					btnGetMap.addActionListener(this);
					panel1.add(btnGetMap, new TableLayoutConstraints(5, 0, 5,
							0, TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- label3 ----
					label3.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label3, new TableLayoutConstraints(0, 1, 0, 1,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- ttfSizeH ----

					panel1.add(ttfSizeH, new TableLayoutConstraints(1, 1, 1, 1,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- label5 ----
					label5.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label5, new TableLayoutConstraints(2, 1, 2, 1,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- ttfLon ----

					panel1.add(ttfLon, new TableLayoutConstraints(3, 1, 3, 1,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- btnQuit ----
					btnQuit.setMnemonic('Q');
					btnQuit.setHorizontalAlignment(SwingConstants.LEFT);
					btnQuit.setHorizontalTextPosition(SwingConstants.RIGHT);
					btnQuit.setActionCommand("quit");
					btnQuit.addActionListener(this);

					panel1.add(btnQuit, new TableLayoutConstraints(5, 1, 5, 1,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					/***
					 * @author Dhgiang
					 * Added an anonymous inner class for ItemLister and event handling for the combo box
					 * Used the switch case condition handling to set coordinates based on the city selected
					 * Juan helped modified this method by adding the startTaskAction() at the end so 
					 * users don't have to click on Get Map button.
					 */
					
					cities.setSelectedIndex(0); // initialize the city selection item to 0 (or the first item)		
					
					cities.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							Integer z = cities.getSelectedIndex();
							switch (z) {
							case 0:
								ttfLat.setText("45.5");
								ttfLon.setText("-73.55");
								break;
							case 1:
								ttfLat.setText("43.65");
								ttfLon.setText("-79.38");
								break;
							case 2:
								ttfLat.setText("49.2505");
								ttfLon.setText("-123.1119");
								break;
							case 3:
								ttfLat.setText("40.7142");
								ttfLon.setText("-74.0064");
								break;
							case 4:
								ttfLat.setText("10.4901");
								ttfLon.setText("-66.9151");
								break;
							case 5:
								ttfLat.setText("22.257");
								ttfLon.setText("114.2");
								break;
							default:
								break;
							}
							startTaskAction();
						}
					});
					
					/***
					 * @author Dhgiang
					 * Added the combo box to the panel
					 */
					panel1.add(cities, new TableLayoutConstraints(1, 2, 1, 2,
							TableLayoutConstraints.FULL,
							TableLayoutConstraints.FULL));

					// ---- label6 ----
					label6.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label6, new TableLayoutConstraints(2, 2, 2, 2,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));

					// ---- ttfZoom ----

					panel1.add(ttfZoom, new TableLayoutConstraints(3, 2, 3, 2,
							TableLayoutConstants.FULL,
							TableLayoutConstants.FULL));
				}
				{
					btnPanel.setOpaque(false);
					btnPanel.setLayout(new GridLayout(0, 3));

					
					/****
					 * @author Dhgiang
					 * Initializing the zoom IN / OUT buttons with proper parameters and
					 * adding them to the btnPanel of Panel1 (panel within a panel)
					 */
					// ---- btnZoomIn ----
					btnZoomIn.setHorizontalAlignment(SwingConstants.LEFT);
					btnZoomIn.setHorizontalTextPosition(SwingConstants.RIGHT);
					btnZoomIn.setActionCommand("Zoomin");
					btnZoomIn.addActionListener(this);
					btnPanel.add(btnZoomIn, new TableLayoutConstraints(5, 2, 5,
							2, TableLayoutConstraints.FULL,
							TableLayoutConstraints.FULL));

					// ---- btnZoomOut ----
					btnZoomOut.setHorizontalAlignment(SwingConstants.RIGHT);
					btnZoomOut.setHorizontalTextPosition(SwingConstants.RIGHT);
					btnZoomOut.setActionCommand("Zoomout");
					btnZoomOut.addActionListener(this);

					btnPanel.add(btnZoomOut, new TableLayoutConstraints(5, 2,
							5, 2, TableLayoutConstraints.FULL,
							TableLayoutConstraints.FULL));

					/***
					 * @author Dhgiang
					 * Having created a new JSlider slider object, maximum & minimum values 
					 * are initialized along with incremental values
					 * 
					 */
					// ---- slider -----
					slider.setMaximum(19);
					slider.setMinimum(0);
					slider.setPaintTicks(true);
					slider.setMajorTickSpacing(19);
					slider.setMinorTickSpacing(1);
					slider.setPaintTrack(false);
					slider.createStandardLabels(4, 0);

					/***
					 * @author Dhgiang
					 * Added a ChangeListener to the slider so that 
					 * 1) the slider moves left if (-) button is clicked, slider moves right if (+) is clicked
					 * 2) and zoom values will increase or decrease if slider bar is shifted left or right accordingly
					 * 3) after the user releases the click button, the map will display automatically with the values
					 *    set by the slider bar, without having the user to click on get map button
					 */
					slider.addChangeListener(new ChangeListener() {
						@Override
						public void stateChanged(ChangeEvent arg0) {
							Integer a = slider.getValue();

							if (a >= 0 && a <= 19) {
								ttfZoom.setText(a.toString());
								startTaskAction();
							}

						}

					});
					slider.setBorder(null);
					
					/***
					 * @author Dhgiang
					 * Added the slider bar to the button panel
					 */
					btnPanel.add(slider, new TableLayoutConstraints(5, 2, 5, 2,
							TableLayoutConstraints.FULL,
							TableLayoutConstraints.FULL));
				}
				
				/***
				 * @author Dhgiang
				 * Adding the button panel to panel1
				 */
				panel1.add(btnPanel, new TableLayoutConstraints(5, 2, 5, 2,
						TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				contentPanel.add(panel1, new TableLayoutConstraints(0, 0, 0, 0,
						TableLayoutConstants.FULL, TableLayoutConstants.FULL));

				/***
				 * @author jpmolinamatute
				 * Initializing coordinates for the cardinal points
				 * Adding the cardinal points button to the control panel
				 */
				// ---- Cardinals points -----
				btnNW.setActionCommand("Northwest");
				btnNW.addActionListener(this);
				spaceControl.gridx = 0;
				spaceControl.gridy = 0;
				controlPanel.add(btnNW, spaceControl);

				btnN.setActionCommand("North");
				btnN.addActionListener(this);
				spaceControl.gridx = 3;
				spaceControl.gridy = 0;
				controlPanel.add(btnN, spaceControl);

				btnNE.setActionCommand("Northeast");
				btnNE.addActionListener(this);
				spaceControl.gridx = 6;
				spaceControl.gridy = 0;
				controlPanel.add(btnNE, spaceControl);

				btnW.setActionCommand("West");
				btnW.addActionListener(this);
				spaceControl.gridx = 0;
				spaceControl.gridy = 3;
				controlPanel.add(btnW, spaceControl);

				spaceControl.gridx = 3;
				spaceControl.gridy = 3;
				controlPanel.add(imgLbl, spaceControl);

				btnE.setActionCommand("East");
				btnE.addActionListener(this);
				spaceControl.gridx = 6;
				spaceControl.gridy = 3;
				controlPanel.add(btnE, spaceControl);

				btnSW.setActionCommand("Southwest");
				btnSW.addActionListener(this);
				spaceControl.gridx = 0;
				spaceControl.gridy = 6;
				controlPanel.add(btnSW, spaceControl);

				btnS.setActionCommand("South");
				btnS.addActionListener(this);
				spaceControl.gridx = 3;
				spaceControl.gridy = 6;
				controlPanel.add(btnS, spaceControl);

				btnSE.setActionCommand("Southeast");
				btnSE.addActionListener(this);
				spaceControl.gridx = 6;
				spaceControl.gridy = 6;
				controlPanel.add(btnSE, spaceControl);

				contentPanel.add(controlPanel, new TableLayoutConstraints(0, 1,
						0, 1, TableLayoutConstants.FULL,
						TableLayoutConstants.FULL));

			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}

		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(700, 800);

		setLocationRelativeTo(null);
	}

	// JFormDesigner - End of component initialization
	// //GEN-END:initComponents

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license

	// JFormDesigner - End of variables declaration //GEN-END:variables

	/**
	 * @author jpmolinamatute
	 * 
	 * this method control the use the longitude and latitud using the numerical  and arrows keyboard
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		char keyLetter = arg0.getKeyChar();
		int keyCode = arg0.getKeyCode();
		setChange();

		if (keyLetter == '8' || keyCode == 38) {
			setNorth();
			startTaskAction();

		} else if (keyLetter == '6' || keyCode == 39) {
			setEast();
			startTaskAction();

		} else if (keyLetter == '2' || keyCode == 40) {
			setSouth();
			startTaskAction();

		} else if (keyLetter == '4' || keyCode == 37) {
			setWest();
			startTaskAction();
		} else if (keyLetter == '7') {
			setNorthwest();
			startTaskAction();
		} else if (keyLetter == '9') {
			setNortheast();
			startTaskAction();
		} else if (keyLetter == '3') {
			setSoutheast();
			startTaskAction();
		} else if (keyLetter == '1') {
			setSouthwest();
			startTaskAction();
		} else if (keyLetter == 'Q' || keyLetter == 'q') {
			quitProgram();
		}

		if (keyCode == 10) {
			startTaskAction();
		}
	}

	/**
	 * @author Nazmul
	 * 
	 * Modified by:
	 * jpmolinamatute
	 * dhgiang
	 * this method perform all the action for all the buttons in the program
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String actionToPerform = new String(arg0.getActionCommand());
		Integer b = Integer.parseInt(ttfZoom.getText());
		setChange();

		if (actionToPerform.equals("North")) {
			setNorth();
		} else if (actionToPerform.equals("East")) {
			setEast();
		} else if (actionToPerform.equals("Northeast")) {
			setNortheast();
		} else if (actionToPerform.equals("Southeast")) {
			setSoutheast();
		} else if (actionToPerform.equals("Southwest")) {
			setSouthwest();
		} else if (actionToPerform.equals("South")) {
			setSouth();
		} else if (actionToPerform.equals("Northwest")) {
			setNorthwest();
		} else if (actionToPerform.equals("West")) {
			setWest();
		} else if (actionToPerform.equals("quit")) {
			quitProgram();
		} else if (actionToPerform.equals("Zoomin")) {
			b = b - 1;
			if (b >= 0) {
				ttfZoom.setText(b.toString());
				slider.setValue(b);
			}
		} else if (actionToPerform.equals("Zoomout")) {
			b = b + 1;
			if (b <= 19) {
				ttfZoom.setText(b.toString());
				slider.setValue(b);
			}
		}

		startTaskAction();
	}

	/**
	 * @author jpmolinamatute
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @author jpmolinamatute
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
