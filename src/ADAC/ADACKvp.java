package ADAC;

public abstract class ADACKvp {

	private final ADACKey adacKey;

	protected final KvpListener listener;

	public ADACKvp(KvpListener listener, ADACKey key) {

		this.listener = listener;
		adacKey = key;
		read();

	}

	public short getFieldOffset(){
		return getKey().getOffset();
	}
	
	public ADACKey getKey() {
		return adacKey;
	}
	
	public Short getKeyNum() {
		return getKey().getKeyNum();
	}

	/**
	 * get a formatted string ready for putting in the log window when required
	 * 
	 * @return
	 */
	public String getLogString() {
		return getKey().getKeyNum() + ", " + getKey().getDataType() + ", " + getKey().getOffset() + ", "
				+ getString();
	}

	public abstract String getString();

	protected abstract void read();

}
