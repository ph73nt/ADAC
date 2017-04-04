package ADAC;

public class FloatKvp extends ADACKvp {

	private float value;

	public FloatKvp(KvpListener listener, ADACKey key) {
		super(listener, key);
	}

	@Override
	public String getString() {
		return "" + getValue();
	}

	public float getValue() {
		return value;
	}

	@Override
	protected void read() {
		listener.read(this);

	}

	public void setValue(float value) {
		this.value = value;
	}

}
