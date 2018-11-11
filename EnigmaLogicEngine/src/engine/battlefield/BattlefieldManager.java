package engine.battlefield;

import engine.players.ally.Ally;

import java.util.*;

public class BattlefieldManager {

    private final Map<String,Battlefield> battlefieldsMap;

    public BattlefieldManager() {battlefieldsMap = new HashMap<>();}

    public boolean isBattlefieldExist(String battlefieldName){return battlefieldsMap.containsKey(battlefieldName);}
    public void createBattlefield(Battlefield battlefield){battlefieldsMap.put(battlefield.getBattleName(),battlefield);}
    public Battlefield getBattlefield(String battlename){return battlefieldsMap.get(battlename); }
    public boolean isBattlefieldReady(String battlename){return  battlefieldsMap.get(battlename).isBattleReady();}
    public boolean addAllyToBattlefield(String battlefield, Ally ally){
        if(battlefieldsMap.containsKey(battlefield))
        return battlefieldsMap.get(battlefield).addAlliesToBattlefield(ally);
        return false;
    }
    public List<BattlefieldInfo> getBattlefieldsInfoList(){
        List<BattlefieldInfo> battlefieldsInfoList = new ArrayList<>();
        battlefieldsMap.values().forEach(bf -> battlefieldsInfoList.add(new BattlefieldInfo(bf)));
        return battlefieldsInfoList;
    }
    public void startBattlefieldGame(String battlefieldName){battlefieldsMap.get(battlefieldName).startGame();}
    public List<Ally.AllyData> getBattlefieldAlliesData(String battlefield){return battlefieldsMap.get(battlefield).getAlliesData();}
    public Battlefield.BattlefieldProcessInfo getAllyBattlefieldProcessInfo(String username, String battlefieldName){return battlefieldsMap.get(battlefieldName).getAllyBattlefieldProcessInfo(username);}
    public Battlefield.BattlefieldProcessInfo getUboatBattlefieldProcessInfo(String battlefieldName){return battlefieldsMap.get(battlefieldName).getUboatBattlefieldProcessInfo();}
    public void allyConfirmedToBattlefield(String battlefieldName){battlefieldsMap.get(battlefieldName).incConfirmedAllies();}
    public void removeBattlefield(String battlefield){
        if(battlefieldsMap.containsKey(battlefield))
        battlefieldsMap.remove(battlefield);
    }
    public void removeAllyFromBattlefield(String username,String battlefieldName){
        if(battlefieldsMap.containsKey(battlefieldName))
            battlefieldsMap.get(battlefieldName).removeAllyFromBattlefield(username);
    }
    public void clearBattlefield(String battlefield){battlefieldsMap.get(battlefield).clearBattlefield();}
    public boolean isBattlefieldEmpty(String battlefield){return battlefieldsMap.get(battlefield).getAlliesAppliedNum() == 0;}
    public BattlefieldSummary checkBattlefieldGameOver(String battlefield){return battlefieldsMap.get(battlefield).isGameOver();}
    public boolean isAllNotified(String battlefield){return battlefieldsMap.get(battlefield).isAllNotified();}
    public boolean isStartGame(String battlefield){return battlefieldsMap.get(battlefield).isStartGame();}
    public boolean endGame (String battlefield){return battlefieldsMap.get(battlefield).endGame();}
}
