package group11.hitori.com;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

public class Rules {

	private static WriteFile w;
	private static int count = 0;
	private static int sizeOld = 0;
	private static String time = "";

	private static boolean status = false;

	private static int[][] puzzle;
	private static int[][] flag;
	private static String Url = "src\\filesat\\Sat.cnf";
	private static String method;

	private static ArrayList<Integer> save;

	private boolean statusDelFirst = false;
	private boolean statusDelLast = false;

	@SuppressWarnings("static-access")
	public Rules(int[][] puzzle, String method) {
		this.puzzle = puzzle;
		flag = new int[puzzle.length][puzzle.length];
		save = new ArrayList<Integer>();
		w = new WriteFile();
		this.method = method;

		init();

	}

	public void init() {
		setCount(0);
		String title = "";

		long startTime = System.currentTimeMillis();

		try {
			int variable = 0;
			if (method.equals("ChainsAndCycles")) {
				variable = puzzle.length * puzzle.length;
			} else if (method.equals("Connectivity")) {
				variable = puzzle.length * puzzle.length + puzzle.length
						* puzzle.length * puzzle.length * puzzle.length;
			}

			title = "p cnf " + variable;

			String numberClause = count + " ";

			for (int i = 0; i < numberClause.length(); i++) {
				title += " ";
			}
			w.write(title + "         ");

		} catch (IOException e) {
			e.printStackTrace();
		}

		checkRuleOne();
		checkRuleTwo();
	//	getAroundCell();

		if (method.equals("ChainsAndCycles")) {
			ChainsAndCycles();
		} else if (method.equals("Connectivity")) {
			Connectivity();
		}

		w.closeFile();

		try {
			RandomAccessFile raf = new RandomAccessFile(Url, "rw");
			title = title.trim();
			raf.seek(title.length());
			raf.writeBytes(" " + count);
			raf.writeBytes("\n\n");
			raf.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();

		long total = endTime - startTime;

		
		if (total < 60) {
			setTime(total + "ms");
		} else {
			setTime((float)total/1000 + "s.");
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		Rules.time = time;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		Rules.count = count;
	}

	public int[][] getPuzzle() {
		return puzzle;
	}

	private void checkRuleOne() {
		for (int i = 0; i < puzzle.length; i++) {
			traceRowOfRuleOne(puzzle, i);
		}
		for (int i = 0; i < puzzle.length; i++) {
			traceColumnOfRuleOne(puzzle, i);
		}
		for (int i = 0; i < flag.length; i++) {
			for (int j = 0; j < flag.length; j++) {
				if (flag[i][j] == 0) {
					String value = i * flag.length + (j + 1) + " 0\n";

					try {
						w.write(value);
						count++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void checkRuleTwo() {

		for (int i = 0; i < flag.length; i++) {
			traceRowOfRuleTwo(flag, i);
		}
		for (int i = 0; i < flag.length; i++) {
			traceColumnOfRuleTwo(flag, i);
		}

	}

	public void ChainsAndCycles() {
		isChains();
		isCycles();
	}

	public void Connectivity() {
		conditionalCell();
		relationCell();
		eraseChains();
		eraseCycles();
	}

	private void traceRowOfRuleOne(int a[][], int location) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[location][i] == a[location][j]) {
					int x = location * a.length + i + 1;
					int y = location * a.length + j + 1;
					String value = "-" + x + " -" + y + " 0\n";

					try {
						w.write(value);
						count++;
					} catch (IOException e) {
						e.printStackTrace();
					}

					flag[location][j] = 1;
					flag[location][i] = 1;
				}
			}
		}

	}

	private void traceColumnOfRuleOne(int a[][], int location) {
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i + 1; j < a.length; j++) {
				if (a[i][location] == a[j][location]) {
					int x = i * a.length + (location + 1);
					int y = j * a.length + (location + 1);
					String value = "-" + x + " -" + y + " 0\n";

					try {
						w.write(value);
						count++;
					} catch (IOException e) {
						e.printStackTrace();
					}

					flag[j][location] = 1;
					flag[i][location] = 1;
				}
			}
		}

	}

	private void traceRowOfRuleTwo(int a[][], int location) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[location][i] == 1 && a[location][i] == a[location][i + 1]) {
				int x = location * a.length + i + 1;
				int y = x + 1;
				String value = x + " " + y + " 0\n";

				try {
					w.write(value);
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void traceColumnOfRuleTwo(int a[][], int location) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i][location] == 1 && a[i][location] == a[i + 1][location]) {
				int x = i * a.length + (location + 1);
				int y = (i + 1) * a.length + (location + 1);
				String value = x + " " + y + " 0\n";

				try {
					w.write(value);
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Kiểm tra luật 3 dùng phương pháp Connectivity:
	 */

	public void getAroundCell(){
		int input[][] = duplicate(flag);
		
		for(int i=0; i<input.length; i++){
			for(int j=0; j<input.length; j++){
				String result = "";
				
				if (i - 1 >= 0) {
					result += getIndex(i, j) + " " + getIndex(i-1, j) + " 0\n";
					count ++;
				}

				if (i + 1 <= input.length-1) {
					result += getIndex(i, j) + " " + getIndex(i+1, j) + " 0\n";
					count ++;
				}

				if (j - 1 >= 0) {
					result += getIndex(i, j) + " " + getIndex(i, j-1) + " 0\n";
					count ++;
				}

				if (j + 1 <= input.length-1) {
					result += getIndex(i, j) + " " + getIndex(i, j+1) + " 0\n";
					count ++;
				}
				
				if(!result.isEmpty()){
					try {
						w.write(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	// Liên kết chéo giữa 1 ô đen với các ô đen xung quanh nó
	public void conditionalCell() {
		int[][] input = duplicate(flag);

		for (int i = 0; i < flag.length; i++) {
			for (int j = 0; j < flag.length; j++) {
				if (input[i][j] == 1) {

					String result = "";
					int index = getIndex(i, j);

					if (i - 1 >= 0 && j - 1 >= 0) {
						if (input[i - 1][j - 1] == 1) {
							int index1 = index - 1 - input.length;
							result += index + " " + index1 + " "
									+ getIndex(i, j, i - 1, j - 1) + " "
									+ getIndex(i - 1, j - 1, i, j) + " 0\n";
							count++;

							result += "-" + getIndex(i, j, i - 1, j - 1) + " -"
									+ getIndex(i - 1, j - 1, i, j) + " 0\n";
							count++;

						}
					}

					if (i - 1 >= 0 && j + 1 < input.length) {
						if (input[i - 1][j + 1] == 1) {
							int index1 = index + 1 - input.length;
							result += index + " " + index1 + " "
									+ getIndex(i, j, i - 1, j + 1) + " "
									+ getIndex(i - 1, j + 1, i, j) + " 0\n";
							count++;

							result += "-" + getIndex(i, j, i - 1, j + 1) + " -"
									+ getIndex(i - 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i + 1 < input.length && j - 1 >= 0) {
						if (input[i + 1][j - 1] == 1) {
							int index1 = index - 1 + input.length;
							result += index + " " + index1 + " "
									+ getIndex(i, j, i + 1, j - 1) + " "
									+ getIndex(i + 1, j - 1, i, j) + " 0\n";
							count++;

							result += "-" + getIndex(i, j, i + 1, j - 1) + " -"
									+ getIndex(i + 1, j - 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i + 1 < input.length && j + 1 < input.length) {
						if (input[i + 1][j + 1] == 1) {
							int index1 = index + 1 + input.length;
							result += index + " " + index1 + " "
									+ getIndex(i, j, i + 1, j + 1) + " "
									+ getIndex(i + 1, j + 1, i, j) + " 0\n";
							count++;

							result += "-" + getIndex(i, j, i + 1, j + 1) + " -"
									+ getIndex(i + 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (!result.isEmpty()) {
						try {
							w.write(result);

							// input[i][j] = 2;
						} catch (IOException e) {

							e.printStackTrace();
						}
					}

				}
			}
		}
		// showRelation();
	}

	// Tính chất bắc cầu
	public void relationCell() {
		int[][] input = duplicate(flag);

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (input[i][j] == 1) {
					if (i - 1 >= 0 && j - 1 >= 0) {
						if (input[i - 1][j - 1] == 1) {
							String result = getRelation(i - 1, j - 1, i, j,
									input);
							if (!result.isEmpty()) {
								try {
									w.write(result);
								} catch (IOException e) {

									e.printStackTrace();
								}
							}
						}
					}

					if (i - 1 >= 0 && j + 1 <= input.length - 1) {
						if (input[i - 1][j + 1] == 1) {
							String result = getRelation(i - 1, j + 1, i, j,
									input);
							if (!result.isEmpty()) {
								try {
									w.write(result);
								} catch (IOException e) {

									e.printStackTrace();
								}
							}
						}
					}

					if (i + 1 <= input.length - 1 && j - 1 >= 0) {
						if (input[i + 1][j - 1] == 1) {
							String result = getRelation(i + 1, j - 1, i, j,
									input);
							if (!result.isEmpty()) {
								try {
									w.write(result);
								} catch (IOException e) {

									e.printStackTrace();
								}
							}
						}
					}

					if (i + 1 <= input.length - 1 && j + 1 <= input.length - 1) {
						if (input[i + 1][j + 1] == 1) {
							String result = getRelation(i + 1, j + 1, i, j,
									input);
							if (!result.isEmpty()) {
								try {
									w.write(result);
								} catch (IOException e) {

									e.printStackTrace();
								}
							}
						}
					}

				}
			}
		}
	}

	public String getRelation(int i, int j, int i_parent, int j_parent,
			int input[][]) {
		String result = "";

		if (i - 1 >= 0 && j - 1 >= 0) {
			if (input[i - 1][j - 1] == 1) {
				result += "-" + getIndex(i_parent, j_parent, i, j) + " -"
						+ getIndex(i, j, i - 1, j - 1) + " "
						+ getIndex(i_parent, j_parent, i - 1, j - 1) + " 0\n";
				count++;
			}
		}

		if (i - 1 >= 0 && j + 1 <= input.length - 1) {
			if (input[i - 1][j + 1] == 1) {
				result += "-" + getIndex(i_parent, j_parent, i, j) + " -"
						+ getIndex(i, j, i - 1, j + 1) + " "
						+ getIndex(i_parent, j_parent, i - 1, j + 1) + " 0\n";
				count++;
			}
		}

		if (i + 1 <= input.length - 1 && j - 1 >= 0) {
			if (input[i + 1][j - 1] == 1) {
				result += "-" + getIndex(i_parent, j_parent, i, j) + " -"
						+ getIndex(i, j, i + 1, j - 1) + " "
						+ getIndex(i_parent, j_parent, i + 1, j - 1) + " 0\n";
				count++;
			}
		}

		if (i + 1 <= input.length - 1 && j + 1 <= input.length - 1) {
			if (input[i + 1][j + 1] == 1) {
				result += "-" + getIndex(i_parent, j_parent, i, j) + " -"
						+ getIndex(i, j, i + 1, j + 1) + " "
						+ getIndex(i_parent, j_parent, i + 1, j + 1) + " 0\n";
				count++;
			}
		}

		return result;
	}

	/*
	 * Mã hóa Chains: xây dựng không có liên kết chéo ở 2 ô biên
	 */
	public void eraseChains() {
		int[][] input = duplicate(flag);

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (i == 0 || j == 0 || i == input.length - 1
						|| j == input.length - 1) {
					if (input[i][j] == 1) {
						String result = "";
						if (i - 1 >= 0 && j - 1 >= 0) {
							result += "-" + getIndex(i - 1, j - 1, i, j)
									+ " 0\n";
							count++;
						}

						if (i - 1 >= 0 && j + 1 <= input.length - 1) {
							result += "-" + getIndex(i - 1, j + 1, i, j)
									+ " 0\n";
							count++;
						}

						if (i + 1 <= input.length - 1 && j - 1 >= 0) {
							result += "-" + getIndex(i + 1, j - 1, i, j)
									+ " 0\n";
							count++;
						}

						if (i + 1 <= input.length - 1
								&& j + 1 <= input.length - 1) {
							result += "-" + getIndex(i + 1, j + 1, i, j)
									+ " 0\n";
							count++;
						}

						if (!result.isEmpty()) {
							try {
								w.write(result);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		getBoundary();
	//	getBoundary(input);

	}

	public void getBoundary(int input[][]) {

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (i == 0 || j == 0 || i == input.length - 1
						|| j == input.length - 1) {
					if (input[i][j] == 1) {
						input[i][j] = 2;
						String result = searchBoundaryCell(i, j, input);
						input[i][j] = 1;
						if (!result.isEmpty()) {
							try {
								w.write(result);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public String searchBoundaryCell(int n, int m, int input[][]) {
		String result = "";
		int index = getIndex(n, m);

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (i == 0 || j == 0 || i == input.length - 1
						|| j == input.length - 1) {
					if (input[i][j] == 1) {
						if (index % 2 == 0) {
							if (getIndex(i, j) % 2 == 0) {
								result += "-" + getIndex(n, m, i, j) + " 0\n";
								count++;
							}
						} else {
							if (getIndex(i, j) % 2 != 0) {
								result += "-" + getIndex(n, m, i, j) + " 0\n";
								count++;
							}
						}
					}
				}
			}
		}

		return result;
	}

	private void getBoundary() {

		int input[][] = duplicate(flag);

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (input[i][j] == 1) {
					if (i == 0 || j == 0 || i == input.length - 1
							|| j == input.length - 1) {
						searchBoundaryTopBottom(i, j, i, j, input);
						save.clear();
					}
				}
			}
		}

		for (int j = input.length - 1; j >= 0; j--) {
			for (int i = input.length - 1; i >= 0; i--) {
				if (input[i][j] == 1) {
					if (i == 0 || j == 0 || i == input.length - 1
							|| j == input.length - 1) {
						searchBoundaryRightLeft(i, j, i, j, input);
						save.clear();
					}
				}
			}
		}
		save.clear();

	}

	private void searchBoundaryTopBottom(int i, int j, int i_parent,
			int j_parent, int input[][]) {

		if (i + 1 < input.length && j - 1 >= 0) {
			if (input[i + 1][j - 1] == 1) {
				if (!checkSame(save, i * input.length + (j + 1))) {
					save.add(i * input.length + (j + 1));
				}
				if (j - 1 == 0 || i + 1 == input.length - 1) {
					save.add((i + 1) * input.length + j);

					try {
						w.write("-"
								+ getIndex(i_parent, j_parent, i + 1, j - 1)
								+ " 0\n");
						count++;
						w.write("-"
								+ getIndex(i + 1, j - 1, i_parent, j_parent)
								+ " 0\n");
						count++;
					} catch (IOException e) {

						e.printStackTrace();
					}

					save.remove(save.size() - 1);

				} else {
					searchBoundaryTopBottom(i + 1, j - 1, i_parent, j_parent,
							input);
				}
			}
			if (j < input.length - 1 && statusDelFirst == false) {
				if (input[i + 1][j + 1] == 0) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				} else if (input[i + 1][j + 1] == 1 && input[i + 1][j - 1] == 1) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				}
			}
			statusDelFirst = false;
		}
		if (i + 1 < input.length && j + 1 < input.length) {
			if (input[i + 1][j + 1] == 1) {
				if (!checkSame(save, i * input.length + (j + 1))) {
					save.add(i * input.length + (j + 1));
				}
				if (i + 1 == input.length - 1 || j + 1 == input.length - 1) {
					save.add((i + 1) * input.length + (j + 2));

					try {
						w.write("-"
								+ getIndex(i_parent, j_parent, i + 1, j + 1)
								+ " 0\n");
						count++;
						w.write("-"
								+ getIndex(i + 1, j + 1, i_parent, j_parent)
								+ " 0\n");
						count++;
					} catch (IOException e) {

						e.printStackTrace();
					}

					save.remove(save.size() - 1);
				} else {
					searchBoundaryTopBottom(i + 1, j + 1, i_parent, j_parent,
							input);
				}
			}
			if (j - 1 >= 0 && statusDelFirst == false) {
				if (input[i + 1][j - 1] == 0) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				} else if (input[i + 1][j + 1] == 1 && input[i + 1][j - 1] == 1) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				}
			}
			statusDelFirst = false;
		}

	}

	private void searchBoundaryRightLeft(int i, int j, int i_parent,
			int j_parent, int input[][]) {
		if (i - 1 >= 0 && j - 1 >= 0) {
			if (input[i - 1][j - 1] == 1) {
				if (!checkSame(save, i * input.length + (j + 1))) {
					save.add(i * input.length + (j + 1));
				}

				if (j - 1 == 0 || i - 1 == 0) {
					if (!checkSame(save, (i - 1) * input.length + j)) {
						save.add((i - 1) * input.length + j);
					}

					try {
						w.write("-"
								+ getIndex(i_parent, j_parent, i - 1, j - 1)
								+ " 0\n");
						count++;
						w.write("-"
								+ getIndex(i - 1, j - 1, i_parent, j_parent)
								+ " 0\n");
						count++;
					} catch (IOException e) {

						e.printStackTrace();
					}

					save.remove(save.size() - 1);

				} else {
					searchBoundaryRightLeft(i - 1, j - 1, i_parent, j_parent,
							input);
				}
			}
			if (i < input.length - 1 && statusDelLast == false) {
				if (input[i + 1][j - 1] == 0) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelLast = true;
					}
				} else if (input[i + 1][j - 1] == 1 && input[i - 1][j - 1] == 1) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				}
			}
			statusDelLast = false;
		}

		if (i + 1 < input.length && j - 1 >= 0) {
			if (input[i + 1][j - 1] == 1) {
				if (!checkSame(save, i * input.length + (j + 1))) {
					save.add(i * input.length + (j + 1));
				}
				if (i + 1 == input.length - 1 || j - 1 == 0) {
					if (!checkSame(save, (i + 1) * input.length + j)) {
						save.add((i + 1) * input.length + j);
					}

					try {
						w.write("-"
								+ getIndex(i_parent, j_parent, i + 1, j - 1)
								+ " 0\n");
						count++;
						w.write("-"
								+ getIndex(i + 1, j - 1, i_parent, j_parent)
								+ " 0\n");
						count++;
					} catch (IOException e) {

						e.printStackTrace();
					}

					save.remove(save.size() - 1);
				} else {
					searchBoundaryRightLeft(i + 1, j - 1, i_parent, j_parent,
							input);
				}
			}
			if (i - 1 >= 0 && statusDelLast == false) {
				if (input[i - 1][j - 1] == 0) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelLast = true;
					}
				} else if (input[i + 1][j - 1] == 1 && input[i - 1][j - 1] == 1) {
					if (!save.isEmpty()) {
						save.remove(save.size() - 1);
						statusDelFirst = true;
					}
				}
				statusDelLast = false;
			}
		}

	}

	/*
	 * Mã hóa Cycles: 1. Kết nối chéo không được phép hướng đến cùng 1 ô 2.
	 * Không tạo kết nối đối với chính nó:
	 */
	public void eraseCycles() {

		int input[][] = duplicate(flag);

		// 1.
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if (input[i][j] == 1) {
					String result = "";

					if (i - 1 >= 0 && j - 1 >= 0 && j + 1 <= input.length - 1) {
						if (input[i - 1][j - 1] == 1
								&& input[i - 1][j + 1] == 1) {
							result += "-" + getIndex(i - 1, j - 1, i, j) + " -"
									+ getIndex(i - 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i + 1 <= input.length - 1 && j - 1 >= 0
							&& j + 1 <= input.length - 1) {
						if (input[i + 1][j - 1] == 1
								&& input[i + 1][j + 1] == 1) {
							result += "-" + getIndex(i + 1, j - 1, i, j) + " -"
									+ getIndex(i + 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i - 1 >= 0 && j - 1 >= 0 && i + 1 <= input.length - 1) {
						if (input[i - 1][j - 1] == 1
								&& input[i + 1][j - 1] == 1) {
							result += "-" + getIndex(i - 1, j - 1, i, j) + " -"
									+ getIndex(i + 1, j - 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i - 1 >= 0 && i + 1 <= input.length - 1
							&& j + 1 <= input.length - 1) {
						if (input[i - 1][j + 1] == 1
								&& input[i + 1][j + 1] == 1) {
							result += "-" + getIndex(i - 1, j + 1, i, j) + " -"
									+ getIndex(i + 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i - 1 >= 0 && j - 1 >= 0 && i + 1 <= input.length - 1
							&& j + 1 <= input.length - 1) {
						if (input[i - 1][j - 1] == 1
								&& input[i + 1][j + 1] == 1) {
							result += "-" + getIndex(i - 1, j - 1, i, j) + " -"
									+ getIndex(i + 1, j + 1, i, j) + " 0\n";
							count++;
						}
					}

					if (i - 1 >= 0 && j - 1 >= 0 && i + 1 <= input.length - 1
							&& j + 1 <= input.length - 1) {
						if (input[i - 1][j + 1] == 1
								&& input[i + 1][j - 1] == 1) {
							result += "-" + getIndex(i - 1, j + 1, i, j) + " -"
									+ getIndex(i + 1, j - 1, i, j) + " 0\n";
							count++;
						}
					}

					if (!result.isEmpty()) {

						try {
							w.write(result);
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
			}
		}

		// 2.

		String result = "";
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if(i==0||j==0||i==input.length-1||j==input.length-1){
					
				}else
				if (input[i][j] == 1) {
					result += "-" + getIndex(i, j, i, j) + " 0\n";
					count++;
				}
			}
		}

		if (!result.isEmpty()) {

			try {
				w.write(result);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public int getIndex(int i, int j) {
		return (i * flag.length + (j + 1));
	}

	public int getIndex(int i, int j, int i_new, int j_new) {
		int x = getIndex(i, j) * flag.length * flag.length
				+ getIndex(i_new, j_new);
		return x;
	}

	private boolean checkSame(ArrayList<Integer> c, int key) {
		for (int i = 0; i < c.size(); i++)
			if (c.get(i) == key)
				return true;
		return false;
	}

	/*
	 * Kiểm tra luật 3 dùng phương pháp Chains and Cycles:
	 */

	public void isChains() {
		int[][] copy = duplicate(flag);

		for (int i = 0; i < flag.length; i++) {
			for (int j = 0; j < flag.length; j++) {
				if (copy[i][j] == 1) {

					if (i == 0 || j == 0 || i == flag.length - 1
							|| j == flag.length - 1) {
						int[][] cache = duplicate(copy);
						searchChains(i, j, i * cache.length + (j + 1), cache);
						copy[i][j] = 2;
					}

				}
				save.clear();
			}
		}

	}

	public void searchChains(int i, int j, int begin, int[][] input) {
		status = false;

		save.add(i * input.length + (j + 1));

		/* int sizeOld = save.size(); */
		sizeOld = save.size();

		input[i][j] = 2;

		if (i - 1 >= 0 && j - 1 >= 0) {
			nextChains(i - 1, j - 1, begin, input);
			// sizeOld = save.size();
		}
		if (i - 1 >= 0 && j + 1 <= input.length - 1) {
			nextChains(i - 1, j + 1, begin, input);
			// sizeOld = save.size();
		}

		if (i + 1 <= input.length - 1 && j - 1 >= 0) {
			nextChains(i + 1, j - 1, begin, input);
			// sizeOld = save.size();
		}
		if (i + 1 <= input.length - 1 && j + 1 <= input.length - 1) {
			nextChains(i + 1, j + 1, begin, input);
			// sizeOld = save.size();
		}

		if (save.size() == sizeOld) {
			if (!save.isEmpty()) {
				save.remove(save.size() - 1);
				input[i][j] = 1;
				status = false;
				sizeOld = save.size();
			}
		}
	}

	public void nextChains(int i, int j, int begin, int[][] input) {
		if (input[i][j] == 1) {
			if (i == 0 || j == 0 || i == input.length - 1
					|| j == input.length - 1) {

				save.add(i * input.length + (j + 1));

				try {
					w.write(convertString(save));
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (!save.isEmpty()) {
					save.remove(save.size() - 1);
				}

			} else {
				status = true;
				if(input.length < 17){
					if (!isSonCycles(save, i * input.length + (j + 1))) {
						searchChains(i, j, begin, input);
					}
				}else {
					if (!isSon(save, i * input.length + (j + 1))) {
						searchChains(i, j, begin, input);
					}
				}
			}
		}
	}

	public void isCycles() {
		int[][] copy = duplicate(flag);

		for (int i = 0; i < flag.length; i++) {
			for (int j = 0; j < flag.length; j++) {
				if (copy[i][j] == 1) {

					int[][] cache = duplicate(copy);

					checkShape(i, j, cache);
					copy[i][j] = 2;
					searchCycles(i, j, i, (i + 1) * cache.length + (j + 1) - 1,
							cache);

				}
				save.clear();
			}
		}
	}

	public void searchCycles(int i, int j, int column, int begin, int input[][]) {
		status = false;
		save.add(i * input.length + (j + 1));

		input[i][j] = 2;

		if (i + 1 <= input.length - 1 && j - 1 >= 0) {
			nextCycles(i + 1, j - 1, column, begin, input);
		}
		if (i + 1 <= input.length - 1 && j + 1 <= input.length - 1) {
			nextCycles(i + 1, j + 1, column, begin, input);
		}
		if (i - 1 >= 0 && j + 1 <= input.length - 1 && i - 1 >= 0) {
			nextCycles(i - 1, j + 1, column, begin, input);
		}

		if (i - 1 >= 0 && j - 1 >= 0 && i - 1 >= 0) {
			nextCycles(i - 1, j - 1, column, begin, input);
		}

		if (status == false && !save.isEmpty()) {
			save.remove(save.size() - 1);
			input[i][j] = 1;
		}

	}

	public void nextCycles(int i, int j, int column, int begin, int input[][]) {

		if ((i * input.length + (j + 1)) == begin) {
			if (save.size() > 2) {
				save.add(i * input.length + (j + 1));
				try {
					w.write(convertString(save));
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}

				save.remove(save.size() - 1);
			}
		} else if (input[i][j] == 1) {
			status = true;

			if (input.length <= 12) {
				if (!isSon(save, i * input.length + (j + 1))) {
					searchCycles(i, j, column, begin, input);
				}
			} else {
				if (!isSon(save, i * input.length + (j + 1))) {
					searchCycles(i, j, column, begin, input);
				}
			}
		}

	}

	private void checkShape(int i, int j, int input[][]) {
		ArrayList<Integer> saveShape = new ArrayList<Integer>();
		if (i + 2 < input.length - 1 && j + 1 < input.length - 1 && j - 1 >= 0) {
			if (input[i + 2][j] == 1 && input[i + 1][j - 1] == 1
					&& input[i + 1][j + 1] == 1) {
				saveShape.add(i * input.length + (j + 1));
				saveShape.add((i + 1) * input.length + (j + 1) - 1);
				saveShape.add((i + 2) * input.length + (j + 1));
				saveShape.add((i + 1) * input.length + (j + 1) + 1);

				// input[i+2][j] = 0;

				try {
					w.write(convertString(saveShape));
					count++;
				} catch (IOException e) {
					e.printStackTrace();
				}

				saveShape.clear();

			}
		}
	}

	public boolean isSon(ArrayList<Integer> list, int x) {
		boolean status = false;
		for (int ii = 0; ii < list.size(); ii++) {
			int parent = (list.get(ii) + x) / 2 - flag.length;
			for (int jj = 0; jj < ii; jj++)
				if (list.get(jj) == parent)
					status = true;
		}

		return status;
	}

	public boolean isSonCycles(int x) {
		boolean status = false;

		ArrayList<Integer> copy = duplicate(save);
		ArrayList<Integer> a = new ArrayList<Integer>();

		copy.add(x);

		for (int i = 0; i < copy.size(); i++) {
			int left = copy.get(i);
			for (int j = i + 1; j < copy.size(); j++) {
				int right = copy.get(j);
				if (left + 2 == right || left - 2 == right) {
					int top = (left + right) / 2 - flag.length;
					int bottom = (left + right) / 2 + flag.length;
					if (searchKey(copy, top) && searchKey(copy, bottom)) {
						a.add(copy.indexOf(left));
						a.add(copy.indexOf(right));
						a.add(copy.indexOf(top));
						a.add(copy.indexOf(bottom));

						Collections.sort(a);

						for (int k = 0; k < a.size() - 1; k++)
							if (a.get(k + 1) - a.get(k) == 1) {
								status = true;
							} else {
								status = false;
								break;
							}
					}
				}
			}
		}

		copy.clear();
		a.clear();

		return status;
	}

	public boolean isSonCycles(ArrayList<Integer> list, int x) {
		boolean status = false;

		for (int i = 0; i < list.size(); i++) {
			int parent = (list.get(i) + x) / 2 - flag.length;

			for (int j = 0; j < i; j++)
				if (list.get(j) == parent && (i + 2 == x || i == x + 2))
					status = true;
		}

		return status;
	}

	public boolean searchKey(ArrayList<Integer> list, int x) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) == x)
				return true;
		return false;
	}

	public ArrayList<Integer> duplicate(ArrayList<Integer> list) {
		ArrayList<Integer> copy = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++)
			copy.add(list.get(i));

		return copy;
	}

	public int[][] duplicate(int[][] input) {
		int[][] copy = new int[input.length][input.length];
		for (int i = 0; i < input.length; i++)
			for (int j = 0; j < input.length; j++)
				copy[i][j] = input[i][j];
		return copy;
	}

	public String convertString(ArrayList<Integer> s) {
		String result = "";
		for (int i = 0; i < s.size(); i++)
			result += s.get(i) + " ";
		return result + "0\n";
	}
}