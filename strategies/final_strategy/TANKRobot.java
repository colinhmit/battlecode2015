package final_strategy;


import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TANKRobot extends BaseRobot {
    
    public static Direction tarDir;

    public boolean hasBeenSupplied;
    public MapLocation targetToProtect;


	public TANKRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		hasBeenSupplied = false;
		targetToProtect = getOurClosestTowerToThem();
	}

	@Override
	public void run() {
		try {
		    DataCache.updateRoundVariables();
		    RobotInfo[] senseAbleRobots = rc.senseNearbyRobots(24, this.theirTeam);
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TANK);
            MapLocation currentLocation = rc.getLocation();
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
            double supplyLevel = rc.getSupplyLevel();
            int numTanks = rc.readBroadcast(TANK_PREVIOUS_CHAN);
            if(rc.getSupplyLevel() > 0){
            	hasBeenSupplied = true;
            }
            if (enemyRobots.length>0) {
//                if (enemyRobots[0].type==RobotType.LAUNCHER) {
//                    Direction directionTo = rc.getLocation().directionTo(enemyRobots[0].location);
//                    rc.move(directionTo);
//                } else if (enemyRobots[0].type==RobotType.MISSILE) {
//                    Direction directionTo = rc.getLocation().directionTo(enemyRobots[0].location);
//                    rc.move(directionTo);
//                }
            	for(RobotInfo enemy : senseAbleRobots){
	        		if(enemy.type == RobotType.LAUNCHER){
//	        			NavSystem.smartNav(enemy.location, false);
	        			MapLocation enemyLoc = enemy.location;
//	        			Direction dirTo = currentLocation.directionTo(enemyLoc);
//	        			if(rc.isCoreReady() && !currentLocation.add(dirTo).equals(enemyLoc)){
//	        				rc.move(dirTo);
//	        			}
	        			NavSystem.dumbNav(enemyLoc);
	        			break;
	        		} else if(enemy.type == RobotType.MISSILE){
//	        			NavSystem.smartNav(enemy.location, false);
	        			MapLocation enemyLoc = enemy.location;
//	        			Direction dirTo = currentLocation.directionTo(enemyLoc);
//	        			if(rc.isCoreReady() && !currentLocation.add(dirTo).equals(enemyLoc)){
//	        				rc.move(dirTo);
//	        			}
	        			NavSystem.dumbNav(enemyLoc);
	        			break;
	        		}
            	}
        
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemyTanks(enemyRobots);
                }
            }
            if (rc.isCoreReady()) {
                if ((supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) || !hasBeenSupplied) {
                    NavSystem.dumbNav(this.myHQ);
                }
            }
            if (Clock.getRoundNum() < (rc.getRoundLimit()*.8-100)  ) {
                if (rc.isCoreReady()) {
                    if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<30) {
                        NavSystem.dumbNav(this.myHQ);
                    } else if (rc.senseNearbyRobots(20, this.theirTeam).length < 1 ) {
                        //MapLocation ourClosest = getOurClosestTowerToThem();
                       // RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
                        ////System.out.println(neighbors.length);
                        //int numTanks = numTanksSurrounding(rc,neighbors);
                        double radiusOfTanks = numTanks/Math.PI;
                        for(MapLocation towerLoc : rc.senseEnemyTowerLocations()){
                        	Direction directionTowardsTower = targetToProtect.directionTo(towerLoc);
                        	MapLocation furthestTank = targetToProtect.add(directionTowardsTower, (int) Math.sqrt(radiusOfTanks));
                        	int distance = towerLoc.distanceSquaredTo(furthestTank);
                        	if(distance <=24){
                        		int difference = 24 - distance;
                        		int changeInTarget = (int)Math.sqrt(difference);
                        		Direction dirFromTowerToLoc = towerLoc.directionTo(targetToProtect);
                        		targetToProtect = targetToProtect.add(dirFromTowerToLoc,changeInTarget);

                        	}
                        }
//                        if(currentLocation.distanceSquaredTo(ourClosest) > radiusOfTanks || rc.canMove(currentLocation.directionTo(ourClosest)) ) {
//                        	NavSystem.dumbNav(ourClosest);
//                        }
                        if(currentLocation.distanceSquaredTo(targetToProtect) > radiusOfTanks || rc.canMove(currentLocation.directionTo(targetToProtect)) ) {
                        	NavSystem.dumbNav(targetToProtect);
                        }
                    }
                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {                        
                        NavSystem.dumbNav(closest);
                    } else {
                        NavSystem.dumbNav(DataCache.enemyHQ);
                    }
                } 
            }
            transferSpecificSupplies(RobotType.TANK, rc, nearbyAllies);
            rc.broadcast(TANK_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);


		} catch (Exception e) {
			//                    //System.out.println("caught exception before it killed us:");
			//                    //System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
