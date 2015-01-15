package navbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {

	public static int count= 0;

	public static MapLocation testRobotLoc;
	public static MapLocation testRobotInternalLoc;
	public static MapLocation[] testHQLocs;
	public static MapLocation[] testRobotSeenLocs;
	public static int broadcastReady;

	public static int[][] testmap;


	public HQRobot(RobotController rc) throws GameActionException {
		super(rc);

		//Init Systems//
		NavSystem.HQinit(rc);
		MapEngine.HQinit(rc);
		BroadcastSystem.write(2001, 1);


		



	}

	@Override
	public void run() {
		try {

			

			broadcastReady = BroadcastSystem.read(2001);

			if (rc.isCoreReady() && rc.getTeamOre() >= 100 && count ==0) {
				rc.setIndicatorString(0, "trying to spawn");
				rc.spawn(rc.getLocation().directionTo(DataCache.enemyHQ),RobotType.BEAVER);
				count = 1;
	            	//RobotPlayer.trySpawn(RobotPlayer.directions[RobotPlayer.rand.nextInt(8)], RobotType.BEAVER);
        	}

        	if (broadcastReady == 1){

        		//System.out.println("/////////////////////////");
        		BroadcastSystem.broadcastMapArray(REFCHANNEL, MapEngine.map);
        		// System.out.println("Test");
        		BroadcastSystem.prepareandsendMapDataDict(MapEngine.waypointDictHQ);
        		//testmap = BroadcastSystem.downloadMapArray(REFCHANNEL);
        		
        		// int[][] thirdMap = new int[MapEngine.xdim][MapEngine.ydim];
        		// for (int x=0;x<MapEngine.xdim;x++){
        		// 	for (int y=0;y<MapEngine.ydim;y++){
        		// 		if (MapEngine.map[x][y]==testmap[x][y]){
        		// 			thirdMap[x][y] = 1;
        		// 		}
        		// 		else{
        		// 			thirdMap[x][y] = 9;
        		// 		}
        		// 	}
        		// }
        		// System.out.println("/////////////////////////");
        		// Functions.displayArray(testmap);
        		// System.out.println("/////////////////////////");
        		//BroadcastSystem.write(2001, 0);
        	}



        	testRobotInternalLoc = Functions.intToLoc(rc.readBroadcast(TESTCHANNEL));

        	testRobotLoc = Functions.internallocToLoc(testRobotInternalLoc);
        	//System.out.println(testRobotLoc);
        	//System.out.println(testRobotLoc);
        	testHQLocs = MapEngine.structScan(rc.getLocation());

        	testRobotSeenLocs = MapEngine.unitScan(testRobotLoc);

        		// for (MapLocation loc: testHQLocs){
        		// 	rc.setIndicatorDot(loc, 255, 255, 255);
        		// }

        	//System.out.println(testRobotSeenLocs);
        	//MapEngine.scanTiles(testRobotSeenLocs);
        	MapEngine.scanTiles(testHQLocs);
        	MapEngine.scanTiles(testRobotSeenLocs);

        	//System.out.println(MapEngine.waypointDictHQ);

        	// System.out.println("/////////////////////////");
        	// Functions.displayArray(MapEngine.map);
        	// System.out.println("/////////////////////////");

        	//System.out.println(testRobotLoc);




		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
