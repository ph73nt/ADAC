package ADAC;

public class ByteKvp extends ADACKvp {
	
	private String value;
	
	public ByteKvp(KvpListener listener, ADACKey key){
	
		super(listener, key);
		
	}

	public String getString(){
		return value;
	}
	
	@Override
	protected void read() {
		listener.read(this);		
	}
	
	public void setString(byte[] bytes){
		value = new String(bytes);
	}
	
	public void setString(String value){
		this.value = value;
	}
	
}
