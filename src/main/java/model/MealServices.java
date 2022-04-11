package model;

import java.math.BigDecimal;

public class MealServices extends Command {
	private BigDecimal price;

	public MealServices() {
	}

	public MealServices(int id, String code, String name, BigDecimal price) {
		super(id, code, name);
		this.price = price;
	}

	@Override
	public String toString() {
		return this.getName() + " £" + this.price;
	}
}
