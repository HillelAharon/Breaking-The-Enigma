package engine.players.uboat;

import engine.machineRelated.components.machine.api.EnigmaMachine;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Uboat {
    private String name;
    private String battlefieldName;
    private EnigmaMachine machine;
    private boolean ready;
    private Set<String> dictionary;
    private String excludeChars;
    private String encryptedMSG;
    private String decryptedMSG;
    private List<String> candidateMSGToScreen;

    //////////////////////////////////Getters And Setters/////////////////////////////////////
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public EnigmaMachine getMachine() {
        return machine;
    }
    public void setMachine(EnigmaMachine machine) {
        this.machine = machine;
    }
    public boolean isReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
        if(ready)
        candidateMSGToScreen.clear();
    }
    public Set<String> getDictionary() {
        return dictionary;
    }
    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }
    public String getExcludeChars() {
        return excludeChars;
    }
    public void setExcludeChars(String excludeChars) {
        this.excludeChars = excludeChars;
    }
    public String getEncryptedMSG() {
        return encryptedMSG;
    }
    public void setDecryptedMSG(String decryptedMSG) {
        this.decryptedMSG = decryptedMSG.replaceAll(excludeChars,"");
    }
    public String getBattlefieldName() {
        return battlefieldName;
    }
    public void setBattlefieldName(String battlefieldName) {
        this.battlefieldName = battlefieldName;
    }
    public List<String> getCandidateMSGToScreen() {
        if(candidateMSGToScreen.size() > 0) {
            List<String> res = new ArrayList<>(candidateMSGToScreen.size());
            candidateMSGToScreen.forEach(msg ->res.add(msg));
            candidateMSGToScreen.clear();
            return res;
        }
        return null;
    }
    public String getDecryptedMSG() {
        return decryptedMSG;
    }
    public EnigmaMachine.MachineInfo getMachineInfo(){
        if(machine != null)
        return this.machine.getMachineInfo();
        return null;
    }
    /////////////////////////////////////////////////////////////////////////////////////

    public Uboat(String username){
        this.name = username;
        this.ready = false;
        candidateMSGToScreen = new ArrayList<>();
    }

    public boolean checkMsgWords(String message){
        message = message.replaceAll(excludeChars,"");
        String[] splited = message.split(" ");
        for (String word: splited) {
            if (!dictionary.contains(word)) {
                return false;
            }
        }
        return true;
    }
    public void processAndSetEncryptedMSG(String message) {
        this.encryptedMSG = this.machine.process(message);
    }

    public void addCandidateMSGToScreenList(Pair<String ,String > message){
        String msg = message.getKey();
        msg += ">" + message.getValue();
        //System.out.println(msg);
        candidateMSGToScreen.add(msg);
    }
}
