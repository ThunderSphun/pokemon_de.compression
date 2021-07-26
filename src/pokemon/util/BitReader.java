package pokemon.util;

import java.io.*;

public class BitReader implements Closeable {
	private static final String EOF_EXCEPTION_MESSAGE = "end of file is reached";
	private final InputStream input;
	private byte currentByte;
	private byte pointerIndex;

	public BitReader(byte[] bytes) {
		this(new ByteArrayInputStream(bytes));
	}

	public BitReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	private BitReader(InputStream input) {
		this.input = input;
		this.currentByte = 0;
		// makes sure file is next at first call
		// no manual call here, as it is not the job of the constructor
		this.pointerIndex = -1;
	}

	public byte next() throws IOException {
		this.setByte();
		this.atEndOfFile();
		byte shiftedByte = (byte) (this.currentByte >> this.pointerIndex);
		this.pointerIndex--;

		return (byte) (shiftedByte & 1);
	}

	public byte next(int amount) throws IOException {
		this.atEndOfFile();
		if (amount > 8 || amount <= 0) {
			throw new IllegalArgumentException("you can only ask 1-8 bytes at once, but you asked " + amount);
		}
		if (amount == 1) {
			return this.next();
		}

		byte result = 0;
		for (int i = 0; i < amount; i++) {
			result <<= 1;
			result |= this.next();
		}
		return result;
	}

	public byte[] nextBatch(int amount) throws IOException {
		if (amount <= 8) {
			return new byte[]{next(amount)};
		}
		if (this.input.available() < amount) {
			this.atEndOfFile();
			return this.nextBatch(this.input.available());
		}

		byte[] bytes = new byte[(amount / 8)];
		for (int i = 0; i < bytes.length; i++) {
			if (amount > 8) {
				bytes[i] = next(8);
				amount -= 8;
			} else {
				bytes[i] = next(amount);
			}
		}
		return bytes;
	}

	private void setByte() throws IOException {
		if (this.pointerIndex < 0) {
			this.atEndOfFile();
			int readData = this.input.read();
			this.currentByte = (byte) readData;
			this.pointerIndex = 7;
		}
	}

	private void atEndOfFile() throws IOException {
		if (this.input.available() < 0) {
			throw new EOFException(EOF_EXCEPTION_MESSAGE);
		}
	}

	@Override
	public void close() throws IOException {
		this.input.close();
	}
}
