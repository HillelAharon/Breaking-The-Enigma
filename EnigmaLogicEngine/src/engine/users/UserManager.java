package engine.users;
import java.util.*;

public class UserManager {

    private final Map<String,String> usersMap;
    public UserManager() {
        usersMap = new HashMap<>();
    }
    public void addUserUpdateUrl(String username, String url) { usersMap.put(username,url); }
    public void removeUser(String username) {
        usersMap.remove(username);
    }
    public String getUserLastUrl(String username){return usersMap.get(username);}
    public boolean isUserExists(String username) { return usersMap.containsKey(username); }
    public String suggestAlternativeUsername(String username) {
        int i = 1;
        while(true){
            // villainsList[reandom];
            String suggestedUsername = username + i;
            if(usersMap.containsKey(suggestedUsername) == false ){
                return suggestedUsername;
            }
            i++;
        }
    }

}



