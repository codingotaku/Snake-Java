package com.codingotaku.app.snake;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Snake {
	ArrayList<Rectangle> cells = new ArrayList<>();
	private int xOffset, yOffset;
	private Rectangle playArea;

	Snake(Rectangle initPos, Rectangle playArea) {
		// Head of the snake
		cells.add(initPos);
		this.playArea = playArea;
	}

	void move(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	boolean isGameOver() {
		Rectangle head = cells.get(0);
		if (!playArea.intersects(head.getBoundsInLocal())) {
			return true;
		}
		for (int i = 1; i < cells.size(); i++) {
			if (head.intersects(cells.get(i).getBoundsInLocal())) {
				return true;
			}
		}
		return false;
	}

	boolean eat(Rectangle food) {
		Rectangle head = cells.get(0);
		if(!head.intersects(food.getBoundsInLocal())) {
			return false;
		}
		cells.add(new Rectangle(head.getX(), head.getY(),head.getWidth(),head.getHeight()));
		return true;
	}

	void update() {
		if (xOffset == 0 && yOffset == 0) {
			return;
		}
		//verify offsets
		for (int i = cells.size() - 1; i > 0; i--) {
			Rectangle prev = cells.get(i);
			Rectangle curr = cells.get(i - 1);
			prev.setX(curr.getX());
			prev.setY(curr.getY());
		}
		Rectangle head = cells.get(0);
		double newX =head.getX()+xOffset * head.getWidth();
		double newY =head.getY()+yOffset * head.getHeight();
		head.setX(newX);
		head.setY(newY);
		//save the offsets
	}

	void render(GraphicsContext gc) {
		gc.setFill(Color.RED);
		cells.forEach(cell -> {
			gc.fillRect(cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight());
		});
	}
}
