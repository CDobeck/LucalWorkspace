package calpoly.cpe123.lucal_2;

import java.util.ArrayList;
import java.util.Random;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.primitive.Rectangle;

import android.content.Context;
import android.graphics.Point;
import android.os.Looper;
import android.test.suitebuilder.annotation.Suppress;
import android.widget.Toast;

class Room {
	int cellX, cellY;
	int x, y, width, height;

	public Room(int x, int y, int width, int height, int cellX, int cellY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.cellX = cellX;
		this.cellY = cellY;
	}

	public Room(int x, int y, int width, int height) {
		this(x, y, width, height, -1, -1);
	}

	int getX() {
		return x;
	}

	void setX(int x) {
		this.x = x;
	}

	int getY() {
		return y;
	}

	void setY(int y) {
		this.y = y;
	}

	int getWidth() {
		return width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	int getHeight() {
		return height;
	}

	void setHeight(int height) {
		this.height = height;
	}

	boolean isIn(int x, int y) {
		return x >= this.x && x < this.x + width && y >= this.y
				&& y < this.y + height;
	}

	static boolean collide(Room a, Room b) {
		return !a.isIn(b.x, b.y) && !a.isIn(b.x + b.width, b.y)
				&& !a.isIn(b.x + b.width, b.y + b.height)
				&& !a.isIn(b.x, b.y + b.height);
	}

	boolean collide(Room b) {
		return Room.collide(this, b);
	}
}

class Hallway {
	private static final int POINTS = 1;
	private static final int WIDTH = 4;
	Point[] pt = new Point[POINTS + 2];
	private static final Random rand = new Random();

	boolean isIn(int x, int y) {
		/*
		 * __________________________ | 1x<lP | | | | y<tP | | 2| _|_ |x>lP&x<rP
		 * _|_ | | | 3| | | | | | | | | |_4|__|__|__|__|__|__x>rP__| |__ __ __
		 * __| |__ y>tP_ __| |__ __&__ __| |__ y<bP_ __| |__ __ __ __| |__ __|__
		 * __| |__ __|__ __| |_____y>bP__|
		 */

		
		for (int i = 0; i < pt.length - 1; i++) {
			//if the two points in question are horizontal...
			if (pt[i].y == pt[i + 1].y) {
				//lP = the left point, rP = right point
				Point lP = pt[i].x < pt[i + 1].x ? pt[i] : pt[i + 1], rP = pt[i].x > pt[i + 1].x ? pt[i]
						: pt[i + 1];
				if (
				// Check to make sure it's within the width
				Math.abs(y - pt[i].y) <= WIDTH / 2f &&
				// Check to make sure it's either in the margin on either end or
				// in the middle
						x >= lP.x - WIDTH / 2f &&
						x <= rP.x + WIDTH / 2f)
					return true;
			//if the two points in question are vertical
			} else if (pt[i].x == pt[i + 1].x) {
				Point tP = pt[i].y < pt[i + 1].y ? pt[i] : pt[i + 1], bP = pt[i].y > pt[i + 1].y ? pt[1]
						: pt[i + 1];
				if (// See the comments above
				Math.abs(x - pt[i].x) <= WIDTH / 2f && y >= tP.y - WIDTH / 2f && y <= bP.y + WIDTH / 2f )
					return true;
			}
		}
		// If you made it this far without a return true;, :(
		return false;
		
	}

	static Hallway generateConnection(int worldWidth, int worldHeight,
			Room start, Room finish) {
		Hallway h = new Hallway();

		/*
		 * Point[0] = somewhere within start room
		 * 
		 * First point: (x >= start.x && x < start.x+width) || (y >= start.y &&
		 * y < start.y+height)
		 * 
		 * Middle points: pt[i].x == pt[i-1].x || pt[i].y == pt[i-1].x
		 * 
		 * Last point: (x >= end.x && x < end.x+width) || (y >= end.y && y <
		 * end.y+height)
		 * 
		 * Point[n] = somewhere within end room
		 */

		// Add Point[0]
		h.pt[0] = new Point((int) (rand.nextDouble()
				* (start.getWidth() - WIDTH) + start.getX()),
				(int) (rand.nextDouble() * (start.getHeight() - WIDTH) + start
						.getY()));
		// Add Point[1] - Point[n-1]
//		Point toAdd;
		// For xy, true means that the x will be the same as the previous point,
		// false that y will be the same
		
		//
		// THIS ONLY WORKS WITH A SINGLE ELBOW
		//
		
		boolean xy
		//,a, b
		;

		xy = rand.nextBoolean();
		h.pt[1] = new Point(
				xy ? h.pt[0].x : rand.nextInt(finish.width - WIDTH)
						+ finish.width + WIDTH / 2,
				!xy ? h.pt[0].y : rand.nextInt(finish.height - WIDTH)
						+ finish.height + WIDTH / 2);


		//
		//
		//
		
//		for (int i = 1; i < POINTS + 1; i++) {
//			do {
//				a = b = false;
//				xy = rand.nextBoolean();
//				toAdd = new Point(
//						xy ? h.pt[i - 1].x : rand.nextInt(worldWidth),
//						!xy ? h.pt[i - 1].y : rand.nextInt(worldHeight));
//				if (i == 1 && i == POINTS) {
					// if (i == 1) {
					// /*
					// * (x >= start.x && x < start.x+width) || (y >= start.y
					// * && y < start.y+height)
					// */
					// a = (toAdd.x >= start.getX() && toAdd.x < start.getX()
					// + start.getWidth())
					// || (toAdd.y >= start.getY() && toAdd.y < start
					// .getY() + start.getHeight());
					//
					// } else
					// a = true;
					// if (i == POINTS - 1) {
					// b =
					// // Check to make sure it's in line with the ending area
					// ((toAdd.x >= finish.getX() && toAdd.x < finish.getX()
					// + finish.getWidth()) || (toAdd.y >= finish
					// .getY() && toAdd.y < finish.getY()
					// + finish.getHeight()))
					// // Check, if there is a previous point, to make
					// // sure it's in line with the previous point
					// & (POINTS == 1 || (POINTS > 1 ^ (toAdd.x == h.pt[i - 1].x
					// || toAdd.y == h.pt[i - 1].y)))
					// // True if there is not a previous point, if there is
					// // make sure that the current one is in line
					// ;
					// } else
					// b = true;
					// if (a && b) {
					// h.pt[i] = toAdd;
					// }
//					if (xy) {
//						h.pt[i].y = rand.nextInt(finish.height - WIDTH)
//								+ finish.height + WIDTH / 2;
//					} else {
//						h.pt[i].x = rand.nextInt(finish.width - WIDTH)
//								+ finish.width + WIDTH / 2;
//					}
//				} else {
//					/*
//					 * Middle points: pt[i].x == pt[i-1].x || pt[i].y ==
//					 * pt[i-1].x
//					 */
//					if (toAdd.x == h.pt[i - 1].x || toAdd.y == h.pt[i - 1].y) {
//						h.pt[i] = toAdd;
//					}
//				}
//				LucalActivity_v2.v.vibrate(50);
//			} while (h.pt[i] != toAdd);
//		}
		// Add Point[n]
		h.pt[POINTS+1] = new Point(
				!xy ? h.pt[1].x : rand.nextInt(finish.width - WIDTH)
						+ finish.width + WIDTH / 2,
				xy ? h.pt[1].y : rand.nextInt(finish.height - WIDTH)
						+ finish.height + WIDTH / 2);
		return h;
	}
}

public class TMXWorld {
	private static final int ROOMS = 6; // MUST be less than GRIDSIZE^2
	private static final int GRIDSIZE = 4;
	private static final int MAXSIZE = 16, MINSIZE = 5;
	private static final int FLOOR = 1, WALL = 2;
	private final int CELLSIZE;
	private int worldLength;
	private static final Random rand = new Random();
	Room[] rooms = new Room[ROOMS];
	boolean[][] gridCells = new boolean[GRIDSIZE][GRIDSIZE];
	ArrayList<Hallway> halls;
	private int currentRoom = 0;

	//
	// <DEBUG>
	//
	// Context context;
	//
	// </DEBUG>
	//

	// Only works with square world :)
	public TMXWorld(int edgeLength) {
		halls = new ArrayList<Hallway>();
		worldLength = edgeLength;
		CELLSIZE = worldLength / GRIDSIZE;
	}
	
	public boolean registerPlayerPosition(int x, int y)
	{
		int i;
		for (i = 0; i < rooms.length; i++) {
			if (rooms[i].isIn(x, y)) {
					break;
			}
		}
		if(i == currentRoom)
		{
			return false;
		}
		else if (i == rooms.length) {
			currentRoom = -1;
			return false;
		}
		else {
			if (currentRoom != -1) {
				resetRoom(currentRoom);
			}
			resetHalls();
			currentRoom = i;
			return true;
		}
	}

	public void resetWorld() {
		rooms[0] = new Room(0, 0, MAXSIZE, MAXSIZE, 0, 0);
		gridCells[0][0] = true;
		for (int i = 1; i < rooms.length; i++) {
			resetRoom(i);
		}
		if(LucalActivity_v2.HARD_CODED_PATH)
		{
			rooms[rooms.length-1] = new Room(54, 54, 8, 8, 4, 4);
		}
		resetHalls();
		if(LucalActivity_v2.HARD_CODED_PATH)
		{
			halls.add(Hallway.generateConnection(worldLength, worldLength, rooms[0], rooms[1]));
			halls.add(Hallway.generateConnection(worldLength, worldLength, rooms[1], rooms[rooms.length-1]));
		}
	}

	private void resetRoom(int room) {
		/*
		 * For the sake of processing power, I'm going to cheat here Instead of
		 * truly random rooms, let's grid up the whole area Then, just create a
		 * room quasi-randomly within a cell region Let's use a 4x4 gride for
		 * simplicity's sake
		 * 
		 * The below code was the original totally random room generation It's
		 * too slow for use on a phone :( int safety = 0; boolean temp = false;
		 * while (safety++ < 15) { rooms[room] = new Room( room == 0 ? 0 :
		 * rand.nextInt(worldWidth - MAXSIZE), room == 0 ? 0 :
		 * rand.nextInt(worldHeight - MAXSIZE), rand.nextInt(MAXSIZE - MINSIZE)
		 * + MINSIZE, rand.nextInt(MAXSIZE - MINSIZE) + MINSIZE); temp = true;
		 * for (int i = 0; i < rooms.length; i++) { if (i == room) continue; if
		 * (rooms[i] != null && rooms[room].collide(rooms[i])) { temp = false;
		 * break; } } if (temp) break; }
		 * 
		 * if(temp) { resetHalls(); }
		 */

		if (rooms[room] != null) {
			gridCells[rooms[room].cellX][rooms[room].cellY] = false;
		}

		Room r = null;
		int x, y;
		// int i = 0;
		while (r == null) {
			// Toast.makeText(context, "Loop run " + i++, 500);
			x = rand.nextInt(GRIDSIZE);
			y = rand.nextInt(GRIDSIZE);
			if (!gridCells[x][y]) {
				r = new Room(rand.nextInt(CELLSIZE - MINSIZE) + CELLSIZE * x,
						rand.nextInt(CELLSIZE - MINSIZE) + CELLSIZE * y,
						rand.nextInt(MAXSIZE - MINSIZE) + MINSIZE,
						rand.nextInt(MAXSIZE - MINSIZE) + MINSIZE, x, y);
				rooms[room] = r;
				gridCells[x][y] = true;
				break;
			}
		}
	}

	private void resetHalls() {
		halls.clear();
		halls.add(Hallway.generateConnection(worldLength, worldLength,
				rooms[0], rooms[1]));
		for (int i = 0; i < rooms.length; i++) {
			for (int j = i + 1; j < rooms.length; j++) {
				if (rooms[i] != null && rooms[j] != null && rand.nextBoolean()) {
					halls.add(Hallway.generateConnection(worldLength,
							worldLength, rooms[i], rooms[j]));
				}
			}
		}

	}

	public TMXTiledMap drawTMX(TMXTiledMap map) {
		for (int y = 0; y < worldLength; y++) {
			// LucalActivity_v2.v.vibrate(100);
			for (int x = 0; x < worldLength; x++) {
				map.getTMXLayers()
						.get(0)
						.getTMXTile(x, y)
						.setGlobalTileID(map, isInAnything(x, y) ? FLOOR : WALL);
			}
		}
		return map;
	}

	private boolean isInAnything(int x, int y) {
		for (int i = 0; i < rooms.length; i++) {
			if (rooms[i] != null && rooms[i].isIn(x, y))
				return true;
		}
		for (int j = 0; j < halls.size(); j++) {
			if (halls.get(j).isIn(x, y))
				return true;
		}
		return false;
	}
}
