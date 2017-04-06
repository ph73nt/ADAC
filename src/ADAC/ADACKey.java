package ADAC;

public class ADACKey {

	private final short keyNum;
	private final short fieldOffset;

	public ADACKey(short keyNum, short fieldOffset) {

		this.keyNum = keyNum;
		this.fieldOffset = fieldOffset;

	}

	public int getDataType() {
		return ADACDictionary.type[keyNum];
	}

	public short getFieldOffset() {
		return fieldOffset;
	}

	public short getKeyNum() {
		return keyNum;
	}
}
