package com.pending.game3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.spec.ECField;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Game3 {
    public static final String jsonDir = "game3/resources/json";
    public static final String mainSplash = "So, you want to play an adventure game?\n" +
            "Too bad, this is just an engine for adventure games.\n" +
            "After selecting New Game, you will be prompted to choose a .json file to load the game from.\n" +
            "Yes, you read that correctly: This engine can read in the entire game from a .json file, so you and " +
            "your friends can modify or even create new game worlds to play in this engine.\n" +
            "The sky is the limit!\n\n\n\n" +
            "Or is it?";
    private FileParser fileParser;
    private InputParser inputParser;
    private List<String> inventory;
    private Room currentRoom;
    private HashMap<String, Room> rooms;
    private HashMap<String, Item> items;
    private HashMap<String, Npc> npcs;
    private Scanner reader;

    //singleton
    private static Game3 instance;
    // logic for running a new instance of a game
    public static void runProgram() {
        if (instance == null) {
            instance = new Game3();
            instance.inputParser = new InputParser();
            instance.reader = new Scanner(System.in);
        }
        instance.run();
    }

    private Game3(){}
    //end singleton

    //accessors
    static void setInventory(List<String> newInventory){
        instance.inventory = newInventory;
    }

    static void setCurrentRoom(Room newCurrentRoom){
        instance.currentRoom = newCurrentRoom;
    }

    static void setRooms(HashMap<String, Room> newRooms){
        instance.rooms = newRooms;
    }

    static void setItems(HashMap<String, Item> newItems){
        instance.items = newItems;
    }

    static void setNpcs(HashMap<String, Npc> newNpcs){
        instance.npcs = newNpcs;
    }

    static List<String> getInventory(){
        return instance.inventory;
    }

    static Room getCurrentRoom(){
        return instance.currentRoom;
    }

    static HashMap<String, Room> getRooms(){
        return instance.rooms;
    }

    static HashMap<String, Item> getItems(){
        return instance.items;
    }

    static HashMap<String, Npc> getNpcs(){
        return instance.npcs;
    }



    //method for starting the game
    private void run() {
        System.out.println(mainSplash);//splash screen
        if(mainMenu()) return;

        try (Stream<Path> stream = Files.list(Path.of(jsonDir))) {
            List<Path> files = getJsonList(stream);
            if (promptUserForFile(reader, files)) return;
        } catch (Exception e) {
            System.out.println("Unable to locate resources\\json folder.");
            return;
        }
        inventory = fileParser.startingInventory;
        rooms = fileParser.roomsAtStart;
        items = fileParser.itemsAtStart;
        currentRoom = rooms.get(fileParser.startingRoom);
        npcs = fileParser.npcsAtStart;
        mainLoop();
    }
    // method to run the main menu
    private boolean mainMenu() {
        System.out.println("[1]: Start new game\n[4]: quit program");
        while (true){
            String input = reader.nextLine();
            switch (input){
                case "1":
                    return false;
                case "4":
                    return true;
            }
            System.out.println("Invalid input, please enter the number for your menu selection.");

        }
    }
    //method that handles the game logic of taking in a command and displaying a room
    private void mainLoop() {
        while (true) {
            displayRoom();
            if(inputParser.getInput(reader)) {
                break;
            }
        }
    }
    // method that show the user the current room, items in the room, and their inventory
    private void displayRoom() {
        System.out.println(getCurrentRoom().description);
        System.out.println("Items: " + getCurrentRoom().items);
        System.out.println("Inventory: " + getInventory());
        System.out.print("Movement options: ");
        for(String direction : currentRoom.connections.keySet()) {
            System.out.print("\"" + direction + "\" ");
        }
    }
    // method to prompt user to load JSON file
    private boolean promptUserForFile(Scanner reader, List<Path> files) {

        // Dummy code for Iteration 1, delete when ready to uncomment real code below
//        try {
//            fileParser = FileParser.loadFile(Path.of(jsonDir));
//        } catch (Exception e) {
//            System.out.println("FileParser error");
//        }

        //Real code for files, uncomment when ready
        while (true){
            printFiles(files);
            System.out.print("Enter a number to select a json file to load: ");
            String input = reader.nextLine();
            if ("quit".equals(input.toLowerCase())) return true;
            try{
                int inputIndex = Integer.parseInt(input) - 1;
                fileParser = FileParser.loadFile(files.get(inputIndex));
                if(fileParser == null) return true;
                else return false;
            } catch (Exception e) {
                System.out.println("Invalid input, please try again.");
            }
        }
    }
    // displays file names
    private void printFiles(List<Path> files) {
        for (int i = 0; i < files.size(); i++){
            System.out.println("[" + (1 + i) + "]: " + files.get(i).getFileName());
        }
    }
    // gets JSON files from the list
    private List<Path> getJsonList(Stream<Path> stream) {
        return stream.filter(file -> (!Files.isDirectory(file))
                && file.getFileName().toString().contains(".json"))
                .collect(Collectors.toList());
    }
}