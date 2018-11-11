package engine.battlefield;

public class BattlefieldInfo {
    private final String name;
    private final String uboat;
    private  String status;
    private  int rounds;
    private  String level;
    private  String usersApplied;

    public BattlefieldInfo(Battlefield battlefield){
        name = battlefield.getBattleName();
        uboat = battlefield.getUboat().getName();
        level = battlefield.getDifficulty().toString();
        rounds = battlefield.getRounds();
        usersApplied = battlefield.getAlliesAppliedNum() +"/"+ battlefield.getAlliesNum();
        if(battlefield.isStartGame())
            status = "Started";
        else
        status = battlefield.getAlliesAppliedNum() < battlefield.getAlliesNum() ? "open" : "full" ;
    }


}