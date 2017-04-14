package ADAC;

public class IntKvp extends ADACKvp {

	private int value;
	
	public IntKvp(KvpListener listener, ADACKey key) {
		super(listener, key);
	}

	@Override
	public String getString() {
		return "" + getValue();
	}

	public int getValue() {
		return value;
	}

	@Override
	protected void read() {
		listener.read(this);
		
	}

	public void setValue(int value) {
		this.value = value;
	}

}
