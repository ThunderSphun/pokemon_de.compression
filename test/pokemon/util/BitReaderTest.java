package pokemon.util;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BitReaderTest {

    private BitReader reader;

    @Test
    public void whenConstruct_thenInitialized() {
        byte[] testData = { 0b00000001 };

        reader = new BitReader(testData);

        assertNotNull(reader.input);
        assertEquals(0, reader.currentByte);
        assertEquals(-1, reader.pointerIndex);
    }

    @Test
    public void givenTestData_whenNextBit_thenRetrievesBitsInOrder() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new BitReader(testData);

        assertEquals(0b01, reader.next());

        // Verify we're now on the first byte and correct position index.
        assertEquals((byte) 0b10010011, reader.currentByte);
        assertEquals(6, reader.pointerIndex);

        // Read remaining bits.
        assertEquals(0b00, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b01, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b01, reader.next());
        assertEquals(0b01, reader.next());

        // Should still be on the first byte, but past the end of it.
        assertEquals((byte) 0b10010011, reader.currentByte);
        assertEquals(-1, reader.pointerIndex);

        // Next byte
        assertEquals(0b00, reader.next());

        // Now should be on second byte
        assertEquals((byte) 0b01010101, reader.currentByte);
        assertEquals(6, reader.pointerIndex);

        assertEquals(0b01, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b01, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b01, reader.next());
        assertEquals(0b00, reader.next());
        assertEquals(0b01, reader.next());
    }

    @Test
    public void givenTestData_whenNextBitGroup_thenRetrievesBitsInOrder() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new BitReader(testData);

        assertEquals(0b0010, reader.next(2));
        assertEquals(0b0100, reader.next(4));
        assertEquals(0b0011, reader.next(2));

        // Next byte
        assertEquals(0b0010, reader.next(3));
        assertEquals(0b0001, reader.next(1));
        assertEquals(0b0101, reader.next(4));
    }

    @Test
    public void givenTestData_whenNextBitsSpansByteBoundary_thenRetrievesCorrectBits() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new BitReader(testData);

        assertEquals(0b100100, reader.next(6));
        assertEquals(0b1101, reader.next(4)); // last 2 bits of b1 plus first 2 bits of next byte
        assertEquals(0b010101, reader.next(6));
    }

    @Test(expected = EOFException.class)
    public void givenTestData_whenReadPastEOF_thenEOFException() throws IOException {
        byte[] testData = {
                (byte) 0b10010011,
                (byte) 0b01010101 };

        reader = new BitReader(testData);

        assertEquals((byte) 0b10010011, reader.next(8));
        assertEquals((byte) 0b01010101, reader.next(8));

        reader.next(); // EOFException
    }
}
