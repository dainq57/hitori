package group11.hitori.com;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;


public class HitoriUI extends JFrame implements ActionListener, ItemListener{
	
	private static final long serialVersionUID = 1L;
	
	private int sizeBoard;
	private static int sizeCell = 50;
	private static int sizeFont;
	private int [][] puzzle;
	
	private Rules rule;
	private static ArrayList<Integer> resultCode;
	
	private static JLabel info;
	private static JComboBox<Integer> boxSize;
	private static JButton newGame;
	private static JButton solver1;
	private static JButton solver2;
	private static JButton zoom;
	private static JPanel boardLayout;
	
	private static JLabel [][] boardLabel;
	
	
	private static JScrollPane scroll;
	private static JPanel parentLayout;
	
	public HitoriUI(){
		sizeBoard = 5;
		initComponent();
	}
	
	public int getSizeBoard() {
		return sizeBoard;
	}

	public void setSizeBoard(int sizeBoard) {
		this.sizeBoard = sizeBoard;
	}
	
	public int[][] getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(int[][] puzzle) {
		this.puzzle = puzzle;
	}

	public void initComponent(){
		
		try{
			UIManager.put("ScrollBarUI", "group11.hitori.com.MyScrollBarUI");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.Windows.WindowLookAndFeel");
		}catch(Exception e){
			
		}
		
		setTitle("Hitori Puzzle");
		setIconImage(getToolkit().getImage("src\\img\\icon.jpeg"));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);
		setLocation(20, 20);
		setMinimumSize(new Dimension(864, 560));
		setMaximumSize(new Dimension(1366, 768));
		setResizable(true);
		
		parentLayout = new JPanel(new GridLayout(1, 1));
		boardLayout = new JPanel();
		scroll = new JScrollPane();
		
		
		add(createOptions(),BorderLayout.LINE_START);
		add(parentLayout, BorderLayout.CENTER);
		
		initInput();
		
	}
	
	public void initInput(){
		
		Input in = new Input(sizeBoard);
		in.readFile();
		setPuzzle(in.getInput());
		
		createBoard();
		
	//	pack();
	}
	
	public JPanel createOptions(){
		
		JPanel option = new JPanel();
		option.setSize(180, 90);
		option.setLayout(null);
		option.setBorder(BorderFactory.createTitledBorder(""));
		option.setPreferredSize(new Dimension(300,  sizeBoard * sizeCell + (sizeBoard-1)*4));
		option.setBackground(new Color(255, 255, 255));
		
		
		newGame = new JButton("New Game");
		/*newGame.setPreferredSize(new Dimension(180, 90));*/
		newGame.setSize(200, 70);
		newGame.setFocusable(false);
		newGame.setBackground(new Color(102, 0, 204));
		newGame.setFont(new Font("Segoe UI", Font.BOLD, 30));
		newGame.setForeground(new Color(255, 255, 255));
		newGame.setLocation(15, 15);
		newGame.setBorderPainted(false);
		newGame.addActionListener(this);
		
		
		int [] list = {5, 6, 7, 8, 9, 10, 12, 15,17, 20, 21, 23, 24, 25, 30};
		boxSize = new JComboBox<Integer>();
		for(int i=0; i<list.length; i++){
			boxSize.addItem(list[i]);
		}
		boxSize.setBackground(new Color(255, 255, 255));
		boxSize.setSize(50, 35);
		boxSize.setSelectedItem(0);
		boxSize.setLocation(230, 50);
		boxSize.addItemListener(this);
		
		solver1 = new JButton("Solver1");
		solver1.setSize(100, 40);
		solver1.setFocusable(false);
		solver1.setBackground(new Color(102, 0, 204));
		solver1.setFont(new Font("Segoe UI", Font.BOLD, 16));
		solver1.setForeground(new Color(255, 255, 255));
		solver1.setLocation(15, 130);
		solver1.setBorderPainted(false);
		solver1.setToolTipText("<html><b style='background-color: rgb(255, 255, 255);font-face:Segoe UI;color: rgb(0, 0, 0);'>"
				+ "Chains and Cycles</b></html>");
		solver1.addActionListener(this);
		
		solver2 = new JButton("Solver2");
		solver2.setSize(100, 40);
		solver2.setFocusable(false);
		solver2.setBackground(new Color(102, 0, 204));
		solver2.setFont(new Font("Segoe UI", Font.BOLD, 16));
		solver2.setForeground(new Color(255, 255, 255));
		solver2.setLocation(180, 130);
		solver2.setBorderPainted(false);
		solver2.setToolTipText("<html><b style='background-color: rgb(255, 255, 255);font-face:Segoe UI;color: rgb(0, 0, 0);'>"
				+ "Connectivity</b></html></b></html>");
		solver2.addActionListener(this);
		
		zoom = new JButton("Zoom");
		zoom.setSize(70, 30);
		zoom.setFocusable(false);
		zoom.setBackground(new Color(148, 170, 214));
		zoom.setFont(new Font("Segoe UI", Font.BOLD, 10));
		zoom.setForeground(new Color(255, 255, 255));
		zoom.setLocation(195, 185);
		zoom.setBorderPainted(false);
		zoom.setEnabled(false);
		zoom.addActionListener(this);
		
		info = new JLabel();
		
		info.setSize(250, 300);
		info.setFont(new Font("Segoe UI", Font.PLAIN, 17));
		info.setForeground(new Color(102, 0, 204));
		info.setLocation(35, 240);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		
		option.add(newGame);
		option.add(boxSize);
		option.add(solver1);
		option.add(solver2);
		option.add(zoom);
		option.add(info);
		
		
		
		
		return option;
	}

	public void createBoard(){
		boardLayout.removeAll();
		boardLayout.setLayout(new GridLayout(sizeBoard, sizeBoard, 4, 4));
		
		
		parentLayout.remove(boardLayout);
		
		boardLabel = new JLabel[sizeBoard][sizeBoard];
		
		for(int i=0; i<sizeBoard; i++)
			for(int j=0; j<sizeBoard; j++){
				
				boardLabel[i][j] = new JLabel("", JLabel.CENTER);
				boardLabel[i][j].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(99, 91, 162)));
				boardLabel[i][j].setBackground(new Color(255, 255, 255));
				boardLabel[i][j].setPreferredSize(new Dimension(sizeCell, sizeCell));
				boardLabel[i][j].setOpaque(true);
			
			}
		
		
		sizeFont = 40;
		if(sizeBoard == 8){
			sizeFont = 26;
		}else if(sizeBoard == 9){
			sizeFont = 25;
		}else if(sizeBoard == 10){
			sizeFont = 27;
		}else if(sizeBoard == 12){
			sizeFont = 18;
		}else if(sizeBoard >= 15){
			sizeFont = 18;
		}
		
		
		for(int i=0; i<sizeBoard; i++)
			for(int j=0; j<sizeBoard; j++){
				
				boardLabel[i][j].setFont(new Font("FrankRuehl", Font.BOLD, sizeFont));
			}
		// import input data
		for(int i=0; i<sizeBoard; i++)
			for(int j=0; j<sizeBoard; j++)
				boardLabel[i][j].setText(puzzle[i][j] + "");
		
		for(int i=0; i<sizeBoard; i++)
			for(int j=0; j<sizeBoard; j++)
				boardLayout.add(boardLabel[i][j]);
		
		boardLayout.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		boardLayout.setBackground(new Color(215, 215, 215));
		
		scroll.setViewportView(boardLayout);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.getVerticalScrollBar().setUI(new MyScrollBarUI());
		scroll.getHorizontalScrollBar().setUI(new MyScrollBarUI());
		
		parentLayout.removeAll();	
		
		if(sizeBoard < 15){
			if(sizeBoard >= 10 && sizeBoard < 15){
				parentLayout.setPreferredSize(new Dimension(670, 670));
			}else{
				parentLayout.setPreferredSize(new Dimension(0, 0));
			}
			parentLayout.add(boardLayout);
				
		}else{
				
			parentLayout.add(scroll);
			if(sizeBoard == 15){
				parentLayout.setPreferredSize(new Dimension(900, 680));
				
			}else{
				parentLayout.setPreferredSize(new Dimension(1000, 680));
			}
		}
		parentLayout.validate();
		parentLayout.repaint();
		pack();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		
		if(source == newGame){
			info.setText("");
			zoom.setEnabled(false);
			sizeCell = 50;
			initInput();
			
		}
		if(source == solver1){
			rule = new Rules(puzzle, "ChainsAndCycles");
			draw();
			if(sizeBoard >= 17){
				zoom.setEnabled(true);
			}
		}
		if(source == solver2){
			rule = new Rules(puzzle, "Connectivity");
			draw();
			if(sizeBoard >= 17){
				zoom.setEnabled(true);
			}
		}
		if(source == zoom){
			
			parentLayout.remove(scroll);
			parentLayout.add(boardLayout);
			parentLayout.validate();
			parentLayout.repaint();
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			
		}
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			
			String number = e.getItem().toString();
			number = number.trim();
			if (number.indexOf("5") == 0) {
				sizeBoard = 5;
				
			} else if (number.indexOf("6") == 0) {
				sizeBoard = 6;
				
			} else if (number.indexOf("7") == 0) {
				sizeBoard = 7;
				
			} else if (number.indexOf("8") == 0) {
				sizeBoard = 8;
				
			} else if (number.indexOf("9") == 0) {
				sizeBoard = 9;
				
			} else if (number.indexOf("10") == 0) {
				sizeBoard = 10;
				
			} else if (number.indexOf("12") == 0) {
				sizeBoard = 12;
				
			}else if (number.indexOf("15") == 0) {
				sizeBoard = 15;
				
			}else if (number.indexOf("17") == 0) {
				sizeBoard = 17;
				
			}else if (number.indexOf("20") == 0) {
				sizeBoard = 20;
				
			}else if(number.indexOf("21") == 0) {
				sizeBoard = 21;
				
			}else if(number.indexOf("23") == 0) {
				sizeBoard = 23;
				
			}else if(number.indexOf("24") == 0) {
				sizeBoard = 24;
				
			}else if(number.indexOf("25") == 0) {
				sizeBoard = 25;
				
			}else if(number.indexOf("30") == 0) {
				sizeBoard = 30;
				
			}
			
		}
	}

	public void draw(){
		info.setText("Solving ...");
		
		
		SatSolver solver = new SatSolver();
		solver.readCNF();
		solver.analysisString();
		resultCode = solver.getResult();
		if (!resultCode.isEmpty()) {
			for (int k = 0; k < resultCode.size(); k++) {
				if (resultCode.get(k) < 0) {
					int value = resultCode.get(k) * (-1);
					for (int i = 0; i < puzzle.length; i++) {
						for (int j = 0; j < puzzle.length; j++) {
							if (i * puzzle.length + (j + 1) == value) {
								boardLabel[i][j].setBackground(new Color(0, 0, 0));
								boardLabel[i][j].setForeground(new Color(255, 255, 255));
							}
						}
					}
				}
			}
		}
		
		if(!resultCode.isEmpty())
			resultCode.clear();

		if(solver.isStatus() == false){
			info.setText("<html><b>Unsatisfiable !</b></html>");
		}else{
			SatCommand command = new SatCommand();
			try {
				command.RunCommandSat();
				HashMap<String, String> infoSat = command.getInfoSat();
				info.setText("<html> Write file CNF: " + rule.getTime() + "  <br><br> <font ><b>Satisfiable ! </b></font><br>Varibale: " + infoSat.get("var") + 
						"<br>Clause: " + rule.getCount() + "<br>Time: " + infoSat.get("time") + "s.</html>");
			} catch (Exception e1) {
				System.out.println("Error Call SatCommand !");
				e1.printStackTrace();
			}
			
		}
	}
}