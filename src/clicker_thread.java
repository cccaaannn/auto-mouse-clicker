

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class clicker_thread extends Thread {


	ArrayList<ArrayList<Integer>> mouse_coordinates = new ArrayList<ArrayList<Integer>>();
	Robot r;
	int delay = 0;
	int delay_between_loops = 0;
	boolean loop_clicks = false;


	public clicker_thread(ArrayList<ArrayList<Integer>> mouse_coordinates,int delay,int delay_between_loops,boolean loop_clicks){
		
		/*
		*parameters
		*mouse_coordinates doubly integer array
		*delay  delay between clicks
		*delay_between_loops  delay between lick loop cycle
		*loop_clicks  loop decision
		*/
		
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		this.delay = delay;
		
		this.delay_between_loops = delay_between_loops;
		
		this.loop_clicks = loop_clicks; 
			
		this.mouse_coordinates.add(new ArrayList<Integer>());
		this.mouse_coordinates.add(new ArrayList<Integer>());

		this.mouse_coordinates.get(0).addAll(mouse_coordinates.get(0));
		this.mouse_coordinates.get(1).addAll(mouse_coordinates.get(1));


	}




	public void run(){

		do {
		for (int i = 0; i < mouse_coordinates.get(0).size(); i++) {

			//r.mouseMove(mouse_coordinates.get(0).get(i), mouse_coordinates.get(1).get(i));    
			moveMouse(mouse_coordinates.get(0).get(i), mouse_coordinates.get(1).get(i),3,r);

			int mask = InputEvent.BUTTON1_DOWN_MASK;
			r.mousePress(mask);
			r.mouseRelease(mask);
			
			
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				continue;
			}

			
		}
		
		try {
			Thread.sleep(delay_between_loops);
		} catch (InterruptedException e) {
			continue;
		}
		
		
		}while(loop_clicks);
		
		
	}


	//if mouse is not on proper position move it again
	public static void moveMouse(int x, int y, int maxTimes, Robot screenWin) {
		for(int count = 0;(MouseInfo.getPointerInfo().getLocation().getX() != x || 
				MouseInfo.getPointerInfo().getLocation().getY() != y) &&
				count < maxTimes; count++) {
			screenWin.mouseMove(x, y);
		}
	}


}
