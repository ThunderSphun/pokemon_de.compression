package pokemon.util;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KSBitReaderTest {

    private KSBitReader reader;

    @Test
    public void whenConstruct_thenInitialized() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new KSBitReader(testData);

        assertEquals(2, reader.bytes.length);
        assertEquals(-1, reader.currentByte);
        assertEquals(-1, reader.bitIndex);
    }

    @Test
    public void givenTestData_whenNextBit_thenRetrievesBitsInOrder() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new KSBitReader(testData);

        assertEquals(1, reader.next());

        // Verify we're now on the first byte and correct position index.
        assertEquals(0, reader.currentByte);
        assertEquals(6, reader.bitIndex);

        // Read remaining bits.
        assertEquals(0, reader.next());
        assertEquals(0, reader.next());
        assertEquals(1, reader.next());
        assertEquals(0, reader.next());
        assertEquals(0, reader.next());
        assertEquals(1, reader.next());
        assertEquals(1, reader.next());

        // Should still be on the first byte, but past the end of it.
        assertEquals(0, reader.currentByte);
        assertEquals(-1, reader.bitIndex);

        // Next byte
        assertEquals(0b0, reader.next());

        // Now should be on second byte
        assertEquals(1, reader.currentByte);
        assertEquals(6, reader.bitIndex);

        assertEquals(1, reader.next());
        assertEquals(0, reader.next());
        assertEquals(1, reader.next());
        assertEquals(0, reader.next());
        assertEquals(1, reader.next());
        assertEquals(0, reader.next());
        assertEquals(1, reader.next());
    }

    @Test
    public void givenTestData_whenNextBits_thenRetrievesBitsInOrder() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new KSBitReader(testData);

        assertEquals((byte) 0b00000010, reader.next(2));
        assertEquals((byte) 0b00000100, reader.next(4));
        assertEquals((byte) 0b00000011, reader.next(2));

        // Next byte
        assertEquals((byte) 0b00000010, reader.next(3));
        assertEquals((byte) 0b00000001, reader.next(1));
        assertEquals((byte) 0b00000101, reader.next(4));
    }

    @Test
    public void givenTestData_whenNextBitsSpansByteBoundary_thenRetrievesCorrectBits() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new KSBitReader(testData);

        assertEquals((byte) 0b00100100, reader.next(6));
        assertEquals((byte) 0b00001101, reader.next(4)); // last 2 bits of b1 plus first 2 bits of next byte
        assertEquals((byte) 0b00010101, reader.next(6));
    }

    @Test(expected = EOFException.class)
    public void givenTestData_whenReadPastEOF_thenEOFException() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new KSBitReader(testData);

        assertEquals((byte) 0b10010011, reader.next(8));
        assertEquals((byte) 0b01010101, reader.next(8));

        reader.next(); // EOFException
    }
}
