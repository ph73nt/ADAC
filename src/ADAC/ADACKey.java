package ADAC;

/**
 * An Object representing a key within the header block of an ADAC object. Each
 * key contains six bytes: the key number, the key data type, an empty byte and
 * the offset to the key's value. The key's data type is ignored in preference
 * to a lookup on the {@link ADACDictionary}.
 * 
 * The object is similar to a key-value pair, only it's a key-offset pair.
 * 
 * @author neil
 *
 */
public class ADACKey {

	private final short keyNum;
	private final short fieldOffset;

	/**
	 * Create an ADACKey object, which is just a key-offset pair for looking up
	 * the value.
	 * 
	 * @param keyNum
	 *            The number of the key. The description and the data type of
	 *            the key can be found in the {@link ADACDictionary}
	 * @param fieldOffset
	 *            The offset in the file to the value of the key.
	 */
	public ADACKey(short keyNum, short fieldOffset) {

		this.keyNum = keyNum;
		this.fieldOffset = fieldOffset;

	}

	/**
	 * Get the data type of the key contained in this object. This is just a
	 * lookup in the {@link ADACDictionary}
	 * 
	 * @return An integer enumeration of the datatype referred to in the
	 *         {@link ADACDictionary}
	 */
	public int getDataType() {
		return ADACDictionary.type[keyNum];
	}

	/**
	 * Get the offset in bytes in the file to the value of the key.
	 * 
	 * @return The offset in bytes.
	 */
	public short getOffset() {
		return fieldOffset;
	}

	/**
	 * Get the key number.
	 * 
	 * @return The number of the key. The description and the data type of the
	 *         key can be found in the {@link ADACDictionary}
	 * 
	 */
	public short getKeyNum() {
		return keyNum;
	}
}
