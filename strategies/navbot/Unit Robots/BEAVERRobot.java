package navbot;

import battlecode.common.Clock;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class BEAVERRobot extends BaseRobot {


	public static Direction tarDir;

	public static MapLocation tile;
	public static MapLocation[] visibleTiles;

	public static int downloadReady;
	public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
	}

	@Override
	public void run() {
		try {

			DataCache.updateRoundVariables();
			downloadReady = BroadcastSystem.read(2001);
			//System.out.println(DataCache.currentLoc);

			//System.out.println(Functions.locToInternalLoc(DataCache.currentLoc));

			rc.broadcast(TESTCHANNEL, Functions.locToInt(Functions.locToInternalLoc(DataCache.currentLoc)));
			
			tarDir = rc.getLocation().directionTo(DataCache.enemyHQ);
			
			// visibleTiles = MapEngine.unitScan(DataCache.currentLoc);
			// //System.out.println(visibleTiles);
			// for (MapLocation tile: visibleTiles){
			// 	rc.setIndicatorDot(tile, 255, 255, 255);
			// }
			// DataCache.updateSeenLocs(newLocs);
			// newLocs=new ArrayList<MapLocation>();
			// //DataCache.displaySeenLocs();

			if (downloadReady==1){
				MapEngine.map = BroadcastSystem.downloadMapArray(REFCHANNEL);
			}

			// System.out.println("/////////////////////////");
   //      	Functions.displayArray(MapEngine.map);
   //      	System.out.println("/////////////////////////");
			
			NavSystem.dumbNav(DataCache.enemyHQ);




		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
