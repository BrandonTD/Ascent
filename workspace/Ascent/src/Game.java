//Vanshil Shah
//June 4th, 2013
//Game class, does a lot in relation to interaction between the other classes. This class is the most important class in the game. It has a lot of logic on how the game runs, GameStates and balancing the difficulty.

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	// screen dimensions and variables
	static int WIDTH =  800;//Toolkit.getDefaultToolkit().getScreenSize().width;
	static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height-80;
	private JFrame frame;

		
	// game updates per second
	static final int UPS = 60;
	static int t = 0;
	
	// variables for the thread
	private Thread thread;
	private boolean running;
	KeyBoard k = new KeyBoard();
	Mouse m = new Mouse();
	static MouseMotion mm = new MouseMotion();
	
	//Game_state Variables
	static int reset = -1;
	static boolean enterPressed;
	static  int GAME_STATE = 0, GAME_MENU = 0, GAME_INSTRUCTION = 1,GAME_VIEW_HIGHSCORE = 2, GAME_PLAY = 3, GAME_PAUSE = 5, PAUSED_STATE = -1, GAME_WRITE_HIGHSCORE = 6, LOST_STATE = -1;
	static  int pausedBar_State = 0, pausedBar_Menu = 0, pausedBar_Instructions = 1, pausedBar_TimeTrial = 3, pausedBar_Survival = 4; 
	static  int menu_State = 1, menu_Menu = 0, menu_Instructions = 1, menu_Highscores = 2, menu_TimeTrial = 3, menu_Survival = 4; 

	
	double rateAverage = 0;
	
	//Menu buttons
	private MenuButton playButton = new MenuButton (225, 200, 356, 161, "PlayButton.png");
	private MenuButton helpButton = new MenuButton (250, 420, 300, 150, "HelpButton.png");
	private MenuButton quitButton = new MenuButton (250, 600, 300, 150, "QuitButton.png");
	private MenuButton [] menuButtons = {playButton, helpButton, quitButton}; 
	
	//Instruction Screen Variables
	
	//Highscore Variables
	static String playerName = "";
	public static ArrayList<Reflector> walls;
	Vector<Score> TimeTrialHS;
	Vector<Score> SurvivalHS;
	static int rating = 0;
	int score = 0;
	
	//Game_play Variables
	
	BufferedImage backgroundImg = null;
	
	// used for drawing items to the screen
	public Graphics2D graphics;
	
	//----------------------------------------------------------------------------------------
	// menu and gamestate changing methods
	public void updateGameState(){
		if (reset != -1 && reset != 5){
			GAME_STATE = reset;
			init();
			reset = -1;
		}
		if (reset == 5){
			togglePause();
			reset = -1;
		}

	}
	public void togglePause(){
		if (PAUSED_STATE == -1){
			PAUSED_STATE = GAME_STATE;
			GAME_STATE = GAME_PAUSE;
		}
		else {
			GAME_STATE = PAUSED_STATE;
			PAUSED_STATE = -1;
		}
	}
	
	// ---------------------------------------------------------------------------------------
	// initialize game objects, load media(pics, music, etc)
	public void init(){	
		if (GAME_STATE == GAME_MENU){
			initMenu();
		}
		if (GAME_STATE == GAME_INSTRUCTION){
			
		}
		if (GAME_STATE == GAME_VIEW_HIGHSCORE){
			
		}
		if (GAME_STATE == GAME_PLAY){
			initGamePlay();
		}
		if (GAME_STATE != GAME_PAUSE){
			
		}
		if (GAME_STATE == GAME_WRITE_HIGHSCORE){
			initWriteHighscore();
		}
	}

		public void initMenu(){
		/*	Vector<Integer> v = new Vector<Integer>(); 
			try {
		            Scanner s = new Scanner(new File("RatingsAndPlays.txt"));
		            while (s.hasNextLine()) {
		                v.add(Integer.parseInt(s.nextLine()));
		            }
		            s.close();
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        }
			rateAverage = 0;
			for(int i = 0; i<v.size();i++){
				rateAverage += v.elementAt(i);
			}
			rateAverage/=v.size();
			try {
				menuBackgroundImg = ImageIO.read(ResourceLoader.load("menuBackground.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			initBackground();
		}
		
		public void initGamePlay() {
			initWalls();
		}
			public void initWalls(){
				walls = new ArrayList<Reflector>();
			}
		
		
		public void initBackground(){
			try {
				//backgroundImg = ImageIO.read(ResourceLoader.load("b.png"));
				backgroundImg  = ImageIO.read(new File("b.png"));
			} catch (IOException e) {
			}
		}
		public void initWriteHighscore(){
			
		}
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// //update game objects
	public void update() {
		updateGameState();
		//t++;
		if (GAME_STATE == GAME_MENU){
			updateMenu();
		}
		if (GAME_STATE == GAME_INSTRUCTION){
			updateInstructions();
		}
		if (GAME_STATE == GAME_VIEW_HIGHSCORE){
			updateHighscoreScreen();
		}
		if (GAME_STATE == GAME_PLAY){
			updateGamePlay();
		}
		if (GAME_STATE != GAME_PAUSE){	
	
		}
		else {
			updatePauseBar();
		}
		if (GAME_STATE == GAME_WRITE_HIGHSCORE){
			//updateWriteHighscore();
		}
	}
		
	public void updateMenu() {
		if (m.getIsCLicked()) {
			Point click = m.getClick().getPoint();

			if (playButton.contains(click)) {
				GAME_STATE = GAME_PLAY;

			} else if (helpButton.contains(click)) {
				GAME_STATE = menu_Instructions;
			} else if (quitButton.contains(click)) {
				running = false;
			}
		}

		Point mousePosition = mm.getMouse();
	
		if (mousePosition != null) {
			for (MenuButton mb : menuButtons) {
				if (mb.contains(mousePosition)) {
					mb.setHoveringOver(true);
				} else {
					mb.setHoveringOver(false);
				}
			}
		}

	}

	public void updateInstructions() {
		updatePauseBar();
		k.shortCutKeys();
	}

	public void updateGamePlay() {
		k.shortCutKeys();
		updateWalls();
	}

	public void updateWalls() {
		Reflector r = m.get();
		if (r != null) {
			walls.add(r);
			for (int i = 0; i < walls.size(); i++)
				System.out.println(walls.get(i).getX1());
		}
		for (int i = 0; i < walls.size(); i++) {
			if (walls.get(i).isOutOfBounds()) {
				walls.remove(i);
			}
		}

	}
		public void updateHighscoreScreen(){
			updatePauseBar();
		}
		
		public void updatePlayer() {	
		}
	
		public void updatePauseBar(){
			k.changePausedStates();
			if (enterPressed){
				reset = pausedBar_State;
				PAUSED_STATE = -1;
				enterPressed = false;
			}
		}
		
	/*	public void updateWriteHighscore(){
			if(hs_State == hs_Writing){
				k.writePlayerName();
				if (enterPressed){
					if (LOST_STATE == GAME_RANDOM){
						writeTimeInTextFile();
					}
					else if (LOST_STATE == GAME_SURVIVAL){
						writeScoreInTextFile();
					}
					hs_State = hs_Rating;
					enterPressed = false;
				}
			}
			if(hs_State == hs_Rating){
				k.rateGame();
				if (enterPressed){		
					writeRatingInTextFile();
					rating = 0;
					reset = LOST_STATE;
					hs_State = hs_Writing;
					enterPressed = false;
				}
			}
			
		}*/
			public void writeTimeInTextFile(){
				try {
					PrintWriter timeFileWriter = new PrintWriter(new BufferedWriter(new FileWriter("TimeTrialHighScore.txt", true)));
					Double time = (double)(t/6)/10;
					String text = time.toString();
					timeFileWriter.println(playerName.trim() + "||" + text);
					timeFileWriter.close();	
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
					            
			}
			public void writeScoreInTextFile(){
				try {
					PrintWriter scoreFileWriter = new PrintWriter(new BufferedWriter(new FileWriter("SurvivalHighScore.txt", true)));
					scoreFileWriter.println(playerName.trim() + "||" + score);
					scoreFileWriter.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
						 
			}
			public void writeRatingInTextFile(){
				try {
					PrintWriter scoreFileWriter = new PrintWriter(new BufferedWriter(new FileWriter("RatingsAndPlays.txt", true)));
					if (rating>5){
						rating = 5;
					}
					scoreFileWriter.println(rating);
					scoreFileWriter.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}					
			}
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// draw things to the screen
	public void draw() {
		//graphics.translate(0, t);
		if (GAME_STATE == GAME_MENU){
			drawMenu();
		}
		if (GAME_STATE == GAME_INSTRUCTION){
			drawInstructions();
		}
		if (GAME_STATE == GAME_VIEW_HIGHSCORE){
			drawHighscoreScreen();
		}
		if (GAME_STATE == GAME_PLAY){
			drawGamePlay();
		}
		if (GAME_STATE == GAME_PAUSE){
			drawPauseBar(true);
		}
		if (GAME_STATE == GAME_WRITE_HIGHSCORE){
			//drawWriteHighscore();
		}
	}
	
		
		public void drawMenu(){
//			BufferedImage background = null;
//			try {
//				background = ImageIO.read(new File("testpic.jpg"));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			playButton.draw(graphics);
			helpButton.draw(graphics);
			quitButton.draw(graphics);
		}




		
		public void drawInstructions(){
			drawBackground(Color.red);
		}
	
		public void drawHighscoreScreen(){
		
		}
			public void drawTimeHighscore(Vector<Score> v, int x){
				for(int i = 0; i<v.size(); i++){
					writeText(Color.cyan, 30, v.elementAt(i).toString(), x, (40*i)+100);
				}
			}
			public void drawScoreHighscore(Vector<Score> v, int x){
				for(int i = v.size()-1; i>0; i--){
					writeText(Color.cyan, 30, v.elementAt(i).toString(), x, (40*((v.size()-i)-1))+100);
				}
			}
			
		public void drawGamePlay(){	
			drawBackground(Color.gray);
			
			if (m.holding){
				graphics.setColor(Color.black);
				graphics.drawLine(m.x1, m.y1, mm.x, mm.y);
			}
			drawWalls();
			//System.out.println(mm.x + ", " + mm.y);
		}
			public void drawWalls(){
				
				for(int i = 0; i<walls.size(); i++){
				//	System.out.println(i);
					graphics.setColor(Color.cyan);
				//	System.out.println(i);
					walls.get(i).draw(graphics);
				}
			}
		
			public void drawBackground(Color c) {
				graphics.setColor(c);
				graphics.fillRect(0, 0, WIDTH, HEIGHT);
			}
			public void drawScore(){
				String text = score + "";
				writeText(Color.orange, 40, text, 0, 30);
			}
			public void drawTime(){
				Double time = (double)(t/6)/10;
				String text = time.toString();
				writeText(Color.orange, 40, text, 400, 30);
			}
			public void drawPlayer() {

			}
			
		public void drawPauseBar(boolean one){
			int pausedBarX = (WIDTH/2)+100;
			int y0 = 60, y1 = 120, y2 = 180, y3 = 240, y4 = 300;
			int textSize = 40;
			int add = 10;
			
			if (one){
				add = 0;
				writeText(Color.orange, textSize+20, "GAME PAUSED", WIDTH/2, y0);
			}
			writeText(Color.yellow, 10, "Use the Arrow Keys to Choose Where to Go : Enter to select", pausedBarX, 80);
			if (pausedBar_State == pausedBar_Menu){
				writeText(Color.red, textSize+add, "RETURN TO MENU - 0", pausedBarX, y1);
			}
			else {
				writeText(Color.orange, textSize, "RETURN TO MENU - 0", pausedBarX, y1);
			}
			if (pausedBar_State == pausedBar_Instructions){
				writeText(Color.red, textSize+add, "INSTRUCTIONS - 1", pausedBarX, y2);
			}
			else {
				writeText(Color.orange, textSize, "INSTRUCTIONS - 1", pausedBarX, y2);
			}
			if (pausedBar_State == pausedBar_TimeTrial){
				writeText(Color.red, textSize+add, "PLAY TIMETRIAL MODE - 3", pausedBarX, y3);
			}
			else {
				writeText(Color.orange, textSize, "PLAY TIMETRIAL MODE - 3", pausedBarX, y3);
			}
			if (pausedBar_State == pausedBar_Survival){
				writeText(Color.red, textSize+add, "PLAY SURVIVAL MODE - 4", pausedBarX, y4);
			}
			else {
				writeText(Color.orange, textSize, "PLAY SURVIVAL MODE - 4", pausedBarX, y4);
			}
		}
		public void writeText(Color c, int size, String text, int x, int y){
			graphics.setColor(c);
			graphics.setFont(new Font("Arial", Font.PLAIN, size));
			graphics.drawString(text, x, y);
		}
		
		/*public void drawWriteHighscore(){	 
			drawBackground();
			drawScore();
			drawTime();
			if (hs_State == hs_Writing){
				writeText(Color.red, 50, "Please Type Your Name(Max 8 Characters):", 100, 100);
				writeText(Color.CYAN, 40, "Please Rate This Game Out of 5(Type the Number)", 100, 300);
			}
			else if (hs_State == hs_Rating){
				writeText(Color.CYAN, 50, "Please Type Your Name(Max 8 Characters):", 100, 100);
				writeText(Color.red, 40, "Please Rate This Game Out of 5(Type the Number)", 100, 300);
			}
			
			writeText(Color.CYAN, 40, playerName, 100, 200);
		
			String r = rating + "";
			writeText(Color.CYAN, 40, r, 100, 400);
		}*/
	// ---------------------------------------------------------------------------------------
	// main game loop
	public void run() {
		init();
		long startTime = System.nanoTime();
		double ns = 1000000000.0 / UPS;
		double delta = 0;
		int frames = 0;
		int updates = 0;

		long secondTimer = System.nanoTime();
		while (running) {
			long now = System.nanoTime();
			delta += (now - startTime) / ns;
			startTime = now;
			while (delta >= 1) {
				update();
				delta--;
				updates++;
			}
			render();
			frames++;

			if (System.nanoTime() - secondTimer > 1000000000) {
				this.frame.setTitle(updates + " ups  ||  " + frames + " fps");
				secondTimer += 1000000000;
				frames = 0;
				updates = 0;
			}
		}
		stop();
		
	}

	// ---------------------------------------------------------------------------------------
	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.add(game); // game is a component because it extends Canvas
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		game.start();
	}

	public Game() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		frame = new JFrame();
		this.requestFocus();
		this.setBackground(Color.black);
		this.addKeyListener(k);
		this.addMouseListener(m);
		this.addMouseMotionListener(mm);
	}

	// starts a new thread for the game
	public synchronized void start() {
		thread = new Thread(this, "Game");
		running = true;
		thread.start();
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy(); // method from Canvas class

		if (bs == null) {
			createBufferStrategy(3); // creates it only for the first time the
										// loop runs (trip buff)
			return;
		}

		graphics = (Graphics2D) bs.getDrawGraphics();
		draw();
		graphics.dispose();
		bs.show();
	}

	// stops the game thread and quits
	public synchronized void stop() {
		System.exit(0);
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
//:3