package engine.players.ally;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public  class  AlliesManager {

    private Map<String,Ally>alliesMap;
    int port = 9000;

    public AlliesManager(){alliesMap = new HashMap<>();}
    public void  AddAllies(String username){
        alliesMap.put(username,new Ally(username,port++));
    }
    public void removeAlly(String username){
        alliesMap.get(username).closeServer();
        alliesMap.remove(username);
    }
    public Ally getAlly(String username){return alliesMap.get(username);}
    public boolean isAllyExist(String username){return alliesMap.containsKey(username); }
    public void setAllyReady(String username,boolean ready){alliesMap.get(username).setReady(ready);}
    public void setAllyMissionSize(String username,int missionSize){alliesMap.get(username).setDMMissionSize(missionSize);}
    public String getBattlefieldName(String username){return alliesMap.get(username).getBattlefieldName();}
    public boolean initDmForAlly(String username){return alliesMap.get(username).initDM();}
    public void removeBattlefieldInfo(String username){alliesMap.get(username).removeBattlefieldInfo();}
}
