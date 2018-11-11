package engine.battlefield;

import engine.machineRelated.jaxb.schema.generated.Battlefield;
import engine.players.ally.decryptionManager.DecryptionManager;
import engine.players.uboat.Uboat;

import java.io.IOException;

public final class BattlefieldBuilder {

    private DecryptionManager.Difficulty difficulty;
    private int rounds;
    private String battleName;
    private int alliesNum;
    private Battlefield battlefield;
    private Uboat uboat;

    ////////////////////////Getters/////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////

    public BattlefieldBuilder(Battlefield battlefield, Uboat uboat) throws IOException {
        this.battlefield = battlefield;
        if(!isDifficultyValid())
            throw new IOException("Battlefield level invalid" +
                    "\nPlease load file the contain one of the below levels:" +
                    "\n1.Easy" +
                    "\n2.Medium" +
                    "\n3.Hard" +
                    "\n4.Impossible");
        this.battleName = battlefield.getBattleName();
        this.uboat = uboat;
        this.alliesNum = battlefield.getAllies();
    }

    public boolean isDifficultyValid(){
        try {
            this.difficulty = DecryptionManager.Difficulty.valueOf(battlefield.getLevel().toUpperCase());
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public engine.battlefield.Battlefield buildBattlefield(){
        this.rounds = battlefield.getRounds();
        this.alliesNum = battlefield.getAllies();
        return create();
    }
    public engine.battlefield.Battlefield create(){
        return new engine.battlefield.Battlefield(this);
    }

}
