package ADAC;

public interface KvpListener {

	public void read(ByteKvp byteKvp);
	public void read(FloatKvp floatKvp);
	public void read(IntKvp intKvp);
	public void read(ShortKvp shortKvp);
	
}
