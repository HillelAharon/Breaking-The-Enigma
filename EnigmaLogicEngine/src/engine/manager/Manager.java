package engine.manager;

import common.SecretAndMsgInfo;
import engine.battlefield.*;
import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.machineRelated.components.machine.builder.EnigmaMachineBuilder;
import engine.machineRelated.jaxb.schema.generated.Decipher;
import engine.machineRelated.jaxb.schema.generated.Enigma;
import engine.machineRelated.jaxb.schema.generated.Machine;
import engine.players.ally.AlliesManager;
import engine.players.ally.Ally;
import engine.players.uboat.Uboat;
import engine.players.uboat.UboatManager;
import engine.users.UserManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Manager {
    public static final String UBOAT = "uboat";
    public static final String ALLIES = "ally";
    private final static String JAXB_XML_GENERATED = "engine.machineRelated.jaxb.schema.generated";
    UserManager userManager;
    BattlefieldManager battlefieldManager;
    AlliesManager alliesManager;
    UboatManager uboatManager;

    public Manager() {
        synchronized (this) {
            userManager = new UserManager();
            battlefieldManager = new BattlefieldManager();
            alliesManager = new AlliesManager();
            uboatManager = new UboatManager();
        }
    }


    ////////////////////////User Manager Functions///////////////////////////////////
    public boolean isUserExists(String username) {
        return userManager.isUserExists(username);
    }

    public synchronized String suggestAlternativeUsername(String username) {
        return userManager.suggestAlternativeUsername(username);
    }

    public synchronized void addUserUpdateUrl(String username, String url) {
        userManager.addUserUpdateUrl(username, url);
    }

    public synchronized void deleteUser(String username) {
        userManager.removeUser(username);
    }

    public synchronized String getUserLastUrl(String username) {
        return userManager.getUserLastUrl(username);
    }
    /////////////////////////////////////////////////////////////////////////////////


    ///////////////////////ally Manager Functions//////////////////////////////////
    public synchronized void addAllies(String username) {
        alliesManager.AddAllies(username);
    }

    public synchronized boolean addAlliesToBattlefield(String username, String battlefield) {
        return battlefieldManager.addAllyToBattlefield(battlefield, alliesManager.getAlly(username));
    }

    public synchronized void setAllyReady(String username, boolean ready) {
        alliesManager.setAllyReady(username, ready);
    }

    public synchronized void setAllyMissionSize(String username, int missionSize) {
        alliesManager.setAllyMissionSize(username, missionSize);
    }

    public synchronized boolean initDMForAlly(String username) {
        return alliesManager.initDmForAlly(username);
    }

    public synchronized int getAllyPort(String username) {
        return alliesManager.getAlly(username).getPort();
    }

    public synchronized Ally.AllyData getAllyData(String username){
        return alliesManager.getAlly(username).getAllyData();
    }

    //public synchronized int getAllyMissionSize(String username){return  alliesManager.getAlly(username).getDMMissionSize();}
    public synchronized void removeAllyFromBattlefield(String username) {
        battlefieldManager.removeAllyFromBattlefield(username, getBattlefieldName(username));
        alliesManager.removeBattlefieldInfo(username);
    }

    public synchronized boolean isBattlefieldEmpty(String username) {
        return battlefieldManager.isBattlefieldEmpty(getBattlefieldName(username));
    }

    public synchronized BattlefieldSummary isGameOver(String username) {
        return battlefieldManager.checkBattlefieldGameOver(getBattlefieldName(username));

    }

    public synchronized boolean endGame(String username){return battlefieldManager.endGame(getBattlefieldName(username));}


    /////////////////////////////////////////////////////////////////////////////////




    /////////////////////Uboat Manager Functions/////////////////////////////////////
    public synchronized void addUboat(String username){uboatManager.addUboat(username);}

    public EnigmaMachine buildUboatMachine(String username ,Machine machine) throws IOException, JAXBException {
        return uboatManager.setUboatMachine(username,new EnigmaMachineBuilder(machine).buildMachine());
    }

    public void buildUboatDictionary(String username, Decipher decipher){
        String excludeChars = "[" +decipher.getDictionary().getExcludeChars() + "]";
        uboatManager.setUboatExcludeChars(username,excludeChars);
        String tempDictionary = decipher.getDictionary().getWords().trim();
        tempDictionary = tempDictionary.replaceAll(excludeChars,"");
        tempDictionary = tempDictionary.toUpperCase();
        String[] splited = tempDictionary.split(" ");
        uboatManager.setUboatDictionary(username,new HashSet(Arrays.asList(splited)));
    }

    public synchronized EnigmaMachine getUboatMachine(String username){return uboatManager.getUboatMachine(username);}

    public synchronized Uboat getUboat(String username){return uboatManager.getUbout(username);}

    public synchronized EnigmaMachine.MachineInfo getUboatMachineInfo(String username){return uboatManager.getUboatMachineInfo(username);}

    public synchronized boolean setUboatSecretAndMsgFromJS(String username, SecretAndMsgInfo newSecretAndMsg){return uboatManager.setUboatSecretAndMsgFromJS(username,newSecretAndMsg);}

    public synchronized void setUboatReady(String username,boolean ready){uboatManager.getUbout(username).setReady(ready);}
    ////////////////////////////////////////////////////////////////////////////////




    ////////////////////Battlefield Manager Functions///////////////////////////////
    public void createBattlefield( String username,engine.machineRelated.jaxb.schema.generated.Battlefield battlefield) throws JAXBException, IOException {
        Battlefield newBattlefield = new BattlefieldBuilder(battlefield,uboatManager.getUbout(username)).buildBattlefield();
        battlefieldManager.createBattlefield(newBattlefield);
    }

    //    public Battlefield getUboatBattlefield(String username){return battlefieldManager.getBattlefield(uboatManager.getBattlefieldName(username));}

    public boolean isBattleReady(String username){
        return battlefieldManager.isBattlefieldReady(getBattlefieldName(username));
    }

    public synchronized List<BattlefieldInfo> getBattlefieldsInfoList(){return battlefieldManager.getBattlefieldsInfoList();}

    public synchronized void startBattlefieldGame(String username){battlefieldManager.startBattlefieldGame(getBattlefieldName(username));}

    public synchronized List<Ally.AllyData> getBattlefieldAlliesData(String username) {
        switch (this.getUserRole(username)) {
            case UBOAT:
            return battlefieldManager.getBattlefieldAlliesData(getBattlefieldName(username));
            case ALLIES:
                return battlefieldManager.getBattlefieldAlliesData(getBattlefieldName(username));
        }
        return null;
    }

    public synchronized Battlefield.BattlefieldProcessInfo getUboatBattlefieldProcessInfo(String username){return battlefieldManager.getUboatBattlefieldProcessInfo(getBattlefieldName(username));}

    public synchronized Battlefield.BattlefieldProcessInfo getAllyBattlefieldProcessInfo(String username){return battlefieldManager.getAllyBattlefieldProcessInfo(username,getBattlefieldName(username));}

    public synchronized void allyConfirmedToBattlefield(String username){battlefieldManager.allyConfirmedToBattlefield(getBattlefieldName(username));}

    public synchronized boolean isBattlefieldExist(String username){return battlefieldManager.isBattlefieldExist(getBattlefieldName(username));}

    public synchronized void clearBattlefield(String username){battlefieldManager.clearBattlefield(getBattlefieldName(username));}

    public synchronized boolean isAllNotified(String username){return battlefieldManager.isAllNotified(getBattlefieldName(username));}
    ////////////////////////////////////////////////////////////////////////////////



    //////////////////////Manager Functions/////////////////////////////////////////
    public synchronized EnigmaMachine.MachineInfo xmlToBuilders(String username, InputStream xml) throws JAXBException, IOException {
        Enigma xmlEnigma = deserializeFrom(xml);
        battlefieldManager.removeBattlefield(getBattlefieldName(username));
        if(battlefieldManager.isBattlefieldExist(xmlEnigma.getBattlefield().getBattleName()))
            throw  new IOException("Battle name already exist please choose different file");
        createBattlefield(username,xmlEnigma.getBattlefield());
        EnigmaMachine machine = buildUboatMachine(username,xmlEnigma.getMachine());
        buildUboatDictionary(username,xmlEnigma.getDecipher());
        return machine.getMachineInfo();

    }

    public static Enigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GENERATED);
        Unmarshaller u = jc.createUnmarshaller();
        return (Enigma) u.unmarshal(in);
    }

    public String getUserRole(String username){
        if(alliesManager.isAllyExist(username))
            return ALLIES;
        else if(uboatManager.isUboatExist(username))
            return UBOAT;
        return null;
    }

    public void setReady(String username,boolean ready){
        switch (this.getUserRole(username)) {
            case UBOAT:
               setUboatReady(username,ready);
               break;
            case ALLIES:
                setAllyReady(username,ready);
                break;
        }
    }

    public String getBattlefieldName(String username) {
        switch (this.getUserRole(username)) {
            case UBOAT:
                return uboatManager.getBattlefieldName(username);
            case ALLIES:
                return alliesManager.getBattlefieldName(username);
        }
        return null;
    }

    public synchronized String allyConfirmed(String username){
        if(!alliesManager.getAlly(username).isReady()) {
            if(alliesManager.getAlly(username).getAgentNum() != 0 ){
                allyConfirmedToBattlefield(username);
                setAllyReady(username, true);
                return "Confirm";
            }
            return "Agents number can't be 0";
        }
        return "Set already";
    }

    public synchronized void logOut(String username){
        switch (getUserRole(username)){
            case ALLIES:
                alliesManager.removeAlly(username);
                break;
            case UBOAT:
                battlefieldManager.removeBattlefield(uboatManager.getBattlefieldName(username));
                uboatManager.removeUSer(username);
                break;

        }
        userManager.removeUser(username);
    }
    ////////////////////////////////////////////////////////////////////////////////
}
