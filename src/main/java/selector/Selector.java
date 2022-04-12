package selector;

import SQLConnection.ConnectionClass;
import model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static model.BookingStatus.BookingStatusOptions.CANCELLED;
import static model.BookingStatus.BookingStatusOptions.BOOKED;


public class Selector {

	private static Connection connection;

	private static Statement statement;

	private static ResultSet resultSet;


	//Getting all the available bookings
	public List<Bookings.VBookings> getBooking() {
		List<Bookings.VBookings> bookingList = new ArrayList<>();

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			resultSet = statement.executeQuery
					("SELECT  b.* , c.name as waiterName , d.name as mealName FROM Book b " +
							"join Waiter c on c.id = b.waiterId " +
							"join Meal d on b.MealService = d.id");

			while (resultSet.next()) {
				Bookings.VBookings data = new Bookings.VBookings(
						resultSet.getInt("id"),
						resultSet.getString("code"),
						resultSet.getString("name"),
						resultSet.getString("date"),
						resultSet.getString("phone"),
						resultSet.getString("status"),
						resultSet.getString("waiterName"),
						resultSet.getString("mealName"));

				bookingList.add(data);

			}

			statement.close();
			connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return bookingList;
	}

	//Getting all the available Waiters
	public List<Waiters> getWaiter() {
		List<Waiters> waitersList = new ArrayList<>();

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT  * FROM Waiter");

			while (resultSet.next()) {
				Waiters data = new Waiters(
						resultSet.getInt("id"),
						resultSet.getString("code"),
						resultSet.getString("name"));

				waitersList.add(data);
			}

			statement.close();
			connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return waitersList;
	}

	//getting all the Meal Services
	public List<MealServices> getMealServices() {
		List<MealServices> mealServicesList = new ArrayList<>();

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT * FROM Meal");

			while (resultSet.next()) {
				MealServices data = new MealServices(
						resultSet.getInt("id"),
						resultSet.getString("code"),
						resultSet.getString("name"),
						resultSet.getBigDecimal("price"));

				mealServicesList.add(data);
			}

			statement.close();
			connection.close();

		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return mealServicesList;
	}

 	//updating status to CONFIRMED OR CANCELLED
	public void updateStatus(String statusBooking, String code) {
		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			statement.execute("UPDATE Book SET status = '" + statusBooking + "' WHERE code = '" + code + "'");

			statement.close();
			connection.close();
			System.out.println("Updated Successfully!");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	//parameter to INSERT Booking in DataBase
	public Bookings addBooking(Bookings bookings) throws SQLException {

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();

			String insert = "INSERT INTO Book values(null ,'" + bookings.getCode() + "','" + bookings.getDateTime() + "' , '" + bookings.getPhoneNumber() + "' , '" + bookings.getName() + "' , '" + BOOKED.name() + "','" + bookings.getWaiterId() + "','" + bookings.getMealId() + "')";

			statement.execute(insert);

			statement.close();
			connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return bookings;
	}

	//Validating date and waiter to check if there ar similarities
	public boolean getBookingDateWaiter(String date, int waiter) {

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT count(*) as recordCount FROM Book WHERE date = '" + date + "' AND  waiterid = '" + waiter + "' AND status != '" + CANCELLED.name() + "'");

			while (resultSet.next()) {
				if (resultSet.getInt("recordCount") >= 1) {
					statement.close();
					connection.close();
					return false;

				}
			}

			statement.close();
			connection.close();

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}

		return true;
	}
	//time Function to return the available time Slots
	public String[] getTime() {
		return new String[]{
				"9:00 AM",
				"10:00 AM",
				"11:00 AM",
				"12:00 AM",
				"1:00 PM",
				"2:00 PM",
				"3:00 PM",
				"4:00 PM",
				"5:00 PM",
				"6:00 PM",
				"7:00 PM",
				"8:00 PM",
				"9:00 PM",
				"10:00 PM",
				"11:00 PM",
				"00:00 AM",
				"1:00 AM",};
	}
	//Serching for Customer Name
	public List<Bookings.VBookings> searchDataFromDatabase(String name) {
		List<Bookings.VBookings> bookingList = new ArrayList<>();

		try {
			connection = ConnectionClass.accessWineDatabase();
			statement = connection.createStatement();
			resultSet = statement.executeQuery
					("SELECT  b.* , c.name as waiterName , d.name as mealServicesName FROM Book b " +
							"join Waiter c on c.id = b.waiterid " +
							"join Meal d on b.MealService = d.id WHERE  b.name LIKE '%" + name + "%'");

			while (resultSet.next()) {
				Bookings.VBookings data = new Bookings.VBookings(
						resultSet.getInt("id"),
						resultSet.getString("code"),
						resultSet.getString("name"),
						resultSet.getString("date"),
						resultSet.getString("phone"),
						resultSet.getString("status"),
						resultSet.getString("waiterid"),
						resultSet.getString("MealService"));

				bookingList.add(data);

			}

			statement.close();
			connection.close();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		return bookingList;
	}


}
