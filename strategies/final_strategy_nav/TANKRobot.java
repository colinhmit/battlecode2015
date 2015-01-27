package final_strategy_nav;
import battlecode.common.Clock;

import java.util.*;


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

    public static int downloadReady;
    
    public MapLocation targetToProtect;
    public Direction dirTowardsLauncher;


	public TANKRobot(RobotController rc) throws GameActionException {
		super(rc);
		NavSystem.UNITinit(rc);
		MapEngine.UNITinit(rc);
		hasBeenSupplied = false;
        BroadcastSystem.write(BroadcastSystem.myInstrChannel, TANK_CONS);
        BroadcastSystem.setNotCollecting();
        targetToProtect = getOurClosestTowerToThem();
        dirTowardsLauncher = null;
	}

	@Override
	public void run() {
		try {
		    DataCache.updateRoundVariables();
            //TELL THE HQ WHERE WE ARE
            if (DataCache.hasMoved()){
                MapEngine.senseQueue.add(DataCache.currentLoc);
            }

            downloadReady = BroadcastSystem.read(BroadcastSystem.myInstrChannel);

            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.TANK);
            RobotInfo[] enemyRobotsCanSee = rc.senseNearbyRobots(24,myTeam);
            MapLocation currentLocation = rc.getLocation();
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
            double supplyLevel = rc.getSupplyLevel();
            if(rc.getSupplyLevel() > 0){
            	hasBeenSupplied = true;
            }
//            if(enemyRobotsCanSee.length >0){
//            	for(RobotInfo enemy : enemyRobotsCanSee){
//            		if(enemy.type == RobotType.LAUNCHER){
//            			NavSystem.smartNav(enemy.location, false);
//            			break;
//            		} else if(enemy.type == RobotType.MISSILE){
//            			NavSystem.smartNav(enemy.location, false);
//            			break;
//            		}
//            	}
//            }
//            if (enemyRobots.length>0) {
//            	
//                if (rc.isWeaponReady()) {
//                    attackLeastHealthEnemyTanks(enemyRobots);
//                }
//            }
            if (rc.isCoreReady()) {
                if ((supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<25) || !hasBeenSupplied) {
                    NavSystem.dumbNav(this.myHQ);
                }
            }
            if (downloadReady>=25000){
                        //System.out.println("BEAVER TEST");
                        //rc.setIndicatorString(1, "messaging");
                        BroadcastSystem.prepareandsendLocsDataList(MapEngine.senseQueue, downloadReady);
                        //System.out.println(MapEngine.senseQueue);
                        MapEngine.resetSenseQueue();
                        BroadcastSystem.write(BroadcastSystem.myInstrChannel,0);
                        //System.out.println("BEAVER TEST END");
                    //  System.out.println("BEAVER TESTCHANNEL");
                    }
            if (downloadReady==2){
                //System.out.println("BEAVER TEST 2");

                //rc.setIndicatorString(1, "downloading");
                BroadcastSystem.receiveMapDataDict(BroadcastSystem.dataBand);
                // System.out.println("/////////////////////////");
    // // //            Functions.displayOREArray(MapEngine.map);
       //           System.out.println("/////////////////////////");
       //           Functions.displayWallArray(MapEngine.map);
       // //            // //System.out.println(MapEngine.waypointDict);
       //           System.out.println("/////////////////////////");
                MapEngine.waypointDict = BroadcastSystem.receiveWaypointDict();
                //System.out.println("BEAVER TEST 2 END");
                //System.out.println(MapEngine.waypointDict);
                //System.out.println("Test2");
                //rc.setIndicatorString(1, "not downloading");
                BroadcastSystem.write(BroadcastSystem.myInstrChannel, 0);
            }
            if(dirTowardsLauncher != null && rc.isCoreReady()){
            	NavSystem.smartNav(currentLocation.add(dirTowardsLauncher), false);
            }
            if(enemyRobotsCanSee.length >0){
            	for(RobotInfo enemy : enemyRobotsCanSee){
            		if(enemy.type == RobotType.LAUNCHER){
            			//NavSystem.smartNav(enemy.location, false);
//            			if(rc.isCoreReady()){
//            				rc.move(currentLocation.directionTo(enemy.location));
//            			}
            			dirTowardsLauncher = currentLocation.directionTo(enemy.location);
            			break;
            		} else if(enemy.type == RobotType.MISSILE){
            			//NavSystem.smartNav(enemy.location, false);
//            			if(rc.isCoreReady()){
//            				rc.move(currentLocation.directionTo(enemy.location));
//            			}
            			dirTowardsLauncher = currentLocation.directionTo(enemy.location);
            			break;
            		}
            	}
            }
            else if (enemyRobots.length>0) {
            	
                if (rc.isWeaponReady()) {
                    attackLeastHealthEnemyTanks(enemyRobots);
                }
            }
            
            if (Clock.getRoundNum() < 1400) {
                if (rc.isCoreReady()) {
                    if (supplyLevel < 50 && currentLocation.distanceSquaredTo(this.myHQ)<30) {
                        NavSystem.smartNav(this.myHQ, false);
                    } else if (rc.senseNearbyRobots(20, this.theirTeam).length < 1 ) {
//                        MapLocation ourClosest = getOurClosestTowerToThem();
//                        RobotInfo[] neighbors = rc.senseNearbyRobots(rc.getLocation(),1,rc.getTeam());
                        //System.out.println(neighbors.length);
                        //int numTanks = numTanksSurrounding(rc,neighbors);
                        //System.out.println(numTanks);
                        double radiusOfTanks = rc.readBroadcast(TANK_PREVIOUS_CHAN)/Math.PI;
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
//                        	NavSystem.smartNav(ourClosest, false);
//                        }
                        if(currentLocation.distanceSquaredTo(targetToProtect) > radiusOfTanks || rc.canMove(currentLocation.directionTo(targetToProtect)) ) {
                        	NavSystem.smartNav(targetToProtect, false);
                        }
                    }
                }
            } else {
                if (rc.isCoreReady()) {
                    MapLocation closest  = getClosestTower();
                    if (closest != null) {                        
                        NavSystem.smartNav(closest, false);
                    } else {
                        NavSystem.smartNav(DataCache.enemyHQ, false);
                    }
                } 
            }
            transferSpecificSupplies(RobotType.TANK, rc, nearbyAllies);
            rc.broadcast(TANK_CURRENT_CHAN, rc.readBroadcast(TANK_CURRENT_CHAN)+1);


		} catch (Exception e) {
			//                    System.out.println("caught exception before it killed us:");
			//                    System.out.println(rc.getRobot().getID());
			//e.printStackTrace();
		}
	}
}
