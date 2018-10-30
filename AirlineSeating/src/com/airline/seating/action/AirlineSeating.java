package com.airline.seating.action;

import java.util.Scanner;

public class AirlineSeating {

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		Integer totalPassengers = Integer.parseInt(sc.nextLine());

		Integer[] blockRows = new Integer[1000];
		Integer[] blockCols = new Integer[1000];
		String input;
		int index = 0, maxRow = 0, totalCol = 0;
		System.out.println("Enter blocks dimensions. Type END to stop");

		while (!(input = sc.nextLine()).equals("END")) {
			String[] array = input.split(" ");
			int colValue = Integer.parseInt(array[0].trim());
			blockCols[index] = colValue;
			int rowVal = Integer.parseInt(array[1].trim());
			blockRows[index] = rowVal;

			maxRow = Math.max(maxRow, rowVal);
			totalCol += colValue;
			index++;
		}
		sc.close();

		String[][] seatLayout = new String[maxRow][totalCol];
		new AirlineSeating().arrangeSeat(seatLayout, blockRows, blockCols, totalPassengers);
	}

	void arrangeSeat(String[][] seatLayout, Integer[] blockRows, Integer[] blockCols, int totalPassengers) {
		Integer passengersFilled = 0;

		passengersFilled = fillSeats(seatLayout, blockRows, blockCols, passengersFilled, totalPassengers,
				SEAT_TYPE.AISLE);
		if (passengersFilled >= totalPassengers) {
			return;
		}
		passengersFilled = fillSeats(seatLayout, blockRows, blockCols, passengersFilled, totalPassengers,
				SEAT_TYPE.WINDOW);
		if (passengersFilled >= totalPassengers) {
			return;
		}

		passengersFilled = fillSeats(seatLayout, blockRows, blockCols, passengersFilled, totalPassengers,
				SEAT_TYPE.MIDDLE);
		// for (String[] rowSeat : seatLayout) {
		// for (String val : rowSeat) {
		// System.out.print(val + " ");
		// }
		// System.out.println("");
		// }

		passengersFilled = fillSeats(seatLayout, blockRows, blockCols, passengersFilled, totalPassengers, null);
		for (String[] rowSeat : seatLayout) {
			for (String val : rowSeat) {
				System.out.print(val + " ");
			}
			System.out.println("");
		}

	}

	private Integer fillSeats(String[][] seatLayout, Integer[] blocksRow, Integer[] blocksCol, Integer passengersFilled,
			int totalPassengers, SEAT_TYPE seatType) {
		int blockIndex = 0, prevBlocksSum = 0;

		for (int rowIndex = 0; rowIndex < seatLayout.length; rowIndex++) {
			blockIndex = 0;
			prevBlocksSum = 0;
			for (int colIndex = 0; colIndex < seatLayout[rowIndex].length; colIndex++) {
				if (prevBlocksSum + blocksCol[blockIndex] <= colIndex) {
					prevBlocksSum += blocksCol[blockIndex];
					blockIndex++;
				}
				if (seatLayout[rowIndex][colIndex] == null) {
					if (seatType == null) {
						String seatIndexText = getSeatIndex(colIndex, prevBlocksSum, blocksCol[blockIndex],
								seatLayout[rowIndex].length);
						String value = getResultText(blocksCol, seatIndexText, blockIndex, prevBlocksSum, colIndex);
						seatLayout[rowIndex][colIndex] = value;
					} else {
						if (rowIndex < blocksRow[blockIndex]) {
							boolean isType = isSeatType(colIndex, prevBlocksSum, blocksCol[blockIndex],
									seatLayout[rowIndex].length, seatType);
							if (isType) {
								String value = getResultText(blocksCol, String.format("%03d", passengersFilled + 1),
										blockIndex, prevBlocksSum, colIndex);
								seatLayout[rowIndex][colIndex] = value;
								passengersFilled++;
								if (passengersFilled >= totalPassengers) {
									return passengersFilled;
								}
							}
						}
					}
				}
			}
		}
		return passengersFilled;
	}

	private String getResultText(Integer[] blocksCol, String text, int blockIndex, int prevBlocksSum, int colIndex) {
		int relativeColIndex = colIndex - prevBlocksSum;
		String metaText = "";
		if (relativeColIndex == blocksCol[blockIndex] - 1 && blockIndex < blocksCol.length - 1) {
			metaText = "|  ";
		}
		String value = String.valueOf(text + "  " + metaText);
		return value;
	}

	private boolean isSeatType(int colIndex, int prevBlockCols, int blockColSize, int colSize, SEAT_TYPE seatType) {
		int relativeColIndex = colIndex - prevBlockCols;
		if (SEAT_TYPE.AISLE.equals(seatType)) {
			return isAileSeat(colIndex, prevBlockCols, blockColSize, colSize, relativeColIndex);
		} else if (SEAT_TYPE.WINDOW.equals(seatType)) {
			return isWindowSeat(colIndex, colSize);
		} else if (SEAT_TYPE.MIDDLE.equals(seatType)) {
			return isMiddleSeat(blockColSize, relativeColIndex);
		}
		return false;
	}

	private String getSeatIndex(int colIndex, int prevBlockCols, int blockColSize, int colSize) {
		int relativeColIndex = colIndex - prevBlockCols;

		boolean result = isAileSeat(colIndex, prevBlockCols, blockColSize, colSize, relativeColIndex);
		if (result) {
			return "-A-";
		} else {
			result = isWindowSeat(colIndex, colSize);
			if (result) {
				return "-W-";
			} else {
				result = isMiddleSeat(blockColSize, relativeColIndex);
				if (result) {
					return "-M-";
				}
			}
		}
		return "";
	}

	private boolean isMiddleSeat(int blockColSize, int relativeColIndex) {
		return relativeColIndex > 0 && relativeColIndex < blockColSize;
	}

	private boolean isWindowSeat(int colIndex, int colSize) {
		return colIndex == 0 || colIndex == colSize - 1;
	}

	private boolean isAileSeat(int colIndex, int prevBlockCols, int blockColSize, int colSize, int relativeColIndex) {
		if (blockColSize <= 2) {
			if (relativeColIndex <= 2) {
				return true;
			}
		} else {
			if (relativeColIndex == blockColSize - 1 && colIndex < colSize - 1) {
				return true;
			} else if (prevBlockCols > 0) {
				return relativeColIndex == 0;
			}
		}
		return false;
	}

	enum SEAT_TYPE {
		AISLE("A"), WINDOW("W"), MIDDLE("M");

		private String text;

		private SEAT_TYPE(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

}
