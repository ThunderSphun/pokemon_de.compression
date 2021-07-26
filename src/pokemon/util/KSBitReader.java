package pokemon.util;

import java.io.*;

public class KSBitReader {

    private static final byte[] MASKS = {
            (byte) 0b00000001, // 0
            (byte) 0b00000010, // 1
            (byte) 0b00000100, // 2
            (byte) 0b00001000, // 3
            (byte) 0b00010000, // 4
            (byte) 0b00100000, // 5
            (byte) 0b01000000, // 6
            (byte) 0b10000000  // 7
    };

    public final byte[] bytes;
    public int currentByte;
    public int bitIndex;

    public KSBitReader(byte[] bytes) throws IOException {
        this(new ByteArrayInputStream(bytes));
    }

    public KSBitReader(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public KSBitReader(InputStream in) throws IOException {
        try (in) {
            // Just load our source byte array into RAM. Easier.
            this.bytes = in.readAllBytes();
        }

        this.currentByte = -1;
        this.bitIndex = -1;
    }

    public byte next() throws IOException {
        if (bitIndex < 0) {
            // Move to next byte and reset bit index.
            currentByte++;
            bitIndex = 7;

            if (currentByte >= bytes.length) {
                throw new EOFException("End of input stream has been reached.");
            }
        }

        return (bytes[currentByte] & MASKS[bitIndex--]) != 0 ? (byte) 1 : (byte) 0;
    }

    public byte next(int amount) throws IOException {
        if (amount <= 0 || amount > 8) {
            throw new IllegalArgumentException(String.format(
                    "Invalid bit request (%d): Valid range is 1-8", amount));
        }

        byte output = 0;

        // Output is right-aligned. E.g. if sequence of bits is 10011010, and we
        // requested 4 bits, then the output is 00001001 . Caller is responsible
        // for reading these bits starting at the correct index.

        for (int i = amount - 1; i >= 0; i--) {
            output |= (next() << i);
        }

        return output;
    }
}
