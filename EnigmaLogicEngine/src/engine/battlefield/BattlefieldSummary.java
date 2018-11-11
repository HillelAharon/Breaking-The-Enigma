package engine.battlefield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattlefieldSummary {
    public String getStatus() {
        return status;
    }

    String status;
    String winner = null;
    List<String> rank;
    int round;

    public BattlefieldSummary(Battlefield bf){
        Map<String,Integer> countMap = new HashMap<>();
        bf.getWinners().forEach(name ->
        {
            if(countMap.containsKey(name))
                countMap.put(name,(countMap.get(name) + 1));
            else
                countMap.put(name , 1);
        });
        int max = 0;
        for(String key : countMap.keySet()){
            if(max < countMap.get(key)){
                max = countMap.get(key);
                status = "Winner";
                winner = key;
            }
            else if (max == countMap.get(key)){
                status = "Dead Heat!!";
                winner = null;
            }
        }

        rank = new ArrayList<>();
        for(int i = max ; i >= 0 ; --i){
            for(String key : countMap.keySet()) {
                if (countMap.get(key) == i)
                rank.add(key + " win " + countMap.get(key) + " times");

            }
        }




    }
    public BattlefieldSummary(String winner,int round){
        status = "round";
        this.winner = winner;
        this.round = round;
    }
}
