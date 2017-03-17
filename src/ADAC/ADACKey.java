package ADAC;

public class ADACKey {
	
	private final short keyNum;
	private final byte dataType;
	private final short fieldOffset;
	
	public ADACKey(short keyNum, byte dataType, short fieldOffset){
		
		this.keyNum = keyNum;
		this.dataType = dataType;
		this.fieldOffset = fieldOffset;
		
	}
	
	public byte getDataType() {
		return dataType;
	}

	public short getFieldOffset() {
		return fieldOffset;
	}
	
	public short getKeyNum() {
		return keyNum;
	}
}
