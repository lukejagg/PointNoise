package pointNoise;

/// <Summary>
///	Generates a Worley Noise image.
/// Left click to increase the closest thing (nth closest ++)
/// Right click to increase number of points.
/// Shift + click to decrement them.
/// </Summary>

import java.util.ArrayList;
import java.util.Comparator;
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

public class WorleyNoise extends Application
{

	static Position check;

	Comparator<Double> sortByDistance = new Comparator<Double>()
	{

		@Override
		public int compare(Double arg0, Double arg1) 
		{
			
			return (int) ((arg0 - arg1)*64);
			
		}
		
	};
	
	class Position
	{
		
		private double[] p;
		
		Position (double[] p)
		{
			
			this.p = p;
			
		}
		
		double Distance(double[] p) 
		{
				
			double sum = 0;
			for (int i = 0; i < p.length; i++) {
				sum += (this.p[i]-p[i])*(this.p[i]-p[i]);
			}
			return sum; //Math.sqrt(sum);
			
		}
		
	}
	
	public static void main (String[] args) 
	{
		
		launch(args);
		
	}
	
	static final int width = 800;
	static final int height = 800;
	static final double zRange = 0;
	static final double zPos = 0;
	static final double wRange = 0;
	static final double wPos = 0;
	static final double aRange = 0;
	static final double aPos = 0;
	static final double bRange = 0;
	static final double bPos = 0;
	static final double cRange = 0;
	static final double cPos = 0;

	boolean heatmap = false;
	boolean darkenmap = false;
	
	static int pointCount = 1;
	static int nthClosest = 0;
	
	boolean generating = false;
	
	@Override
	public void start(Stage stage) 
	{
			
		final Group root = new Group();
		final Scene s = new Scene(root, width, height, Color.BLACK);
	    final Canvas canvas = new Canvas(width, height);
	    final GraphicsContext gc = canvas.getGraphicsContext2D();
	    root.getChildren().add(canvas);
	    stage.initStyle(StageStyle.UNDECORATED);
	    stage.setTitle("DRAW!");
	    stage.setScene(s);
	    stage.setResizable(false);
	    stage.show();
	    canvas.setFocusTraversable(true);
	    
	    Long n = System.nanoTime();
	    canvas.setOnKeyPressed(e -> 
	    {
	    	if (generating)
	    		return;
	    	
	    	generating = true;
		    Random rnd = new Random(n);
		    
		    Position[] points = new Position[pointCount];
			ArrayList<Double> distances = new ArrayList<Double>();
			
			for (int i = 0; i < pointCount; i++) 
			{
				//FIXME ADD MORE DIM'S
				points[i] = new Position(new double[] { rnd.nextDouble() * width, rnd.nextDouble() * height, rnd.nextDouble() * zRange, rnd.nextDouble() * wRange, rnd.nextDouble() * aRange, rnd.nextDouble() * bRange, rnd.nextDouble() * cRange});
			}
			
			for (int i = 0; i < pointCount; i++) {
				distances.add(0D);
			}
			
			for (int i = 0; i < pointCount; i++) 
			{
				//FIXME ADD MORE DIM'S
				distances.set(i, points[i].Distance(new double[] {0, 0, 0, 0, 0, 0, 0}));
			}
		    distances.sort(sortByDistance);
			
		    double _d = distances.get(nthClosest);
		    double _m = _d;
		    long t0 = System.nanoTime();
		    
		    for (int x = 0; x < width; x++)
		    {
		    	for (int y = 0; y < height; y++) 
		    	{
				    for (int i = 0; i < pointCount; i++) 
					{
				    	//FIXME ADD MORE DIM'S
						distances.set(i, points[i].Distance(new double[] {x, y, zPos, wPos, aPos, bPos, cPos}));
					}
				    distances.sort(sortByDistance);
				    if (distances.get(nthClosest) > _d)
				    	_d = distances.get(nthClosest);
				    if (distances.get(nthClosest) < _m)
				    	_m = distances.get(nthClosest);
		    	}
		    }
		    
		    System.out.println("Took " + (System.nanoTime() - t0)/1000000000.00 + " seconds to calculate max distance.");
		    
		    final double minDistance = _m;
		    final double minFactor = (heatmap ? 360 : 255) / (_d - _m);
		    
		    AnimationTimer t = new AnimationTimer() {
		    
		    	int x = 0;
		    	int y = 0;
		    	
		    	@Override
		    	public void handle(long t) {
		    	
		    		for (int lol = 0; lol < 50000; lol++) {
			    		for (int i = 0; i < pointCount; i++) 
			    		{
			    			//FIXME ADD MORE DIM'S
			    			distances.set(i, points[i].Distance(new double[] {x, y, zPos, wPos, aPos, bPos, cPos}));
			    		}
			    		distances.sort(sortByDistance);
			    		if (heatmap) {
				    		double color = ((distances.get(nthClosest).doubleValue() - minDistance)*minFactor);
				    		gc.setFill(Color.hsb(color,1,darkenmap ? Math.pow(1-color/360, 2) : 1));
			    		}
			    		else {
			    			int color = (int) ((distances.get(nthClosest).doubleValue() - minDistance)*minFactor);
				    		gc.setFill(Color.rgb(color,color,color));
			    		}
			    		gc.fillRect(x, y, 1, 1);
			    		
			    		if (++x >= width) {
			    			x = 0;
			    			if (++y >= height)
			    			{
			    				generating = false;
			    				this.stop();
			    				return;
			    			}
			    		}
		    		}
			    
		    	}
		    
		    };
		    
		    t.start();
	    
	    });
	    	
	    canvas.setOnMousePressed(e -> 
	    {
	    	if (generating)
	    		return;
	    	
	    	if (e.isControlDown()) {
	    		
	    	}
	    	else if (e.isShiftDown()) {
	    		if (e.isPrimaryButtonDown()) {
		    		nthClosest--;
		    		if (nthClosest < 0)
		    			nthClosest = 0;
		    	}
		    	else if (e.isSecondaryButtonDown())
		    		pointCount--;
	    	}
	    	else {
		    	if (e.isPrimaryButtonDown()) {
		    		nthClosest++;
		    		if (nthClosest == pointCount)
		    			nthClosest--;
		    	}
		    	else if (e.isSecondaryButtonDown())
		    		pointCount++;
	    	}
	    });
	    
	}

}
