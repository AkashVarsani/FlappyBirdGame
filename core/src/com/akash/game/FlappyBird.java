package com.akash.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background, bird[];
    Texture bt, tt, gameOver, swipe;
    int flapeState = 0, temp = 0;
    int renderLag = 5;
    float birdY = 0;
    float velocity = 0, gravity = 3;
    int gameState = 0;
    int gap = 500;
    int score = 0, tubeNo = 0;
    float maxchange, change[];
    Random random;
    float tubeVelocity = 4;
    int noOfTubes = 3;
    float tubeDistance;
    float tubeX[];
	BitmapFont font;
    Circle birdCircle;
    Rectangle[] topTubeRectangles, bottomTubeRectangles;
    float t1,t2;
    int gameMode=0, pointTouched=200;


	public void init(){
        tubeDistance = Gdx.graphics.getWidth() / (1.5f);
		birdY = Gdx.graphics.getHeight() / 2 - bird[flapeState].getHeight() / 2;
		for (int i = 0; i < noOfTubes; i++) {
			tubeX[i] = Gdx.graphics.getWidth() + tt.getWidth() / 2 + i * tubeDistance;
		}
		flapeState=0;temp=0;
		velocity=0;gameState=0;
		score=0;tubeNo=0;
        pointTouched=200;

    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        background = new Texture("bg.png");
        bird = new Texture[2];
        bird[0] = new Texture("bird.png");
        bird[1] = new Texture("bird2.png");
        birdY = Gdx.graphics.getHeight() / 2 - bird[flapeState].getHeight() / 2;
        tt = new Texture("toptube.png");
        bt = new Texture("bottomtube.png");
		gameOver = new Texture("gameover.png");
        swipe = new Texture("swipe.png");

        tubeDistance = Gdx.graphics.getWidth() / (1.5f);
        maxchange = Gdx.graphics.getHeight() / 2 - gap / 2 - 200;
        random = new Random();

        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[noOfTubes];
        bottomTubeRectangles = new Rectangle[noOfTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(5);

        change = new float[noOfTubes];
        tubeX = new float[noOfTubes];

        for (int i = 0; i < noOfTubes; i++) {
            tubeX[i] = Gdx.graphics.getWidth() + tt.getWidth() / 2 + i * tubeDistance;
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }

    }

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState==0){
            font.draw(batch,"Manual Flapping", Gdx.graphics.getWidth()/2-("Mannual Flapping").length()*16,Gdx.graphics.getHeight()/2+300);
            font.draw(batch,"Gyro Flapping", Gdx.graphics.getWidth()/2-("Gyro Flapping").length()*17,Gdx.graphics.getHeight()/2-240);
            font.getData().setScale(3);
            font.draw(batch,"Select any One above Option !", 20, Gdx.graphics.getHeight()/4-swipe.getHeight()/2-10);
            font.getData().setScale(5);
        }

        if(gameState==2) {
            if (Gdx.input.isTouched()) {
                t1 = Gdx.input.getX();
                if((t1>pointTouched)&&(t1<pointTouched+120))pointTouched+=120;
                if(pointTouched>600)init();
            }
        }

        if (Gdx.input.justTouched()) {

            if(gameState==0){
                t2=Gdx.graphics.getHeight() - Gdx.input.getY();
                if(t2>Gdx.graphics.getHeight()/2){gameMode=1;gravity=3;}
                else {gameMode=2; gravity=0.45f;tubeDistance+=50;
                for (int i=1; i<noOfTubes;i++){
                tubeX[i]+=i*50;
                }
                }
                gameState=1;
                if(gameMode==1)velocity=-10;
                else if(gameMode==2)velocity=-5;
            }
            else if(gameMode==1)velocity = -10;

        }

        if(gameState==1&&gameMode==2){
            t2=Gdx.input.getAccelerometerY();
            if(t2<-0.5f){
                velocity=-4.0f;
            }
            else if(t2>5.0f){
                velocity+=0.35f;
            }

        }

        if (gameState == 1) {


            for (int i = 0; i < noOfTubes; i++) {

                tubeX[i] -= tubeVelocity;
                if (tubeX[i] < -(bt.getWidth())) {
                    tubeX[i] += noOfTubes * tubeDistance;
                    change[i] = (random.nextFloat() - 0.5f) * 2 * maxchange;
                }
                batch.draw(tt, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + change[i]);
                batch.draw(bt, tubeX[i], Gdx.graphics.getHeight() / 2 - bt.getHeight() - gap / 2 + change[i]);
            }

            if (tubeX[tubeNo] < (Gdx.graphics.getWidth() / 2 - tt.getWidth())) {
                score++;
                tubeNo++;
                if (tubeNo == noOfTubes) tubeNo = 0;
            }

            velocity += 0.25 * gravity;
            birdY -= velocity;

            if (birdY < 0) {birdY = 0;gameState=2;}
            if (birdY > (Gdx.graphics.getHeight() - bird[flapeState].getHeight()))
                birdY = Gdx.graphics.getHeight() - bird[flapeState].getHeight();

            birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + bird[flapeState].getHeight() / 2, bird[flapeState].getWidth() / 2);
            for (int i = 0; i < noOfTubes; i++) {
                topTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + change[i], tt.getWidth(), tt.getHeight());
                bottomTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - bt.getHeight() - gap / 2 + change[i], bt.getWidth(), bt.getHeight());
            }

            for (int i = 0; i < noOfTubes; i++) {

                if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                	gameState=2;
				}

            }

        }


        if (temp == renderLag * 2) temp = 0;
        if (temp < renderLag) flapeState = 0;
        else flapeState = 1;
        temp++;
		batch.draw(bird[flapeState], Gdx.graphics.getWidth() / 2 - bird[flapeState].getWidth() / 2, birdY);
		if(gameState==2){
			for (int i = 0; i < noOfTubes; i++) {
				batch.draw(tt, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + change[i]);
				batch.draw(bt, tubeX[i], Gdx.graphics.getHeight() / 2 - bt.getHeight() - gap / 2 + change[i]);
			}
			batch.draw(gameOver, Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
            batch.draw(swipe,20,Gdx.graphics.getHeight()/4-swipe.getHeight()/2);
            font.getData().setScale(3);
            font.draw(batch,"Swipe Right to Start Again !", 20, Gdx.graphics.getHeight()/4-swipe.getHeight()/2-10);
		    font.getData().setScale(5);
        }

		font.draw(batch,String.valueOf(score),50,150);
		batch.end();


    }

}
