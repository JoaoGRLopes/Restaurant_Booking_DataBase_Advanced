package model;

public class Bookings extends Command {

	private String dateTime;
	private String phoneNumber;
	private String status;
	private int waiterId;
	private int mealId;

	public Bookings() {
	}

	public Bookings(int id, String code, String name, String dateTime, String phoneNumber, String status, int waiterId, int mealId) {
		this.setId(id);
		this.setCode(code);
		this.setName(name);
		this.dateTime = dateTime;
		this.phoneNumber = phoneNumber;
		this.status = status;
		this.waiterId = waiterId ;
		this.mealId = mealId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getWaiterId() {return waiterId;}

	public void setWaiterId(int waiterId) {this.waiterId = waiterId;}

	public long getMealId() {
		return mealId;
	}

	public void setMealId(int mealId) {
		this.mealId = mealId;
	}


	public static class VBookings extends Bookings {
		private String waiterName;

		public String getWaiterName() {return waiterName;}

		private String mealName;

		public String getMealName() {return mealName;}


		public VBookings(int id, String code, String name, String dateTime, String phoneNumber, String status, String waiterName, String serviceName) {
			this.setId(id);
			this.setCode(code);
			this.setName(name);
			this.setDateTime(dateTime);
			this.setPhoneNumber(phoneNumber);
			this.setStatus(status);
			this.waiterName = waiterName;
			this.mealName = serviceName;
		}
	}
}
