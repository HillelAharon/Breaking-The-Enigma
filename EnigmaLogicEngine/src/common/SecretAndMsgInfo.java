package common;

import engine.machineRelated.components.machine.secret.Secret;

import java.util.ArrayList;
import java.util.List;

public class SecretAndMsgInfo {

    public SecretAndMsgInfo(Secret secret){
        rotorsIds = new ArrayList<>();
        secret.getSelectedRotorsInOrder().forEach(id -> rotorsIds.add(id.toString()));
        reflector = secret.getSelectedReflector().toString();
        notchs = new ArrayList<>();
        secret.getSelectedRotorsPositions().forEach(pos -> notchs.add(pos.toString()));
    }
    public List<String> getRotorsIds() {
        return rotorsIds;
    }

    public void setRotorsIds(List<String> rotorsIds) {
        this.rotorsIds = rotorsIds;
    }

    public String getReflector() {
        return reflector;
    }

    public void setReflector(String reflector) {
        this.reflector = reflector;
    }

    public List<String> getNotchs() {
        return notchs;
    }

    public void setNotchs(List<String> notchs) {
        this.notchs = notchs;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg.toUpperCase();
    }

    List<String> rotorsIds;
    String reflector;
    List<String> notchs;
    String msg;
}
