package engine.players.ally.decryptionManager;

import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.machineRelated.components.machine.secret.Secret;
import engine.machineRelated.components.reflector.Reflector;
import engine.machineRelated.components.rotor.Rotor;
import engine.players.ally.decryptionManager.communicate.AgentStreamData;
import engine.players.ally.decryptionManager.communicate.Instructions;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static engine.players.ally.decryptionManager.communicate.Instructions.options.MSG;
import static engine.players.ally.decryptionManager.communicate.Instructions.options.SECRET;


public class DecryptionManager extends Observable //implements Runnable
{


    private EnigmaMachine machine;
    private String allyName;
    private Difficulty difficulty;
    private String encryptedMessage = "";
    private Set<String> dictionary;
    private String excludeChars;
    private int missionSize = 0;

    List<AgentThread> agentThreadList;
    Thread runDecryptionManagerThread;
    int port;
    boolean ready = false;
    int agentNum = 0;

    private List<List<Integer>> allRotorsPermutations;
    private List<Reflector.Id> allPossibleReflectors;
    private double allOptionalSecrets;
    private BlockingQueue<Instructions> missionsQueue;
    private BlockingQueue<Pair<String,String >> candidateMSG;
    private boolean onGame = false;
    private boolean exit = false;
    public enum Difficulty {
        EASY(1), MEDIUM(2), HARD(3), IMPOSSIBLE(4);
        private final int val;

        Difficulty(int val) {
            this.val = val;
        }

        public int getValue() {
            return val;
        }
    }

    ///////////////////Getters And Setters//////////////////////////////////////
    public void copyMachine(EnigmaMachine machine) {
        this.machine = new EnigmaMachine(machine);
    }
    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }
    public int getAgentNum() {
        return agentNum;
    }
    public void setMissionSize(int missionSize) {
        this.missionSize = missionSize;
    }
    public int getMissionSize() {
        return missionSize;
    }
    public synchronized void setOnGame(boolean onGame) {
        this.onGame = onGame;
        if(!onGame) {
            setReady(false);
        }

    }
    public synchronized boolean isOnGame() {
        return onGame;
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    public void setDictionary(Set<String> dictionary) {
        this.dictionary = dictionary;
    }
    public void setExcludeChars(String excludeChars) {
        this.excludeChars = excludeChars;
    }
    public void setCandidateMSG(BlockingQueue<Pair<String, String>> candidateMSG) {
        this.candidateMSG = candidateMSG;
    }

    public synchronized boolean isReady() {
            while (ready) {
                try {
                    //System.out.println("Server waiting");
                    this.wait();
                   // System.out.println("Server resume");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        return ready;
    }


    public synchronized void setReady(boolean ready) {
        this.ready = ready;
        notifyAll();
    }


    public void setExit(boolean exit) {
        this.exit = exit;
    }
    ////////////////////////////////////////////////////////////////////////////

    public DecryptionManager(int port, String name) throws IOException {
        this.port = port;
        this.agentThreadList = new ArrayList<>();
        this.allyName = name;
        this.missionsQueue = new ArrayBlockingQueue<>(20);
    }


    public int howManyMissionLeft() {
        return (int)Math.floor(allOptionalSecrets / missionSize);
    }
    public List<Integer> getListagentsProgress(){
        List<Integer> infoList= new ArrayList<>();
        agentThreadList.forEach(agent -> infoList.add(agent.getNumOfSuggestedMsg()));
        return infoList;
    }
    public void setAllReflectorAndRotorsByDifficulty() {
        allPossibleReflectors = new ArrayList<>();
        allRotorsPermutations = new ArrayList<>();
        switch(difficulty){
            case EASY:
                allRotorsPermutations.add(machine.getCurrentSecret().getSelectedRotorsInOrder());
                allPossibleReflectors.add(machine.getCurrentSecret().getSelectedReflector());
                break;
            case MEDIUM:
                allRotorsPermutations.add(machine.getCurrentSecret().getSelectedRotorsInOrder());
                for(int i = 0; i<machine.getTheReflectors().size(); i++)
                    allPossibleReflectors.add(machine.getTheReflectors().get(i).getId());
                break;
            case HARD:
                findAllRotorsPermutations(machine.getCurrentSecret().getSelectedRotorsInOrder(),
                        allRotorsPermutations,
                        new ArrayList<>(),
                        machine.getRotorsInUse());
                for(int i = 0; i<machine.getTheReflectors().size(); i++)
                    allPossibleReflectors.add(machine.getTheReflectors().get(i).getId());
                break;
            case IMPOSSIBLE:
                List<Integer> rotorsId = new ArrayList<>();

                for(Rotor rotor: machine.getTheRotors())
                    rotorsId.add(rotor.getId());

                findAllRotorsPermutations(rotorsId,
                        allRotorsPermutations,
                        new ArrayList<>(),
                        machine.getRotorsInUse());
                for(int i = 0; i<machine.getTheReflectors().size(); i++)
                    allPossibleReflectors.add(machine.getTheReflectors().get(i).getId());
                break;
        }
    }
    private static void findAllRotorsPermutations(List<Integer> allRotorsAvailable, List<List<Integer>> res, List<Integer> currentRotorsList, int k) {
        if (currentRotorsList.size() == k) {
            res.add(currentRotorsList);
        } else {
            for (Integer i : allRotorsAvailable) {
                List<Integer> lst = new ArrayList<>(k);
                lst.addAll(currentRotorsList);
                lst.add(i);

                List<Integer> allRotorsAvailableNextCall = new ArrayList<>(allRotorsAvailable);
                allRotorsAvailableNextCall.remove(i);

                findAllRotorsPermutations(allRotorsAvailableNextCall, res, lst, k);
            }
        }
    }
    public void calcTotalMissionSize(EnigmaMachine machine) {
        if (difficulty == DecryptionManager.Difficulty.EASY) {
            allOptionalSecrets =  Math.pow(machine.getABC().length(),machine.getRotorsInUse());
        } else if (difficulty == DecryptionManager.Difficulty.MEDIUM) {
            allOptionalSecrets =  Math.pow(machine.getABC().length(),machine.getRotorsInUse()) * machine.getTheReflectors().size();
        } else { // diff == NOT COOL or HARD, the difference is by the size of all permutation list size
            allOptionalSecrets = calcAllRotorsPermutationsSize(machine.getTheRotors().size(),machine.getRotorsInUse()) *
                    Math.pow(machine.getABC().length(),machine.getRotorsInUse())*
                    machine.getTheReflectors().size();
        }
    }

    public double calcAllRotorsPermutationsSize(int all , int inUse){
        double res = 1, div = 1;
        for (int i = 1; i <= all ; ++i)
            res *= i;
        for (int i = 1 ; i < inUse ; ++i)
            div *= i;
        return res/div;
    }


    ////////////// Server Form Agents////////////////
    public void startServerListener() {
        Runnable server = () -> {
               // System.out.println("Server> Server is ready...");
                while (true) {
                    //System.out.println("Server> isAcceptable");
                    //waits until a new connection is made
                    //once it does - the method will stop block the code and return a socket to the client
                    try (ServerSocket serverSocket = new ServerSocket(port)){
                        serverSocket.setSoTimeout(2000);
                        //todo private function
                        isReady();

                        if(exit) {
                           // System.out.println("Close Server");
                            sendInstruction(new Instructions(Instructions.options.EXIT));
                            agentThreadList.forEach(thread -> deleteObserver(thread));
                            serverSocket.close();
                            return;
                        }
                        Socket socket = serverSocket.accept();
                       // System.out.println("Server> Accepted");
                        addClient(++agentNum, socket);
                       // System.out.println("Server> Client added");

                    }
                    catch (SocketTimeoutException e){

                    } catch (IOException e) {
                        //System.out.println("Server> I/O exception");
                        return;
                    }
                }



        };
        Thread serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();


    }
    private synchronized void addClient(int clientIndex, Socket socket) throws IOException {
        //creates a new thread for handling communication with the new client
        AgentThread agentThread = new AgentThread(clientIndex, socket);
        //adds the thread to the list of listeners to this server
        addObserver(agentThread);
        //Add to List
        this.agentThreadList.add(agentThread);
        //starts the new chat client
        agentThread.start();
    }

    private synchronized  void sendData() {
        //set mode to changed so that notifyObservers will update its observers
        setChanged();
        //iterate over all observers and update them
        AgentStreamData agentStreamData = new AgentStreamData(this.machine,this.dictionary,this.missionSize,this.encryptedMessage,this.excludeChars);
        notifyObservers(new Instructions(Instructions.options.DATA).setData(agentStreamData));
    }


    private synchronized void sendInstruction(Instructions instruction) {
        //set mode to changed so that notifyObservers will update its observers
        setChanged();
        //iterate over all observers and update them
        notifyObservers(instruction);
    }

    public void interruptAllAliveThreads() {
        runDecryptionManagerThread.interrupt();
        for (AgentThread th : agentThreadList
                ) {
            if (th.isAlive()) {
                th.interrupt();

            }
        }
        missionsQueue.clear();
    }



    ////////////////////////Generate Secrets/////////////////////////////////////////////////////////////////////
    public boolean runDecryptionManager(Thread game) {
        setOnGame(true);
        sendData();
        Runnable generateSecrets = () -> {
            try {
                for (List<Integer> rotorsPermutation : allRotorsPermutations) {
                    for (Reflector.Id refId : allPossibleReflectors) {
                        Secret initMissionSecret = initCurrentSecretSession(rotorsPermutation, refId);
                        machine.setMachineSecret(initMissionSecret);
                        do {
                            missionsQueue.put(new Instructions(SECRET).setSecret(new Secret(machine.getCurrentSecret())));
                            updateMissionsProgress();
                        }
                        while (machine.getCurrentSecret().nextMissionForAgentsAndDM(machine.getABC().length(), missionSize) && isOnGame());

                    }
                }
                if (game.isAlive()) {
                        game.join();
                }
            }catch (InterruptedException e) {
                //System.out.println("Run decryption interrupted");
            }

            //setReady(false);

        };
        runDecryptionManagerThread = new Thread(generateSecrets);
        runDecryptionManagerThread.setDaemon(false);
        runDecryptionManagerThread.start();
        return true;
    }


    private Secret initCurrentSecretSession(List<Integer> rotorsPermut, Reflector.Id refId) {
        return new Secret(rotorsPermut, new ArrayList<Integer>(Collections.nCopies(rotorsPermut.size(), 0)), refId);
        //would rather do it through the builder with the unused function buildForDM but couldnt make it work..;/
    }

    private void updateMissionsProgress() {
        allOptionalSecrets -= missionSize;
       // System.out.println("Mission left " + allOptionalSecrets);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////// Communicate Class ////////////////////////
    public class AgentThread extends Thread implements Observer {
        private int id;
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Instructions instruction;
        private int numOfSuggestedMsg = 0;

        public AgentThread(int id, Socket socket) throws IOException {
            this.id = id;
            this.socket = socket;
            //System.out.println("AgentThread> Setting I/O");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // System.out.println("AgentThread> Done Setting I/O");

        }

        public int getNumOfSuggestedMsg() {
            return numOfSuggestedMsg;
        }

        @Override
        public void update(Observable o, Object arg) {
            try {
                out.writeObject(arg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        ///////////////////////////////////Communicate with agents//////////////////////////////////////////////
        @Override
        public void run() {
//           Runnable reader = () ->{
            try {
                while (true) {
                    try {
                        instruction = (Instructions) in.readObject();
                        switch (instruction.getInstraction()) {
                            case SECRET:
                                if (isOnGame()) {
                                    out.writeObject(missionsQueue.take());
                                    out.flush();
                                }
                                break;
                            case MSG:
                                if (isOnGame()) {
                                    //System.out.println("Ally " + allyName + " Agent " + id + " Send " + instruction.getCandidateMSG());
                                    candidateMSG.put(new Pair<>(allyName, instruction.getCandidateMSG()));
                                    ++numOfSuggestedMsg;
                                }
                                break;

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
//           };
//           Thread readerThread = new Thread(reader);
//           readerThread.start();

        }

    }


}
