
import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.machineRelated.components.machine.secret.Secret;
import engine.players.ally.decryptionManager.communicate.Instructions;

import java.io.*;
import java.net.Socket;
import java.util.Set;

public class Agent //implements Runnable
{
    private static int missionSize;
    private static EnigmaMachine machine;
    private static Set<String> dictionary;
    private static String encryptedMsg;
    private static String excludeChars;
    private static Instructions instruction;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static Socket socket;

    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Agent should get 1 argument with info about socket connection format ip:port");
            return;
        }
        String[] splitString = args[0].split(":");
        try {
            startClient(splitString[0],Integer.parseInt(splitString[1]));
        } catch (Exception e) {
            System.out.println("Agent connection closed");
        }
    }
    public static void startClient(String ip,int port) throws IOException, ClassNotFoundException {
        //creates a socket to the server
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connected");
        startDecryptionProcess();

    }


    public static void startDecryptionProcess() {
        try {
            while (true) {
                boolean continueWithMission;
                String currentDecryptedMsg;

                instruction = (Instructions) in.readObject();
                switch (instruction.getInstraction()) {

                    case SECRET:
                        Secret currentSecret = null;
                        currentSecret = instruction.getSecret();
                        machine.setMachineSecret(currentSecret);
                        machine.convertSecretToString();
                        int missionsCount = 0;
                        do {
                            machine.resetMachine();
                            currentDecryptedMsg = machine.process(encryptedMsg);
                            currentDecryptedMsg.replaceAll(excludeChars, "");
                            if (decMsgMakeSense(currentDecryptedMsg)) {//need to adjust to capital letter!
                                out.writeObject(new Instructions(Instructions.options.MSG).setCandidateMSG(currentDecryptedMsg));
                                out.flush();
                            }
                            continueWithMission = machine.getCurrentSecret().nextMissionForAgentsAndDM(machine.getABC().length(), 1);
                        } while (++missionsCount < missionSize && continueWithMission == true);
                        break;

                    case DATA:
                        getData();
                        break;

                    case INFO:
                        missionSize = instruction.getData().getMissionSize();
                        encryptedMsg = instruction.getData().getEncryptedMSG();
                        break;

                    case EXIT:
                        System.out.println("Agent Exit");
                        return;

                }
                out.writeObject(new Instructions(Instructions.options.SECRET));
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Agent connection closed");
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Agent Class Not Found");
        }
    }
    private static void getData(){
        machine = instruction.getData().getMachine();
        dictionary = instruction.getData().getDictionary();
        missionSize = instruction.getData().getMissionSize();
        encryptedMsg = instruction.getData().getEncryptedMSG();
        excludeChars = instruction.getData().getExcludeChars();
        System.out.println("Agent get Data");
    }
    private static boolean decMsgMakeSense(String currentDecryptedMsg) {
        String[] arrayOfWords = currentDecryptedMsg.split(" ");
        for(int i=0; i < arrayOfWords.length ; i++){
            if(!dictionary.contains(arrayOfWords[i])){
                return false;
            }
        }
        return true;

    }


}
