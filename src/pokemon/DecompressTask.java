package pokemon;

import javafx.scene.paint.Color;
import pokemon.gui.Renderer;
import pokemon.gui.WritableImageWrapper;
import pokemon.sprite.Sprite;
import pokemon.util.BitReader;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecompressTask implements Runnable {
	private final File file;
	private final boolean renderable;
	private BitReader reader;
	private byte widthInTiles;
	private byte heightInTiles;

	public DecompressTask(List<File> files, boolean renderable) {
		this.file = files.get(0);
		this.renderable = renderable;
	}

	public DecompressTask(File file) {
		this.file = file;
		this.renderable = false;
	}

	@Override
	public void run() {
		try {
			this.reader = new BitReader(this.file);
			System.out.println(this.file.getName());

			int sizeOfSprite = this.getSpriteSize();
			byte primaryBuffer = this.reader.next();

			Sprite buffer0SpriteData = new Sprite(this.widthInTiles, this.heightInTiles);
			Sprite buffer1SpriteData = new Sprite(this.widthInTiles, this.heightInTiles);

			this.readToBuffer(primaryBuffer == 0 ? buffer0SpriteData : buffer1SpriteData);
			byte encodeMethod = getEncodeMethod();
			this.readToBuffer(primaryBuffer == 0 ? buffer1SpriteData : buffer0SpriteData);

			if (this.renderable) {
				WritableImageWrapper bufferA = Renderer.Instance().getBuffer(0);
				WritableImageWrapper bufferB = Renderer.Instance().getBuffer(1);

				this.render(buffer0SpriteData, bufferA);
				this.render(buffer1SpriteData, bufferB);
			}

			System.out.println("mode " + encodeMethod);

			switch (encodeMethod) {
				case 1: {
					this.deltaEncode(buffer0SpriteData);
					this.deltaEncode(buffer1SpriteData);
					break;
				}
				case 2: {
					buffer1SpriteData = this.xorData(buffer0SpriteData, buffer1SpriteData);
					this.deltaEncode(buffer0SpriteData);
					break;
				}
				case 3: {
					buffer1SpriteData = this.xorData(buffer0SpriteData, buffer1SpriteData);
					this.deltaEncode(buffer0SpriteData);
					this.deltaEncode(buffer1SpriteData);
					break;
				}
			}

			if (this.renderable) {
				WritableImageWrapper bufferA = Renderer.Instance().getBuffer(0);
				WritableImageWrapper bufferB = Renderer.Instance().getBuffer(1);

				this.render(buffer0SpriteData, bufferA);
				this.render(buffer1SpriteData, bufferB);
			}


			Sprite mainSprite = new Sprite(this.widthInTiles, this.heightInTiles);

			int verticalOffset = Sprite.WIDTH - this.heightInTiles;
			int horizontalOffset = Math.round((Sprite.WIDTH - this.widthInTiles) / 2f);



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void render(Sprite data, WritableImageWrapper buffer) {
		buffer.announceBatchStart();

		data.forEachAbsolute((x, y, b) -> {
			if (b == 0) {
				buffer.set(x, y, Color.WHITE);
			} else {
				buffer.set(x, y, Color.BLACK);
			}
		});

		buffer.announceBatchEnd();
	}

	private int getSpriteSize() throws IOException {
		this.widthInTiles = this.reader.next(4);
		this.heightInTiles = this.reader.next(4);

		int spriteWidth = this.widthInTiles * 8;
		int spriteHeight = this.heightInTiles * 8;

		if (this.renderable) {
			Renderer.Instance().getImage().setSize(spriteWidth, spriteHeight);
			Renderer.Instance().getBuffer(0).setSize(spriteWidth, spriteHeight);
			Renderer.Instance().getBuffer(1).setSize(spriteWidth, spriteHeight);
		}

		return spriteWidth * spriteHeight;
	}

	private void readToBuffer(Sprite sprite) throws IOException {
		byte initialPacket = this.reader.next();
		boolean doingRLE = initialPacket == 0;

		int bitsInSprite = sprite.getAbsoluteWidth() * sprite.getAbsoluteHeight();

		int bitsRead = 0;
		int x = 0;
		int y = 0;
		System.out.println(sprite.getAbsoluteHeight());

		while (bitsRead < bitsInSprite) {
			byte[] bitPairs;

			try {
				bitPairs = doingRLE ? RLE() : DATA(bitsRead, bitsInSprite);
			} catch (EOFException e) {
				System.out.println(bitsRead);
				break;
			}

			for (byte bitPair : bitPairs) {
				sprite.setAbsolute(x + 1, y, (byte) (bitPair & 1));
				sprite.setAbsolute(x, y, (byte) ((bitPair >> 1) & 1));
				bitsRead += 2;
				y++;
				if (y >= sprite.getAbsoluteHeight()) {
					y = 0;
					x += 2;
				}
			}

			doingRLE = !doingRLE;
		}
		System.out.println();
//		System.out.println(bitsRead);
//		System.out.println(bitsInSprite);
//		System.out.println(sprite);
	}

	private byte getEncodeMethod() throws IOException {
		byte encode = this.reader.next();
		if (encode == 1) {
			encode <<= 1;
			encode |= this.reader.next();
			return encode;
		}
		return 1;
	}

	/**
	 * a RLE Packet stores the amount of binary 00 pairs (value N)
	 * it does so by encoding this value in two sub values, L & V
	 * <p>
	 * V will be the value you get when removing the leading bit of N
	 * <p>
	 * L will be the value you need to add to V to get N
	 * L will be stored first, so it needs to let the code know when L is done and V starts,
	 * this is done by using a 0-bit as terminator
	 * <p>
	 * this gives a problem as L is a power of 2
	 * to solve this L has 2 removed to get all 1s and a terminator 0
	 * This will fail on N == 1, so before L and V gets calculated, N gets incremented by 1
	 *
	 * @return byte[] with RLE packet
	 * @throws IOException passed on from {@link BitReader#next()}
	 */
	private byte[] RLE() throws IOException {
		System.out.print("RLE  ");

		int valueL = 0; // initialize value L to 0
		int bits = 0; // counter for amount of bits

		byte next;
		do {
			next = this.reader.next();

			valueL = (valueL << 1) | next; // modify value L

			bits++;
		} while (next != 0);

		int valueV = 0;
		for (int i = 0; i < bits; i++) {
			valueV <<= 1;
			valueV |= this.reader.next();
		}

		// set value N
		int valueN = valueL + valueV + 1;

		byte[] bytes = new byte[valueN]; // make byte[]
		Arrays.fill(bytes, (byte) 0); // fill array with 0b00

		return bytes;
	}

	/**
	 * a Data Packet stores the actual data of de decompressed file
	 * this is a list of numbers that can be directly copied over into the buffer
	 * this list will get terminated by a binary 00 pair, which does not count as part of the data
	 * if the buffer gets filled, there is no terminator pair necessary
	 *
	 * @param currentIndex the current index
	 *                     used to determine if the DATA packet needs to be terminated
	 * @param spriteSize   the total sprite size
	 *                     used to determine if the DATA packet needs to be terminated
	 * @return byte[] with DATA packet
	 * @throws IOException passed on from {@link BitReader#next(int)}
	 */
	private byte[] DATA(int currentIndex, final int spriteSize) throws IOException {
		System.out.print("DATA  ");

		List<Byte> bitPairs = new ArrayList<>(); // need a variable size, as length is unknown
		while (currentIndex + (bitPairs.size() * 2) < spriteSize) {
			byte next;
			try {
				next = this.reader.next(2); // DATA packet parts are pairs
			} catch (EOFException e) {
				break;
			}
			if (next == 0) {
				break;
			}
			bitPairs.add(next); // add if not 0b00
		}

		byte[] bytes = new byte[bitPairs.size()]; // put data into array
		for (int i = 0; i < bytes.length; i++) { // Byte[] cannot be cast to byte[]
			bytes[i] = bitPairs.get(i);
		}
		return bytes;
	}

	/**
	 * deltaEncoding is storing the difference instead of the actual value
	 * bit -1 is assumed to be a 0 for each line
	 * in this case it is done to increase the amount of 0s in the file, and reduce the amount of following 1s
	 *
	 * @param sprite the encoded sprite to decode
	 */
	private void deltaEncode(Sprite sprite) {
		int height = sprite.getHeight();
		int width = sprite.getWidth();

		for (int i = 0; i < height; i++) {
			byte valueToWrite = 0;
			for (int j = 0; j < width; j++) {
				byte currentBit = sprite.getAbsolute(i, j);
				if (currentBit == 1) {
					valueToWrite = (byte) (valueToWrite == 0 ? 1 : 0);
				}
				sprite.setAbsolute(i, j, valueToWrite);
			}
		}
	}

	/**
	 * XORs two sprites together
	 * <p>
	 * XOR is a binary operation with truth table:
	 * <p>
	 * A | B | C
	 * --+---+--
	 * 0 | 0 | 0
	 * 0 | 1 | 1
	 * 1 | 0 | 1
	 * 1 | 1 | 0
	 * <p>
	 * here A and B are inputs, C is the output
	 *
	 * @param spriteA sprite with values A
	 * @param spriteB sprite with values B
	 * @return sprite with values A ^ B
	 */
	private Sprite xorData(Sprite spriteA, Sprite spriteB) {
		Sprite xorSprite = new Sprite(spriteA.getWidth(), spriteA.getHeight());

		spriteA.forEachAbsolute((x, y, a) -> {
			byte b = spriteB.getAbsolute(x, y);
			xorSprite.setAbsolute(x, y, (byte) (a ^ b));
		});

		return xorSprite;
	}
}
