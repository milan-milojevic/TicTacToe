package com.conichi.tictactoe.entity;

public class Point {

    //the point object contains the x, y, and score values
    int x, y, score;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point, int score) {
        this.x = point.x;
        this.y = point.y;
        this.score = score;
    }

    public boolean equals(Point point) {
        if (this.x == point.x && this.y == point.y) {
            return true;
        }
        else {
            return false;
        }
    }
}
