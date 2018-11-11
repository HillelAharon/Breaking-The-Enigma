package engine.machineRelated.components.machine.api;

import common.SecretAndMsgInfo;
import engine.machineRelated.components.machine.builder.EnigmaMachineBuilder;
import engine.machineRelated.components.machine.secret.Secret;
import engine.machineRelated.components.reflector.Reflector;
import engine.machineRelated.components.rotor.Rotor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnigmaMachine implements Serializable {
    private final String ABC;
    private final int ROTORS_IN_USE;
    private List<Rotor> theRotors;
    private List<Reflector> theReflectors;
    private Secret currentSecret = null;

    public EnigmaMachine(EnigmaMachineBuilder builder) {
        ROTORS_IN_USE = builder.rotorsInUse;
        this.ABC = builder.ABC;
        theRotors = builder.theRotors;
        theReflectors = builder.theReflectors;
    }
    public EnigmaMachine(EnigmaMachine otherMachine){
        ABC = otherMachine.ABC;
        ROTORS_IN_USE = otherMachine.ROTORS_IN_USE;
        theRotors = new ArrayList<>();
        for (Rotor r:otherMachine.theRotors) {
            theRotors.add(new Rotor(r));
        }
        theReflectors = new ArrayList<>();
        theReflectors.addAll(otherMachine.theReflectors);
        currentSecret = new Secret(otherMachine.getCurrentSecret());
    }


    public Secret getCurrentSecret(){return currentSecret;}
    public List<Rotor> getTheRotors(){return theRotors;}
    public List<Reflector> getTheReflectors(){return theReflectors;}
    public String getABC(){return ABC;}// y
    public int getRotorsInUse(){return ROTORS_IN_USE;}// y
    public void resetMachine()throws IOException {
        int i = 0;

        if(currentSecret == null)
            throw new IOException("No secret initialize, Please set secret first.");

        for (int rotorsID : currentSecret.getSelectedRotorsInOrder())
            theRotors.get(rotorsID-1).setShiftCounter(currentSecret.getSelectedRotorsPositions().get(i++));//adjusting to zero based arrayList

    }




    public String process(String input){
        String output = "";
       // input = input.toUpperCase();
        for(Character ch : input.toCharArray())
        {
            boolean shiftRotorsPosition = true;
            int theWire = ABC.indexOf(ch);

            for (int j = ROTORS_IN_USE - 1; j >= 0; j--) {
                theWire = theRotors
                        .get(currentSecret.getSelectedRotorsInOrder().get(j)-1)//in case theRotors is sorted!!
                        .process(theWire, true, shiftRotorsPosition);
                shiftRotorsPosition = theRotors
                        .get(currentSecret.getSelectedRotorsInOrder().get(j)-1)//same!
                        .isNotchOnPane();
            }

            theWire = theReflectors.get(currentSecret.getSelectedReflector().ordinal()).reflect(theWire);

            for (int j = 0; j < ROTORS_IN_USE; j++)
                theWire = theRotors
                        .get(currentSecret.getSelectedRotorsInOrder().get(j)-1)//same!!!
                        .process(theWire, false, false);
            try {
                output += ABC.charAt(theWire);
            }
            catch (StringIndexOutOfBoundsException e){
               // System.out.println("The wire is: " + theWire + "\n For Secret: " + currentSecret.toString());
            }
        }
        return output;
    }
    public void setMachineSecretFromJS(SecretAndMsgInfo newSecret){
        List<Integer>rotorsIntIds = new ArrayList<>();
        List<Integer>rotorsIntPos = new ArrayList<>();
        newSecret.getRotorsIds().forEach(id -> rotorsIntIds.add(Integer.parseInt(id)));
        newSecret.getNotchs().forEach(pos -> rotorsIntPos.add(ABC.indexOf(pos)));
        this.currentSecret = new Secret(rotorsIntIds,rotorsIntPos,Reflector.Id.valueOf(newSecret.getReflector()));
        convertSecretToString();
    }

    public void setMachineSecret(Secret secret){
        if(this.currentSecret == null)
            this.currentSecret = new Secret(secret);
        else
        this.currentSecret.setSecret(secret);
        convertSecretToString();

    }
    public void convertSecretToString() {
        String newSecretToString = "";
        newSecretToString += "<";

        for (int i = 0; i < currentSecret.getSelectedRotorsInOrder().size(); i++) {
            newSecretToString += currentSecret.getSelectedRotorsInOrder().get(i);
            if (i != currentSecret.getSelectedRotorsInOrder().size() - 1)
                newSecretToString += ",";
        }

        newSecretToString += "><";

        for (int i = 0; i < currentSecret.getSelectedRotorsPositions().size(); i++) {
            newSecretToString += theRotors.get(currentSecret.getSelectedRotorsInOrder().get(i) - 1)
                    .getCharPositionByInteger(currentSecret.getSelectedRotorsPositions().get(i));
        }
        newSecretToString += "><" + currentSecret.getSelectedReflector().toString() + ">";
        currentSecret.setSecretToString(newSecretToString);
    }
    public MachineInfo getMachineInfo(){return new MachineInfo(this);}

    public class MachineInfo{
        int rotorInUse;
        String ABC;
        List<Integer> rotorIds;
        List<String> reflectorIds;
        SecretAndMsgInfo secretAndMsg;
        public MachineInfo(EnigmaMachine machine){
            this.rotorInUse = machine.getRotorsInUse();
            this.ABC = machine.getABC();
            this.rotorIds = new ArrayList<>();
            machine.getTheRotors().forEach(rotor -> this.rotorIds.add(rotor.getId()));
            this.reflectorIds = new ArrayList<>();
            machine.getTheReflectors().forEach(reflector -> this.reflectorIds.add(reflector.getId().toString()));
            if(machine.getCurrentSecret() != null){
                secretAndMsg = new SecretAndMsgInfo(machine.getCurrentSecret());
            }

        }

    }
}


