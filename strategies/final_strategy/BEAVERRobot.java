package final_strategy;


import java.util.ArrayList;
import java.util.List;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class BEAVERRobot extends BaseRobot {


    public static Direction tarDir;

    public static MapLocation tile;
    public static MapLocation[] visibleTiles;

    public static List<MapLocation> newLocs=new ArrayList<MapLocation>();

	public BEAVERRobot(RobotController rc) throws GameActionException {
		super(rc);
	      NavSystem.UNITinit(rc);
	      MapEngine.UNITinit(rc);
	}

	@Override
	public void run() {
		try {
		    boolean building = false;
			if(rc.isCoreReady()){
				double ore = rc.getTeamOre();
				int minerFactoriesBuilt = rc.readBroadcast(40);
				int barracksBuilt = rc.readBroadcast(41);
				int tankFactoriesBuilt = rc.readBroadcast(42);
				int supplyDepotsBuilt = rc.readBroadcast(44);
				RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.BEAVER);
				

			    if (enemyRobots.length>0) {
	                if (rc.isWeaponReady()) {
	                    attackLeastHealthEnemy(enemyRobots);
	                }
			    } else if (rc.hasBuildRequirements(RobotType.MINERFACTORY) && minerFactoriesBuilt <2) {
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.MINERFACTORY);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.MINERFACTORY);
                        rc.broadcast(40, minerFactoriesBuilt+1);
                        building=true;
                    }			   
			    } else if (rc.hasBuildRequirements(RobotType.SUPPLYDEPOT) && supplyDepotsBuilt < 1 || rc.hasBuildRequirements(RobotType.SUPPLYDEPOT) && minerFactoriesBuilt>0 && supplyDepotsBuilt<3 || ore>1200 && supplyDepotsBuilt < 4 || ore>2000 && supplyDepotsBuilt<5 || ore>2200 && supplyDepotsBuilt<7) {
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.SUPPLYDEPOT);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.SUPPLYDEPOT);
                        rc.broadcast(44, supplyDepotsBuilt+1);
                        building=true;
                    }     			        
			    } else if (rc.hasBuildRequirements(RobotType.BARRACKS) && barracksBuilt < 1 && minerFactoriesBuilt>0) {
                    Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.BARRACKS);
                    if (buildDirection!=null) {
                        rc.build(buildDirection, RobotType.BARRACKS);
                        rc.broadcast(41, barracksBuilt+1);
                        building=true;
                    }       
			    } else if (rc.hasBuildRequirements(RobotType.TANKFACTORY) && tankFactoriesBuilt < 4 || ore>2000 && tankFactoriesBuilt < 5 || ore>2500 && tankFactoriesBuilt<6) {
			        Direction buildDirection = getBuildDirectionCheckerBoard(RobotType.TANKFACTORY);
			        if (buildDirection != null) {
			            rc.build(buildDirection, RobotType.TANKFACTORY);
			            rc.broadcast(42, tankFactoriesBuilt+1);
			            building=true;
			        }
			    } 
			    if (!building) {		        
	                if(rc.senseOre(rc.getLocation())>3){
	                    rc.mine();
	                } else if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())> 18){
	                    Direction moveDir = getMoveDir(this.myHQ);
	                    if (moveDir!=null) {
	                        rc.move(moveDir);
	                    }
	                } else{
	                    moveRandomly();
	                }			        
			    }


			}
			//transferSupplies(rc);
			rc.broadcast(BEAVER_CURRENT_CHAN, rc.readBroadcast(BEAVER_CURRENT_CHAN)+1);
		} catch (Exception e) {
			//                    //System.out.println("caught exception before it killed us:");
			//                    //System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
	
	private void constructBuilding(RobotType type) throws GameActionException {
	    Direction buildDirection = getBuildDirection(type);
	    if (buildDirection != null) {
	        rc.build(buildDirection, type);
	    }
	}
}
