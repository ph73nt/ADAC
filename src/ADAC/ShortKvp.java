package ADAC;

public class ShortKvp extends ADACKvp {

	private short value;
	
	public ShortKvp(KvpListener listener, ADACKey key) {
		super(listener, key);
	}

	@Override
	public String getString() {
		return "" + getValue();
	}
	
	public short getValue(){
		return value;
	}
	
	@Override
	protected void read() {
		listener.read(this);
	}

	public void setValue(short value){
		this.value = value;
	}

}
