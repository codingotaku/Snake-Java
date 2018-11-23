package com.codingotaku.app.snake;

import java.util.Random;
import java.util.prefs.Preferences;

import com.sun.javafx.perf.PerformanceTracker;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {
	private final int gameW = 460;
	private final int gameH = 410;
	private final int gameX = 20;
	private final int gameY = 20;
	private final int size = 10;

	private GraphicsContext gc;
	private PerformanceTracker tracker;
	private Rectangle playArea;
	private Snake snake;
	private Random random;
	private Rectangle food;

	private boolean isGameOver;

	private int score = 0;
	private int highScore = 0;

	@Override
	public void start(Stage stage) {
		Canvas canvas = new Canvas(500, 500);
		Pane root = new Pane(canvas);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		stage.setTitle("Snake");

		Preferences preferences = Preferences.userNodeForPackage(Main.class);
		highScore = Integer.valueOf(preferences.get("highscore", "0"));

		gc = canvas.getGraphicsContext2D();
		random = new Random();
		tracker = PerformanceTracker.getSceneTracker(scene);
		playArea = new Rectangle(gameX, gameY, gameW, gameH);
		Rectangle initPos = new Rectangle(size, size, size, size);
		snake = new Snake(initPos, playArea);
		food = new Rectangle();
		newFood();

		root.requestFocus();
		root.setOnKeyPressed(event -> {
			switch (event.getCode()) {
			case LEFT:
				snake.move(-1, 0);
				break;
			case RIGHT:
				snake.move(1, 0);
				break;
			case UP:
				snake.move(0, -1);
				break;
			case DOWN:
				snake.move(0, 1);
				break;
			case ESCAPE:
				snake.move(0, 0);
				break;
			case ENTER:
				restart();
				break;
			default:
				break;
			}
		});

		timer.start();
	}

	private void restart() {
		if (isGameOver) {
			score = 0;
			Rectangle initPos = new Rectangle(size, size, size, size);
			snake = new Snake(initPos, playArea);
			newFood();
			gameFrame();
			Preferences preferences = Preferences.userNodeForPackage(Main.class);
			preferences.put("highscore", String.valueOf(highScore));
			timer.start();
		}
	}

	private void newFood() {
		food.setX((random.nextInt((int) playArea.getWidth()/size) + 1) * size);
		food.setY((random.nextInt((int) playArea.getWidth()/size) + 1) * size);
		System.out.println(food.getX());
		System.out.println(food.getY());
		food.setWidth(size);
		food.setHeight(size);
	}

	private float getFPS() {
		float fps = tracker.getAverageFPS();
		tracker.resetAverageFPS();
		return fps;
	}

	AnimationTimer timer = new AnimationTimer() {

		private int tic = 100_000_000;
		private long prev = System.nanoTime();

		@Override
		public void handle(long now) {
			if (now - prev > tic) {
				snake.update();
				if (snake.isGameOver()) {
					isGameOver = true;
					gc.strokeText("Game Over!!!", 200, 200);
					timer.stop();
					return;
				}
				if (snake.eat(food)) {
					newFood();
					score++;
				}
				highScore = highScore < score ? score : highScore;
				prev = System.nanoTime();
			}
			gameFrame();
			snake.render(gc);
			gc.setFill(Color.GREEN);
			gc.fillOval(food.getX(), food.getY(), size, size);
		}
	};

	private void gameFrame() {
		gc.setFill(Color.grayRgb(32));
		gc.fillRect(0, 0, 500, 500);
		gc.setStroke(Color.WHITESMOKE);
		gc.strokeRect(gameX - size, gameY - size, gameW + size * 2, gameH + size * 2);
		gc.strokeText(String.format("Score : %d", score), 10, 470);
		gc.strokeText(String.format("High Score : %d", highScore), 200, 470);
		gc.strokeText(String.format("FPS : %.0f", getFPS()), 420, 470);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
