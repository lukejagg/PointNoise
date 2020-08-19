package pointNoise;

import java.util.LinkedList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
 
enum DISTANCE_TYPE { EUCLID, MANHAT, MINKOVSKI, XOR, AND, OR, ACOS, ASIN };

public class VoronoiDiagram extends Application {
	
	static double p = 2;
	static int[] pointsX;
	static int[] pointsY;
	static Color[] colors;
	static int cells = 50;
	static int size = 850;
	DISTANCE_TYPE distance = DISTANCE_TYPE.EUCLID;
 
	@Override
	public void start(Stage stage) 
	{
		final Group root = new Group();
		final Scene s = new Scene(root, size, size, Color.BLACK);
	    final Canvas canvas = new Canvas(size, size);
	    final GraphicsContext gc = canvas.getGraphicsContext2D();
	    root.getChildren().add(canvas);
	    stage.initStyle(StageStyle.UNDECORATED);
	    stage.setTitle("DRAW!");
	    stage.setScene(s);
	    stage.setResizable(false);
	    stage.show();
	    canvas.setFocusTraversable(true);
		
		Random rand = new Random(10000);
		pointsX = new int[cells];
		pointsY = new int[cells];
		colors = new Color[cells];
		
		for (int i = 0; i < cells; i++) 
		{
			pointsX[i] = rand.nextInt(size);
			pointsY[i] = rand.nextInt(size);
			colors[i] = Color.hsb(rand.nextInt(360), rand.nextDouble()*0.5+0.5, rand.nextDouble()*0.5+0.5);
		}
		
		AnimationTimer t = new AnimationTimer() 
		{
		
			int x = 0;
			int y = 0;
			
			public void handle(long dt) 
			{
				
				for (int lol = 0; lol < 30000; lol++) 
				{
					int selectedCell = 0;
					
					for (int i = 0; i < cells; i++) 
					{
						int x1 = pointsX[i] - x;
						int y1 = pointsY[i] - y;
						int x2 = pointsX[selectedCell] - x;
						int y2 = pointsY[selectedCell] - y; 
						
						double r1 = Math.hypot(x1, y1);
						double r2 = Math.hypot(x2, y2);
						
						switch (distance) 
						{
							case EUCLID:
								if (x2*x2+y2*y2 > x1*x1+y1*y1)
								{
									selectedCell = i;
								}
								break;
								
							case MANHAT:
								if (Math.abs(x2)+Math.abs(y2) > Math.abs(x1)+Math.abs(y1))
								{
									selectedCell = i;
								}
								break;
								
							case MINKOVSKI:
								if (Math.pow(Math.pow(Math.abs(x2), p) + Math.pow(Math.abs(y2), p), (1 / p)) > Math.pow(Math.pow(Math.abs(x1), p) + Math.pow(Math.abs(y1), p), (1 / p))) {
									selectedCell = i;
								}
 								break;
								
							case XOR:
								if ((x2^y2) > (x1^y1))
								{
									selectedCell = i;
								}
								break;
								
							case AND:
								if ((x2&y2) > (x1&y1))
								{
									selectedCell = i;
								}
								break;
								
							case OR:
								if ((x2|y2) > (x1|y1))
								{
									selectedCell = i;
								}
								break;
								
							case ACOS:
								if (Math.acos(x2/r2) > Math.acos(x1/r1))
								{
									selectedCell = i;
								}
								break;
								
							case ASIN:
								if (Math.asin(y2/r2) > Math.asin(y1/r1))
								{
									selectedCell = i;
								}
								break;
						}
					}
					
					gc.setFill(colors[selectedCell]);
					gc.fillRect(x, y, 1, 1);
					
					if (++x > size) {
						x = 0;
						if (++y > size) {
							this.stop();
							return;
						}
					}
				}
			}
			 
		};
		
		t.start();
		
	}
 
	public static void main(String[] args) 
	{
		
		launch(args);
		
	}
}