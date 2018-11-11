package engine.machineRelated.components.machine.builder;

import engine.machineRelated.components.machine.api.EnigmaMachine;
import engine.machineRelated.components.reflector.Reflector;
import engine.machineRelated.components.rotor.Rotor;
import engine.machineRelated.jaxb.schema.generated.Machine;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public  class EnigmaMachineBuilder {

    public String ABC;
    public final int rotorsInUse;
    private Map<Integer,Rotor> rotorMap;
    private Map<Integer,Reflector> reflectorMap;
    public List <Rotor> theRotors;
    public List <Reflector> theReflectors;
    public Machine machine;

    public EnigmaMachineBuilder(Machine machine) throws IOException {
        this.machine = machine;
        if(this.machine.getRotorsCount() < 2 || this.machine.getRotorsCount() > 99)
            throw new IOException("Error: Rotors in use should be between 2 to 99.");
        rotorsInUse = machine.getRotorsCount();
        ABC = this.machine.getABC().trim();
        if(ABC.length() % 2 != 0)
            throw new IOException("Error: ABC length must be even.");
        Set<Character> tempSet = new HashSet<>();
        for(Character c : ABC.toCharArray())
        {
            if(tempSet.contains(c))
                throw new IOException("Error: ABC values must be unique.");
            tempSet.add(c);
        }
        rotorMap = new HashMap<>();
        reflectorMap = new HashMap<>();

    }

    public EnigmaMachine buildMachine() throws IOException, JAXBException,IllegalArgumentException
    {

        for(engine.machineRelated.jaxb.schema.generated.Rotor r : this.machine.getRotors().getRotor()) {
            String  from = "",to = "";
            for(int i = 0 ; i < r.getMapping().size() ; ++i){
                from += r.getMapping().get(i).getRight();//Check!!!
                to += r.getMapping().get(i).getLeft();
            }
            defineRotor(r.getId(), from, to, r.getNotch());
        }

        for(engine.machineRelated.jaxb.schema.generated.Reflector r : this.machine.getReflectors().getReflector()) {
            List<Integer> input = new ArrayList<Integer>(), output = new ArrayList<Integer>();
            for(int i = 0 ; i < r.getReflect().size() ; ++i)
            {
                input.add(r.getReflect().get(i).getInput() - 1);
                output.add(r.getReflect().get(i).getOutput() - 1);
            }
            defineReflector(r.getId(), input, output);
        }
        return create();
    }

    public  EnigmaMachineBuilder defineRotor(int id , String from , String to, int notch)throws IOException {
        if(from.length() != to.length())
            throw new IOException("Error: 'from' and 'to' count should be equal.");
        if(id < 1)
            throw new IOException("Error: Rotor id can't be less than 1.");
        if(notch < 1 || notch > ABC.length())
            throw new IOException("Error: Notch must be at least 0 and less than ABC length.");
        if( from.length() != ABC.length())
            throw new IOException("Error: Mapping length must be equal to ABC length.");
        if(this.rotorMap.get(id) != null)
            throw  new IOException("Error: Rotor id already exists.");
        try{
            checkStringUniqueAndExist(from);
            checkStringUniqueAndExist(to);
        }
        catch (IOException e)
        {
            String message = "Error: " + e.getMessage();
            throw new IOException(message);
        }

        rotorMap.put(id ,new Rotor(id,notch,from,to));
        return this;
    }

    public EnigmaMachineBuilder defineReflector(String id ,List<Integer> in, List<Integer> out)throws IOException{
        if(in.size() != out.size())
            throw new IOException("Error: Reflectors input values number must be equal to output values");

        List<Integer> temp = new ArrayList<Integer>(in.size() + out.size());
        temp.addAll(in);
        temp.addAll(out);
        temp = temp.stream().sorted().collect(Collectors.toList());

        if(temp.size() != ABC.length())
            throw new IOException("Error: Reflectors in size and out size must be equal to ABC length");
        for(int i = 0 ; i < temp.size(); ++i)
        {
            if(temp.get(i) != i)
                throw new IOException("Error: Not all connectors in reflector: " + id +  " mapped");
        }

        Reflector.Id enumId;
        switch (id) {
            case "I":
                enumId = Reflector.Id.I;
                break;
            case "II":
                enumId = Reflector.Id.II;
                break;
            case "III":
                enumId = Reflector.Id.III;
                break;
            case "IV":
                enumId = Reflector.Id.IV;
                break;
            case "V":
                enumId = Reflector.Id.V;
                break;
            default:
                throw new IOException("Error: Reflectors Id did't found.");

        }
        int intId = enumId.ordinal();
        if(reflectorMap.containsKey(intId))
            throw new IOException("Error: Reflectors Id already exist");
        reflectorMap.put(intId,new Reflector(enumId , in , out));
        return this;
    }


    public void checkStringUniqueAndExist(String abc)throws IOException{
        Set<Character> tempSet = new HashSet<Character>();
        for(Character c : abc.toCharArray())
        {
            if(tempSet.contains(c))
                throw new IOException("Error: ABC values must be unique");
            if(!ABC.contains(c.toString()))
                throw new IOException("Error: Character: " + c +" doesn't exist in ABC" );
            tempSet.add(c);
        }
    }
    public EnigmaMachine create()throws IOException{
        theRotors = new ArrayList<Rotor>(rotorMap.values());
        if(theRotors.size() < this.rotorsInUse)
            throw new IOException("Error: Unable to define less rotors than minimum rotors in use.");
        for(int i = 0 ; i < theRotors.size() ; ++i) {
            if(i + 1 != theRotors.get(i).getId())
                throw new IOException("Error: Rotors Id numbering should be a consecutive series from 1 to the number of rotors in inventory.");
        }
        theReflectors = new ArrayList<Reflector>(reflectorMap.values());
        for(int i = 0 ; i < theReflectors.size() ; ++i){
            if(i != theReflectors.get(i).getId().ordinal())
                throw new IOException("Error: Reflector Id numbering should be a consecutive series from 1 to the number of reflectors in inventory in roman digits.");
        }

        return new EnigmaMachine(this);
    }
}
