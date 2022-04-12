package GUI;

import com.toedter.calendar.JDateChooser;
import selector.*;
import model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.BookingStatus.BookingStatusOptions.*;

public class Restaurant {
	private JTextField nameTextField;
	private JTextField mobileNumberTextField;
	private JTextField dateTimeTextField;
	private JPanel panel;
	private JComboBox<Waiters> waiterComboBox;
	private JComboBox MealComboBox;
	private JButton clearButton;
	private JButton submitButton;
	private JLabel nameLabel;
	private JLabel mobileNumberLabel;
	private JLabel waiterLabel;
	private JScrollPane jScrollPane;
	private JTable jTable;
	private JComboBox timeComboBox;
	private JDateChooser jDateChooser1;
	private JButton printButton;
	private JTextField search;
	private JButton searchButton;
	private JLabel image;
	private JButton connectButton;
	private JLabel connectionStatus;
	private JButton disconnectButton;
	private Selector selector = new Selector();

	private Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;

	public Restaurant() {


		//formatting date
		String pattern = "MM-dd-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		final String[] date = {null};
		final String[] time = {null};

		// create instance for Book
		Bookings from = new Bookings();

		//set default data
		from.setMealId(1);
		from.setWaiterId(1);
		from.setDateTime(simpleDateFormat.format(new Date()) + " " + selector.getTime()[0]);

		//define table
		final BookingTableModel[] RestaurantTableModel = {new BookingTableModel(selector.getBooking())};

		jTable.setModel(RestaurantTableModel[0]);
		jTable.getAutoCreateRowSorter();
		image.setIcon(new ImageIcon("src/main/java/Picture/restaurantPicture.jpg"));
		startComponents();

		//submit Button Action Listener
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				//setting up new values if these are null
				if (date[0] == null) date[0] = simpleDateFormat.format(new Date());
				if (time[0] == null) time[0] = selector.getTime()[0];

				try {
					String dateTime = date[0] + " " + time[0];
					if (!selector.getBookingDateWaiter(dateTime, from.getWaiterId())) {
						JOptionPane.showMessageDialog(null, "Not Available");
					} else {
						from.setCode(UUID.randomUUID().toString());
						from.setPhoneNumber(mobileNumberTextField.getText());
						from.setName(nameTextField.getText());
						from.setDateTime(dateTime);
						System.out.println(from.getDateTime());

						selector.addBooking(from);
						System.out.println("Successfully added booking!");

					}

					jTable.revalidate();

					RestaurantTableModel[0] = new BookingTableModel(selector.getBooking());
					jTable.setModel(RestaurantTableModel[0]);


				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		//gets values from combo box waiters
		waiterComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox jComboBox = (JComboBox) e.getSource();

				Waiters waiters = (Waiters) jComboBox.getSelectedItem();

				from.setWaiterId(waiters.getId());
			}
		});

		//gets values from combo box Meals
		MealComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox jComboBox = (JComboBox) e.getSource();

				MealServices mealServices = (MealServices) jComboBox.getSelectedItem();

				from.setMealId(mealServices.getId());
			}
		});

		//gets date value
		jDateChooser1.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if ("date".equals(e.getPropertyName())) {
					date[0] = simpleDateFormat.format(Date.from(jDateChooser1.getDate().toInstant()));
				}

			}

		});

		//gets time value
		timeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				time[0] = timeComboBox.getSelectedItem().toString();
			}
		});

		//getting information when pressing on table
		jTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable jTable = (JTable) e.getSource();

					String[] options = {"Confirm Booking", "Cancel Booking", "Exit"};

					int result = JOptionPane.showOptionDialog(null, "Choose the available option for the booking status: ", "Confirmation Booking",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
							null, options, null);
					if (result == JOptionPane.YES_OPTION) {
						selector.updateStatus(CONFIRMED.name(), jTable.getValueAt(jTable.getSelectedRow(), 0).toString());
					} else if (result == JOptionPane.NO_OPTION) {
						selector.updateStatus(CANCELLED.name(), jTable.getValueAt(jTable.getSelectedRow(), 0).toString());
					}

				}

				RestaurantTableModel[0] = new BookingTableModel(selector.getBooking());
				jTable.setModel(RestaurantTableModel[0]);

			}
		});

		//printing information form table
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					jTable.print(JTable.PrintMode.FIT_WIDTH, null, null);
				} catch (PrinterException ex) {
					ex.printStackTrace();
				}
			}
		});

		//getting information from search and comparing with the names on the table
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RestaurantTableModel[0] = new BookingTableModel(selector.searchDataFromDatabase(search.getText()));
				jTable.setModel(RestaurantTableModel[0]);
			}
		});

		//clearing previous inserted values
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nameTextField.setText("");
				mobileNumberTextField.setText("");
			}
		});

		//connecting to server button
		connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reconnectToServer();
			}
		});
		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeConnection();
			}
		});
	}
	//closing connetion to server
	private void closeConnection() {
		if (socket != null){
			connectionStatus.setText("Connection Lost!");
			try{
				socket.close();
			}catch (IOException ex){
				Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				socket = null;
			}
		}
	}
	//reconnecting to server
	private void reconnectToServer() {
		closeConnection();
		connectionStatus.setText("Attempting to connect to server");
		try{
			socket = new Socket("127.0.0.1", 2000);
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			connectionStatus.setText("Connected to server!");
		}
		catch (IOException ex) {
			Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
			connectionStatus.setText("Connection Failed"); // connection failed
		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Restaurant App!");

		frame.setContentPane(new Restaurant().panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(1100,800);
	}

	private void startComponents() {
		// getting waiters from selector
		DefaultComboBoxModel<Waiters> waiterDefaultComboBoxModel = new DefaultComboBoxModel<>(selector.getWaiter().toArray(new Waiters[0]));
		waiterComboBox.setModel(waiterDefaultComboBoxModel);

		// getting Meal Services from selector
		DefaultComboBoxModel<MealServices> mealServiceDefaultComboBoxModel = new DefaultComboBoxModel<>(selector.getMealServices().toArray(new MealServices[0]));
		MealComboBox.setModel(mealServiceDefaultComboBoxModel);

		// getting time from selector
		DefaultComboBoxModel<String> timeDefaultComboBoxModel = new DefaultComboBoxModel<>(selector.getTime());
		timeComboBox.setModel(timeDefaultComboBoxModel);
	}

	//Setting up Table to Display
	private static class BookingTableModel extends AbstractTableModel {
		private final String[] columnNames = {"Code", "Name", "Date Time", "Phone Number", "Status", "Waiter Name", "Meal"};

		private List<Bookings.VBookings> bookList;

		public BookingTableModel(List<Bookings.VBookings> bookList) {
			this.bookList = bookList;
		}

		@Override
		public int getRowCount() {
			return bookList.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return super.getColumnClass(columnIndex);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String s;

			switch (columnIndex) {
				case 0:
					s = bookList.get(rowIndex).getCode();
					break;
				case 1:
					s = bookList.get(rowIndex).getName();
					break;
				case 2:
					s = bookList.get(rowIndex).getDateTime();
					break;
				case 3:
					s = bookList.get(rowIndex).getPhoneNumber();
					break;
				case 4:
					s = bookList.get(rowIndex).getStatus();
					break;
				case 5:
					s = bookList.get(rowIndex).getWaiterName();
					break;
				case 6:
					s = bookList.get(rowIndex).getMealName();
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + columnIndex);
			}

			return s;
		}


	}

	//Defining JDateChooser
	public void createUIComponents() {
		jDateChooser1 = new JDateChooser(Date.from(Instant.now()));
		jDateChooser1.setDateFormatString("dd MMMM yyyy");

	}
}
