package engine.players.uboat;

import common.SecretAndMsgInfo;
import engine.machineRelated.components.machine.api.EnigmaMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UboatManager {
    private Map<String,Uboat>uboatMap;

    public UboatManager(){uboatMap = new HashMap<>();}
    public void removeUSer(String username){uboatMap.remove(username);}
    public void addUboat(String username){uboatMap.put(username,new Uboat(username));}
    public Uboat getUbout(String username){return uboatMap.get(username);}

    public EnigmaMachine setUboatMachine(String username,EnigmaMachine machine){
        uboatMap.get(username).setMachine(machine);
        return machine;
    }
    public void setUboatDictionary(String username, Set<String> dictionary){uboatMap.get(username).setDictionary(dictionary);}
    public void setUboatExcludeChars(String username, String excludeChars){uboatMap.get(username).setExcludeChars(excludeChars);}
    public boolean isUboatExist(String username){return uboatMap.containsKey(username);}
    public EnigmaMachine getUboatMachine(String username){return uboatMap.get(username).getMachine();}
    public EnigmaMachine.MachineInfo getUboatMachineInfo(String username){return uboatMap.get(username).getMachineInfo();}
    public String getBattlefieldName(String username){return uboatMap.get(username).getBattlefieldName();}
    public boolean setUboatSecretAndMsgFromJS(String username, SecretAndMsgInfo newSecretAndMsg){

        if(uboatMap.get(username).checkMsgWords(newSecretAndMsg.getMsg())) {
            uboatMap.get(username).getMachine().setMachineSecretFromJS(newSecretAndMsg);
            uboatMap.get(username).setDecryptedMSG(newSecretAndMsg.getMsg());
            uboatMap.get(username).processAndSetEncryptedMSG(newSecretAndMsg.getMsg());
            return true;
        }
        return false;

    }
}
