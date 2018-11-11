package engine.battlefield;

import engine.players.ally.Ally;
import engine.players.ally.decryptionManager.DecryptionManager;
import engine.players.uboat.Uboat;
import javafx.util.Pair;


import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Battlefield {

    private DecryptionManager.Difficulty difficulty;
    private int rounds;
    private String battleName;
    private int alliesNum;
    private Uboat uboat;
    private Map<String,Ally>allies;
    private int notifications;
    private List<String> winners;
    private String currentWinner = null;
    private String decryptedMSG;
    private BlockingQueue<Pair<String,String>> candidateMSG;

    //dynamic by round
    //private String winner = null;
    private int currentRound = 1;
    private boolean startGame = false;
    private int confirmedAllies = 0;


    public Battlefield(BattlefieldBuilder builder){
        this.difficulty = builder.getDifficulty();
        this.alliesNum = builder.getAlliesNum();
        this.battleName = builder.getBattleName();
        this.rounds = builder.getRounds();
        this.uboat = builder.getUboat();
        this.uboat.setBattlefieldName(battleName);
        allies = new HashMap<>();
        winners = new ArrayList<>(this.rounds);
        this.candidateMSG = new ArrayBlockingQueue<>(alliesNum * 3);
    }

    /////////////////// Getters And Setters ///////////////////////////////
    public DecryptionManager.Difficulty getDifficulty() {
        return difficulty;
    }
    public int getRounds() {
        return rounds;
    }
    public String getBattleName() {
        return battleName;
    }
    public int getAlliesNum() {
        return alliesNum;
    }
    public Uboat getUboat() {
        return uboat;
    }
    public Map<String, Ally> getAllies() {
        return allies;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public int getConfirmedAllies() {
        return confirmedAllies;
    }
    public boolean isStartGame() {
        return startGame;
    }
    public void setStartGame(boolean startGame) {
        this.startGame = startGame;
    }
    public BlockingQueue<Pair<String, String>> getCandidateMSG() {
        return candidateMSG;
    }
    public List<String> getWinners() {
        return winners;
    }
    /////////////////////////////////////////////////////////

    public void incConfirmedAllies(){++confirmedAllies;}
    public boolean isBattleReady(){
        if(confirmedAllies == alliesNum && uboat.isReady() && !startGame)
            return true;
        return false;
    }
    public boolean isFull(){return allies.size() == alliesNum;}
    public int getAlliesAppliedNum(){return allies.size();}
    public List<Ally.AllyData> getAlliesData(){
        List<Ally.AllyData> dataList = new ArrayList<>();
        allies.values().forEach(ally -> dataList.add(ally.getAllyData()));

        return dataList;
    }

    public void removeAllyFromBattlefield(String  ally){
        allies.remove(ally);
    }

    public boolean addAlliesToBattlefield(Ally ally){
        if(!isFull() && !startGame) {
            allies.put(ally.getName(), ally);
            ally.getBattlefieldInfo(this);
            ally.setReady(false);
            return true;
        }
        return false;
    }

    public void startGame(){
        if(startGame)
            return;
        startGame = true;
        currentWinner = null;
        notifications = alliesNum + 1; // notify allies and uboat;
        //System.out.println("Game Starting");
        this.decryptedMSG = uboat.getDecryptedMSG();

        for (Ally ally : allies.values()
                ) {
            ally.setDecryptionProcessInfo(uboat.getMachine(),this.uboat.getEncryptedMSG() );
        }

        Runnable game = () -> {
            //System.out.println("Game Running");
            while(true){
                try {
                    Pair<String ,String> message = candidateMSG.take();
                    //System.out.println("Uboat took " + message.getKey() + ">" + message.getValue());
                    if(message != null){
                        uboat.addCandidateMSGToScreenList(message);
                        if(checkCandidateMsg(message.getValue())){
                            //System.out.println("winner " + message.getKey() + " found " + message.getValue());
                            currentWinner = message.getKey();
                            winners.add(currentWinner);
                            uboat.setReady(false);
                            allies.values().forEach(ally -> ally.setOnGame(false) );
                            return;

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread gameThread = new Thread(game);
        allies.values().forEach(ally -> ally.runDecryptionManager(gameThread));
        gameThread.setDaemon(false);
        gameThread.start();
        //System.out.println("Game Started");

    }
    public boolean checkCandidateMsg(String message){
        return decryptedMSG.compareTo(message) == 0;
    }

    public void clearBattlefield(){
        startGame = false;
        //++currentRound;

        currentWinner = null;
        confirmedAllies = 0;
        candidateMSG.clear();
        decryptedMSG="";
        endGame();
    }

    public boolean endGame(){

        if(currentRound == rounds) {
            allies.clear();
            winners.clear();
            currentRound = 1;
            return true;
        }
        ++currentRound;
        return false;
    }
    public synchronized BattlefieldSummary isGameOver(){
        BattlefieldSummary summary;
        if(currentRound == rounds)
            summary = new BattlefieldSummary(this);
        else
            summary = new BattlefieldSummary(currentWinner, currentRound);
        --notifications;
        if(notifications == 0)
            clearBattlefield();
        return summary;
    }

    public synchronized boolean isAllNotified(){
//        if(notifications == 0)
//            clearBattlefield();
        return notifications == 0;
    }

//////////////////////////////Battlefield Info////////////////////////////////////
    public BattlefieldProcessInfo getUboatBattlefieldProcessInfo(){
        return new BattlefieldProcessInfo(this);


    }
    public BattlefieldProcessInfo getAllyBattlefieldProcessInfo(String username){
        BattlefieldProcessInfo info = new BattlefieldProcessInfo(this);
        info.setStartGame(startGame);
        info.setAgentsProgress(allies.get(username).getDmListAgentsProgress());
        info.setRemainingMissions(allies.get(username).getDMHowManyMissionLeft());
        return info;
    }
    public class BattlefieldProcessInfo {

        String winner;
        int currentRound;
        int rounds;
        List<String> candidateMSG = null;
        private int remainingMissions;
        private List<Integer> agentsProgress = null;
        private boolean startGame = false;

        public BattlefieldProcessInfo(Battlefield bf){
            this.winner = currentWinner;
            this.currentRound = bf.currentRound;
            this.rounds = bf.rounds;
            this.candidateMSG = bf.getUboat().getCandidateMSGToScreen();

        }

        public void setRemainingMissions(int remainingMissions) {
            this.remainingMissions = remainingMissions;
        }

        public void setAgentsProgress(List<Integer> agentsProgress) {
            this.agentsProgress = agentsProgress;
        }

        public void setStartGame(boolean startGame) {
            this.startGame = startGame;
        }
        public String getWinner() {
            return winner;
        }

    }

    //////////////////////////////////////////////////////////////////////
}
