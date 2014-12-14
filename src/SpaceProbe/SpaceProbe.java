package SpaceProbe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SpaceProbe {
	/**
	 * NASA has contracted you to program the AI of a new probe. This new
	 * probe must navigate space from a starting location to an end location.
	 * The probe will have to deal with Asteroids and Gravity Wells. Hopefully
	 * it can find the shortest path.
	 * 
	 * @assume start & end location
	 * 			That there cannot be a asteroid or gravity well within the area
	 * 
	 * @output
	 * Must show the final Map and shortest safe route on the map.
	 * 		. = empty space
	 * 		S = start location
	 * 		E = end location
	 * 		G = gravity well
	 * 		A = Asteroid
	 * 		O = Path.
	 * If you fail to get to the end because of no valid path you must travel
	 * as far as you can and show the path. Note that the probe path was 
	 * terminated early due to "No Complete Path" error.
	 * 
	 * @param n
	 * 			size of grid, nxn
	 * @param x1
	 * 			starting x coordinate
	 * @param y1
	 * 			starting y coordinate
	 * @param x2
	 * 			ending x coordinate
	 * @param y2
	 * 			ending y coordinate
	 * 
	 * @input SpaceProbe n x1 y1 x2 y2
	 */
	public static void main(String[] args){
		
		//Error checking
		if(args.length != 5){
			System.out.println("Invalid number of arguments should be: SpaceProbe n x1 y1 x2 y2");
			return;
		}
		for(int i=0; i<5; i++){
			try { 
		        Integer.parseInt(args[i]); 
		    } catch(NumberFormatException e) { 
		    	System.out.println("Given arg isnt an integer");
		        return; 
		    }
		}
		
		//Initialize the grid & variables
		int n = Integer.parseInt(args[0]);
		int x1 = Integer.parseInt(args[1]);
		int y1 = Integer.parseInt(args[2]);
		int x2 = Integer.parseInt(args[3]);
		int y2 = Integer.parseInt(args[4]);
		char[][] grid = new char[n][n];
		int[] finalNode = new int[5];
		
		//Check given coordinates
		if(x1 >= n || y1 >= n){
			System.out.println("Initial coordinates are greater than grid");
		    return; 
		}
		if(x2 >= n || y2 >= n){
			System.out.println("Final coordinates are greater than grid");
		    return; 
		}
		
		//Initialize the grid
		//Create a randomized grid containing Asteroids 'A' and gravity wells 'G'
		int validGrid = 0;
		while(validGrid == 0){
			validGrid = 1;
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					Random rand = new Random();
					int num = rand.nextInt(100);
					if(num < 15){
						grid[i][j] = 'A';
					} else if(num < 22){
						grid[i][j] = 'G';
					} else {
						grid[i][j] = '.';
					}
				}
			}
			//Check surrounding for gravity wells
			//Set start and end points		NOTE: Sometimes fails
			grid[y1][x1] = 'S';
			if(checkPos(x1, y1, n, grid)!=1){
				validGrid = 0;
			}
			grid[y2][x2] = 'E';
			if(checkPos(x2, y2, n, grid)!=1){
				validGrid = 0;
			}
		}
		
		// Determine the shortest path to exit
		//Hold a list in the format <[x, y, F, Xp, Yp], [...], ...>
		List<int[]> openList = new ArrayList<int[]>();
		//Hold a list in the format <[x, y, F, Xp, Yp], [...], ...>
		List<int[]> closedList = new ArrayList<int[]>();		
		
		//add adjacent nodes to starting position
		addAdjacentNodes(x1, y1, x2, y2, x1, y1, 0, n, openList);
		int[] initialNode = {x1, y1, 0, x1, y1};
		closedList.add(initialNode);
		for(int i=0; i<openList.size(); i++){
			if(checkPos(openList.get(i)[0], openList.get(i)[1], n, grid) == 0 || grid[openList.get(i)[1]][openList.get(i)[0]] == 'A'){
				openList.remove(i);
			}
		}
		int foundEnd = 0;
		while(!openList.isEmpty() && foundEnd == 0){	//switch to while loop
			int highest = 0;
			int nodeNo = 0;
			for(int i=0; i<openList.size(); i++){
				//check for the lowest value
				if(openList.get(i)[2] < highest){
					highest = openList.get(i)[2];
					nodeNo = i;
				}
			}
			if(openList.get(nodeNo)[0] == x2 && openList.get(nodeNo)[1] == y2){
				finalNode = openList.get(nodeNo);
				foundEnd = 1;
			}
			if(foundEnd != 1){
				//Add new nodes
				addAdjacentNodes(openList.get(nodeNo)[0], openList.get(nodeNo)[1], x2, y2, openList.get(nodeNo)[0], openList.get(nodeNo)[1], openList.get(nodeNo)[2], n, openList);
			
				//Remove the node from openList and add to closedList
				closedList.add(openList.get(nodeNo));
				openList.remove(nodeNo);
				
				//Remove invalid nodes
				while(checkIfInvalidLocation(openList, n, grid) == 1);
				while(checkIfVisited(openList, closedList) == 1);
			}
		}
		
		if(foundEnd == 1){
			int xp = finalNode[3];
			int yp = finalNode[4];
			grid[yp][xp] = 'O';
			int atStart = 0;
			while(atStart == 0){		//Gets stuck in here
				for(int i=0; i<closedList.size(); i++){
					if(closedList.get(i)[0] == xp && closedList.get(i)[1] == yp){
						xp = closedList.get(i)[3];
						yp = closedList.get(i)[4];
						if(xp == x1 && yp == y1){
							atStart = 1;
						}
						grid[yp][xp] = 'O';
					}
				}
			}
			grid[y1][x1] = 'S';	
			
			//Create a display
			JFrame frame = new JFrame();
			frame.setTitle("Path was Found");
			frame.setLocation(100, 100);
			JPanel panel = new JPanel();
			JButton[][] displayGrid = new JButton[n][n];
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					//Edit the status & color of each node 
					JButton button = new JButton();
					button.setEnabled(false);
					if(grid[j][i] == 'A'){
						button.setBackground(new Color(0x990000));
					} else if(grid[j][i] == 'O'){
						button.setBackground(new Color(0x33CC33));
					} else if(grid[j][i] == 'S' || grid[j][i] == 'E'){
						button.setBackground(new Color(0x5C5CFF));
					} else if(grid[j][i] == 'G'){
						button.setBackground(new Color(0x4D4D4D));
					} else if(checkPos(i, j, n, grid) == 0){
						button.setBackground(new Color(0xA1A1A1));
					}else {
						button.setBackground(new Color(0xE6E6E6));
					}
					displayGrid[j][i] = button;
					panel.add(displayGrid[j][i]);
				}
			}
			panel.setLayout(new GridLayout(n, n));
			frame.add(panel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setSize(new Dimension(750, 750));
			frame.setVisible(true);
		} else {
			System.out.println("No path could be found");
		}	
	}
	
	/**
	 * Checks if the values in the list aren't an Asteroid or centre of a Gravity Well. If either is found the function return a 1, else returns a 0
	 * 
	 * @param openList
	 * 			The list to search through
	 * @param n
	 * 			The size of the grid
	 * @param grid
	 * 			The 2D grid containing asteroids and gravity wells 
	 * @return
	 */
	private static int checkIfInvalidLocation(List<int[]> openList, int n, char[][] grid){
		for(int i=0; i<openList.size(); i++){
			if(checkPos(openList.get(i)[0], openList.get(i)[1], n, grid) == 0 || grid[openList.get(i)[1]][openList.get(i)[0]] == 'A' || grid[openList.get(i)[1]][openList.get(i)[0]] == 'G'){
				openList.remove(i);
				return 1;
			}
		}
		return 0;
	}
	
	
	/**
	 * Checks the openList to determine if it is within closedList, and has therefore been visited. If found the function returns a 1, else returns 0.
	 * 
	 * @param openList
	 * 			List containing nodes to visit
	 * @param closedList
	 * 			List containing nodes which have been visited
	 */
	private static int checkIfVisited(List<int[]> openList, List<int[]> closedList){
		/*if(!openList.isEmpty() || !closedList.isEmpty()){
			return;
		}*/
		for(int i=0; i<openList.size(); i++){
			for(int j=0; j<closedList.size(); j++){
				if(openList.get(i)[0] == closedList.get(j)[0]){
					if(openList.get(i)[1] == closedList.get(j)[1]){
						openList.remove(i);
						return 1;
					}
				}
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @return
	 */
	private static int getHValue(int x1, int x2, int y1, int y2){
		int result = x2-x1 + y2-y1;
		return result;
	}
	
	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param n
	 * @param openList
	 */
	private static void addAdjacentNodes(int x1, int y1, int x2, int y2, int xp, int yp, int f, int n, List<int[]> openList){
		if(x1 == 0 && y1 == 0){
			addRight(x1, y1, x2, y2, xp, yp, f, openList);
			addSE(x1, y1, x2, y2, xp, yp, f, openList);
			addDown(x1, y1, x2, y2, xp, yp, f, openList);
		}else if(x1 == n-1 && y1 == n-1){
			addLeft(x1, y1, x2, y2, xp, yp, f, openList);
			addNW(x1, y1, x2, y2, xp, yp, f, openList);
			addUp(x1, y1, x2, y2, xp, yp, f, openList);
			
		}else if(x1 == 0){
			if(y1 == n-1){
				addRight(x1, y1, x2, y2, xp, yp, f, openList);
				addNE(x1, y1, x2, y2, xp, yp, f, openList);
				addUp(x1, y1, x2, y2, xp, yp, f, openList);
				
			}else{
				addNE(x1, y1, x2, y2, xp, yp, f, openList);
				addRight(x1, y1, x2, y2, xp, yp, f, openList);
				addSE(x1, y1, x2, y2, xp, yp, f, openList);
				addDown(x1, y1, x2, y2, xp, yp, f, openList);
				addUp(x1, y1, x2, y2, xp, yp, f, openList);
			}
		}else if(y1 == 0){
			if(x1 == n-1){
				addDown(x1, y1, x2, y2, xp, yp, f, openList);
				addSW(x1, y1, x2, y2, xp, yp, f, openList);
				addLeft(x1, y1, x2, y2, xp, yp, f, openList);
			}else{
				addRight(x1, y1, x2, y2, xp, yp, f, openList);
				addSE(x1, y1, x2, y2, xp, yp, f, openList);
				addDown(x1, y1, x2, y2, xp, yp, f, openList);
				addSW(x1, y1, x2, y2, xp, yp, f, openList);
				addLeft(x1, y1, x2, y2, xp, yp, f, openList);
			}
		}else if(x1 == n-1){
			if(y1 == 0){
				addDown(x1, y1, x2, y2, xp, yp, f, openList);
				addSW(x1, y1, x2, y2, xp, yp, f, openList);
				addLeft(x1, y1, x2, y2, xp, yp, f, openList);
			}else{
				addDown(x1, y1, x2, y2, xp, yp, f, openList);
				addSW(x1, y1, x2, y2, xp, yp, f, openList);
				addLeft(x1, y1, x2, y2, xp, yp, f, openList);
				addNW(x1, y1, x2, y2, xp, yp, f, openList);
				addUp(x1, y1, x2, y2, xp, yp, f, openList);
			}
		}else if(y1 == n-1){
			if(x1 == 0){
				addRight(x1, y1, x2, y2, xp, yp, f, openList);
				addNE(x1, y1, x2, y2, xp, yp, f, openList);
				addUp(x1, y1, x2, y2, xp, yp, f, openList);
			}else{
				addNE(x1, y1, x2, y2, xp, yp, f, openList);
				addRight(x1, y1, x2, y2, xp, yp, f, openList);
				addLeft(x1, y1, x2, y2, xp, yp, f, openList);
				addNW(x1, y1, x2, y2, xp, yp, f, openList);
				addUp(x1, y1, x2, y2, xp, yp, f, openList);
			}
		}else{
			addNE(x1, y1, x2, y2, xp, yp, f, openList);
			addRight(x1, y1, x2, y2, xp, yp, f, openList);
			addSE(x1, y1, x2, y2, xp, yp, f, openList);
			addDown(x1, y1, x2, y2, xp, yp, f, openList);
			addSW(x1, y1, x2, y2, xp, yp, f, openList);
			addLeft(x1, y1, x2, y2, xp, yp, f, openList);
			addNW(x1, y1, x2, y2, xp, yp, f, openList);
			addUp(x1, y1, x2, y2, xp, yp, f, openList);
		}
	}
	
	/**
	 * Add the nodes above the current Node
	 * 
	 * @param x1
	 * 			current node position
	 * @param y1
	 * 			current node position
	 * @param x2
	 * 			end position
	 * @param y2
	 * 			end position
	 * @param xp
	 * 			current node position
	 * @param yp
	 * 			current node position
	 * @param f
	 * 			the current f value at the current node
	 * @param openList
	 * 			the list which contains visitable nodes
	 */
	private static void addUp(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1, y1-1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1){
				if(openList.get(i)[1] == y1-1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}
	
	private static void addNE(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1+1, y1-1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1+1){
				if(openList.get(i)[1] == y1-1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}
	
	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param xp
	 * @param yp
	 * @param f
	 * @param openList
	 */
	private static void addRight(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1+1, y1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1+1){
				if(openList.get(i)[1] == y1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}
	
	private static void addSE(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1+1, y1+1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1+1){
				if(openList.get(i)[1] == y1+1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}

	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param xp
	 * @param yp
	 * @param f
	 * @param openList
	 */
	private static void addDown(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1, y1+1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1){
				if(openList.get(i)[1] == y1+1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}	
	}
	
	private static void addSW(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1-1, y1+1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1-1){
				if(openList.get(i)[1] == y1+1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}

	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param xp
	 * @param yp
	 * @param f
	 * @param openList
	 */
	private static void addLeft(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1-1, y1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1-1){
				if(openList.get(i)[1] == y1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}	
	}
	
	private static void addNW(int x1, int y1, int x2, int y2, int xp, int yp, int f, List<int[]> openList){
		int h = getHValue(x1, x2, y1, y2);
		int newF = f + 1 + h;
		int[] val = {x1-1, y1-1, newF, xp, yp};
		int contains = 0;
		for(int i=0; i<openList.size(); i++){
			if(openList.get(i)[0] == x1-1){
				if(openList.get(i)[1] == y1-1){
					contains = 1;
				}
			}
		}
		if(contains == 0){
			openList.add(val);
		}
	}
	
	/**
	 * Check if the given position is within the area of a gravity well
	 * 		.....
	 * 		.XXX.
	 * 		.XGX.
	 * 		.XXX.
	 * 		.....
	 * 
	 * Where 'G'=Gravity Well, 'X'=Invalid Location, '.'=Valid Location
	 * 
	 * 
	 * @param x1
	 * 			x coordinate on the given grid
	 * @param y1
	 * 			y coordinate on hte given grid
	 * @param n
	 * 			dimensions of the grid, nxn
	 * @param grid
	 * 			a 2d array containing the space to traverse
	 * @return int
	 * 			return an integer,	1 if location is valid
	 * 								0 if location is invalid
	 */
	private static int checkPos(int y1, int x1, int n, char[][] grid){
		if(x1 == 0){		//Check if along the x=0 axis
			if(y1 == 0){
				if(grid[x1+1][y1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1][y1+1] == 'G'){
					return 0;
				}
			} else if(y1 == n-1){
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1] == 'G'){
					return 0;
				}
			} else{
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1][y1+1] == 'G'){
					return 0;
				}
			}
		} else if(y1==0){		//Check if along the y=0 axis
			if(x1==0){
				if(grid[x1+1][y1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1][y1+1] == 'G'){
					return 0;
				}
			} else if(x1 == n-1){
				if(grid[x1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				}
			} else {
				if(grid[x1+1][y1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				}
			}
		} else if(x1 == n-1){
			if(y1==0){
				if(grid[x1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				}
			}else if(y1 == n-1){
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1-1] == 'G'){
					return 0;
				}
			}else{
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1+1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1-1] == 'G'){
					return 0;
				}
			}
		} else if(y1==n-1){
			if(x1 == 0){
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1-1] == 'G'){

					return 0;
				} else if(grid[x1+1][y1] == 'G'){
					return 0;
				}
			} else if(x1 == n-1){
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1-1] == 'G'){
					return 0;
				}
			} else {
				if(grid[x1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1-1] == 'G'){
					return 0;
				} else if(grid[x1+1][y1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1] == 'G'){
					return 0;
				} else if(grid[x1-1][y1-1] == 'G'){
					return 0;
				}
			}
		} else {
			if(grid[x1][y1-1] == 'G'){
				return 0;
			} else if(grid[x1+1][y1-1] == 'G'){
				return 0;
			} else if(grid[x1+1][y1] == 'G'){
				return 0;
			} else if(grid[x1+1][y1+1] == 'G'){
				return 0;
			} else if(grid[x1][y1+1] == 'G'){
				return 0;
			} else if(grid[x1-1][y1+1] == 'G'){
				return 0;
			} else if(grid[x1-1][y1] == 'G'){
				return 0;
			} else if(grid[x1-1][y1-1] == 'G'){
				return 0;
			}
		}
		return 1;
	}
}
