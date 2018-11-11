package engine.players.ally.decryptionManager.communicate;

import engine.machineRelated.components.machine.api.EnigmaMachine;

import java.io.Serializable;
import java.util.Set;

public class AgentStreamData implements Serializable {
    EnigmaMachine machine;
    Set<String> dictionary;
    int missionSize;
    String encryptedMSG;



    String excludeChars;

    public AgentStreamData(EnigmaMachine machine, Set<String>dictionary,int missionSize,String encryptedMSG,String excludeChars){
        synchronized (this) {
            this.machine = machine;
            this.dictionary = dictionary;
            this.missionSize = missionSize;
            this.encryptedMSG = encryptedMSG;
            this.excludeChars = excludeChars;
        }
    }

    public AgentStreamData(int missionSize,String encryptedMSG){
        synchronized (this) {
            machine = null;
            dictionary = null;
            this.missionSize = missionSize;
            this.encryptedMSG = encryptedMSG;
        }
    }

    public EnigmaMachine getMachine() {
        return machine;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public int getMissionSize() {
        return missionSize;
    }

    public String getEncryptedMSG() {
        return encryptedMSG;
    }
    public String getExcludeChars() {
        return excludeChars;
    }
}
