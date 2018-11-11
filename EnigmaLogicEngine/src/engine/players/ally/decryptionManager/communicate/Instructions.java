package engine.players.ally.decryptionManager.communicate;

import engine.machineRelated.components.machine.secret.Secret;

import java.io.Serializable;

public class Instructions implements Serializable {
    public enum options{SECRET,MSG,EXIT,INFO,DATA}
    private options instraction;
    private Secret secret;
    private AgentStreamData data;

    public AgentStreamData getData() {
        return data;
    }

    public Instructions setData(AgentStreamData data) {
        this.data = data;
        return this;
    }

    public String getCandidateMSG() {
        return candidateMSG;
    }

    public Instructions setCandidateMSG(String candidateMSG) {
        this.candidateMSG = candidateMSG;
        return this;
    }

    private String candidateMSG;


    public synchronized Secret getSecret() {
        return secret;
    }

    public Instructions setSecret(Secret secret) {
        this.secret = secret;
        return this;
    }

    public Instructions(options op){
        synchronized (this) {
            instraction = op;
        }
    }

    public synchronized void setInstruction(options op){
        instraction = op;
    }

    public synchronized options getInstraction(){
        return instraction;
    }
}
