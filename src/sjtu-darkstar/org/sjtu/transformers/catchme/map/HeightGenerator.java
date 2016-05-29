package org.sjtu.transformers.catchme.map;

import java.util.Random;

import org.sjtu.transformers.catchme.core.Constants;

public class HeightGenerator {
	int sizeX;
	int sizeY;
	int[][] height;

	public HeightGenerator(int y, int x) {
		this.sizeY = y;
		this.sizeX = x;
		height = new int[sizeY][sizeX];
		generateHeight();
	}

	public int setHeight(int h) {
		Random random = new Random();
		if (h == 0) {
			switch (random.nextInt(3)) {
			case 0:
				return 1;
			default:
				return 1;
			}
		}
		if (h == Constants.MAP_HEIGHT - 1) {
			return h - 1 + random.nextInt(2);
		}
		switch (random.nextInt(5)) {
		case 0:
		case 1:
			return h - 1;
		case 3:
			return h;
		default:
			return h + 1;
		}
	}

	public void generateHeight() {
		int x, y;
		Random random = new Random();
		height[0][0] = random.nextInt(Constants.MAP_HEIGHT);
		for (y = 0; y < sizeY - 1; y++) {
			for (x = 0; x < sizeX - 1; x++) {
				height[y][x] = setHeight(height[y][x]);
				if (random.nextBoolean())
					height[y + 1][x] = height[y][x];
				else
					height[y][x + 1] = height[y][x];
			}
			x = sizeX - 1;
			height[y][x] = setHeight(height[y][x]);
			if (random.nextBoolean())
				height[y + 1][x] = height[y][x];
		}
		y = sizeY - 1;
		for (x = 0; x < sizeX - 1; x++) {
			height[y][x] = setHeight(height[y][x]);
			if (random.nextBoolean())
				height[y][x + 1] = height[y][x];
		}
		x = sizeX - 1;
		height[y][x] = setHeight(height[y][x]);

	}

	public int[][] getHeight() {
		return height;
	}

}
