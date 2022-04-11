package model;

public class Waiters extends Command {
	public Waiters(int id, String code, String name) {
		this.setId(id);
		this.setCode(code);
		this.setName(name);
	}

	@Override
	public String toString() {
		return getName();
	}
}
