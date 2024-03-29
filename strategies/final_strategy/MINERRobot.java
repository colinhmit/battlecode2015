package final_strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import battlecode.common.*;

public class MINERRobot extends BaseRobot {
	
	static Random rand = new Random();
	public final static int MINER_COST = 60;
	
    private final static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	private boolean supplied;
	
	public MINERRobot(RobotController rc) throws GameActionException {
		super(rc);
	    NavSystem.UNITinit(rc);
	    MapEngine.UNITinit(rc);
		supplied = false;
	}

	@Override
	public void run() {

		
		try {
            DataCache.updateRoundVariables();
            RobotInfo[] enemyRobots = getEnemiesInAttackingRange(RobotType.MINER);
            MapLocation currentLocation = rc.getLocation();
            double oreCurrentLocation = rc.senseOre(currentLocation);
            double supplyLevel = rc.getSupplyLevel();
            double oreDensity = getOreDensity(currentLocation);
            RobotInfo[] enemiesAround = rc.senseNearbyRobots(24,theirTeam);
			int numMiners = numMiners(enemiesAround);
			int currentNumMiners = rc.readBroadcast(NUM_MINERS_IN_POSITION);
			if(numMiners>= currentNumMiners){
				rc.broadcast(MINERS_TO_ATTACK_X, currentLocation.x);
				rc.broadcast(MINERS_TO_ATTACK_Y, currentLocation.y);
				rc.broadcast(NUM_MINERS_IN_POSITION, numMiners);
				rc.broadcast(MINER_COMMAND, 1);

			}
            if (enemyRobots.length>0 && rc.isWeaponReady()) {
                attackLeastHealthEnemy(enemyRobots);
            }
		    if (Clock.getRoundNum() < rc.getRoundLimit()*0.85) {
	            if (oreDensity > rc.readBroadcast(200) && oreDensity>150) {
	                rc.broadcast(200, (int)oreDensity);
	                rc.broadcast(201, currentLocation.x);
	                rc.broadcast(202, currentLocation.y);
	            }
	            if(rc.isCoreReady()) {
	                if (!supplied) {
	                    NavSystem.dumbNav(myHQ);
	                    if (supplyLevel>100 || Clock.getRoundNum()>1000) {
	                        supplied = true;
	                    }
	                } 
	                else if (rc.senseOre(rc.getLocation())>4 && rc.canMine()) {
	                    rc.mine();
	                } else {
	                    Direction[] directions = getDirectionsAway(this.myHQ);
	                    List<Integer> directionList = moveToMaxOrRandomList(directions);
	                    int ore = directionList.get(1);
	                    Direction direction = RobotPlayer.directions[directionList.get(0)];
	                    if(rc.canMove(direction) && senseNearbyTowers(currentLocation, direction)==0 && ore > 4) {
	                        rc.move(direction);
	                    } else {
	                        if (oreDensity <100 ) {
	                            int xCoordinate = rc.readBroadcast(201);
	                            int yCoordinate = rc.readBroadcast(202);
	                            MapLocation oreLocation = new MapLocation(xCoordinate, yCoordinate); 
	                                if (rc.readBroadcast(200) !=0) {
	                                    NavSystem.dumbNav(oreLocation);
	                                } else {
	                                    int fate = RobotPlayer.rand.nextInt(100);
	                                    if (fate<50) {
	                                        int rv = RobotPlayer.rand.nextInt(8);
	                                        Direction randomDirection = RobotPlayer.directions[rv];
	                                        if (senseNearbyTowers(currentLocation, randomDirection)==0) {
	                                            if (rc.canMove(randomDirection)) {
	                                                rc.move(randomDirection);
	                                            }
	                                        }
	                                    } else {
	                                        int rv = RobotPlayer.rand.nextInt(5);
	                                        Direction randomDirection = getDirectionsAway(this.myHQ)[rv];
	                                        if (senseNearbyTowers(currentLocation, randomDirection)==0) {
	                                            if (rc.canMove(randomDirection)) {
	                                                rc.move(randomDirection);
	                                            }
	                                        }
	                                    }

	                                }
	                        } else {
	                            int rv = RobotPlayer.rand.nextInt(8);
	                            Direction randomDirection = RobotPlayer.directions[rv];
	                            if (senseNearbyTowers(currentLocation, randomDirection)==0) {
	                                if (rc.canMove(randomDirection)) {
	                                    rc.move(randomDirection);
	                                }
	                            }
	                        }

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
		    
			RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
			transferSpecificSupplies(RobotType.MINER, rc, nearbyAllies);
			rc.broadcast(MINER_CURRENT_CHAN, rc.readBroadcast(MINER_CURRENT_CHAN)+1);
			
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
    
    private List<Integer> moveToMaxOrRandomList(Direction[] dirs) throws GameActionException {
        MapLocation myLocation = rc.getLocation();      
        int fate = RobotPlayer.rand.nextInt(5);
        Direction dir = dirs[fate];
        Collections.shuffle(Arrays.asList(directions));
        
        for (Direction direction : directions) {
            if (!rc.canMove(dir) || (rc.canMove(direction)&& rc.senseOre(myLocation.add(direction))> rc.senseOre(myLocation.add(dir)))) {
                dir = direction;
                
            }
        }
        int dirInt = RobotPlayer.directionToInt(dir);
        int oreAmount = (int) Math.floor(rc.senseOre(myLocation.add(dir)));
        List<Integer> returnList = Arrays.asList(dirInt, oreAmount);
        return returnList;
    }

	private Direction getDirectionAwayFromHQ() {
		return (rc.getLocation().directionTo(rc.senseHQLocation()).opposite());
	}
	  
    static int directionToInt(Direction d) {
        switch(d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }
}
