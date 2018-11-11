package engine.players.ally;

import engine.battlefield.Battlefield;
import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.players.ally.decryptionManager.DecryptionManager;

import java.io.IOException;
import java.util.List;


public class Ally {
    private String name;
    private int port;
    private boolean ready;
    private String battlefieldName;
    private DecryptionManager DM;

    ////////////// Getters And Setters ///////////////////
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.DM.setReady(ready);
        this.ready = ready;
    }
    public String getBattlefieldName() {
        return battlefieldName;
    }
    public int getPort() {
        return port;
    }
    /////////////////////////////////////////////////////////////

    ////////////// Getters And Setters For DM ///////////////////
    public void setDMMissionSize(int missionSize){DM.setMissionSize(missionSize);}
    public int getDMMissionSize(){return DM.getMissionSize();}
    public int getDMHowManyMissionLeft(){return DM.howManyMissionLeft();}
    public List<Integer> getDmListAgentsProgress(){return DM.getListagentsProgress();}
    public void setOnGame(boolean on){
        this.DM.setOnGame(on);
        if(!on)
            ready = false;
    }
    /////////////////////////////////////////////////////////


    public Ally(String username, int port){
        this.name = username;
        this.port = port;
        this.ready = false;
        this.DM = null;
    }
    public void removeBattlefieldInfo(){
        this.battlefieldName = "";
        DM.setDifficulty(null);
        DM.setDictionary(null);
        DM.setExcludeChars(null);
        DM.setCandidateMSG(null);
    }
    public void getBattlefieldInfo(Battlefield battlefield){
        this.battlefieldName = battlefield.getBattleName();
        DM.setDifficulty(battlefield.getDifficulty());
        DM.setDictionary(battlefield.getUboat().getDictionary());
        DM.setExcludeChars(battlefield.getUboat().getExcludeChars());
        DM.setCandidateMSG(battlefield.getCandidateMSG());
        calcDMSecrets(battlefield.getUboat().getMachine());

    }
    public boolean initDM() {
        try {
            DM = new DecryptionManager(port, this.name);
            DM.startServerListener();
            return true;
        } catch (IOException e) {
           return false;
        }

    }

    public void closeServer(){
        this.DM.setExit(true);
    }

    public void setDecryptionProcessInfo(EnigmaMachine machine , String EncryptedMsg){
        DM.setEncryptedMessage(EncryptedMsg);
        DM.copyMachine(machine);
        DM.setAllReflectorAndRotorsByDifficulty();
        DM.setOnGame(true);

    }
    public void runDecryptionManager(Thread game){
        this.DM.runDecryptionManager(game);
    }

    public void calcDMSecrets(EnigmaMachine machine){
        DM.calcTotalMissionSize(machine);
    }

    public AllyData getAllyData(){
        return new AllyData(this);
    }
    public int getAgentNum(){
        return this.DM.getAgentNum();
    }


    public class AllyData{
        private String name;
        private int agents;
        private boolean ready;
        private int port;
        private String battlename;

        public AllyData(Ally ally) {
            this.name = ally.getName();
            this.agents = ally.getAgentNum();
            this.ready = ally.ready;
            this.port = ally.port;
            this.battlename = ally.battlefieldName;
        }

        public String getName() { return name; }
        public int getAgents() { return agents; }
        public boolean isReady() {
            return ready;
        }

    }


}
