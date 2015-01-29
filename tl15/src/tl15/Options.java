package tl15;

import java.util.ArrayList;

/**
 * A class to handle command line arguments.
 */
public class Options {
    private final String runCommand;
    private final ArrayList<Option> options = new ArrayList<>();

    /**
     * 
     * @param runCommand The name of the executable as seen in usage() output.
     */
    public Options(String runCommand) {
        this.runCommand = runCommand;
    }
    
    /**
     * Add an option which takes an argument.
     * @see #addFlag(java.lang.String, java.lang.Character, java.lang.String) 
     * @param name
     * @param shortName
     * @param description 
     */
    public void addOption(String name, Character shortName, String description) {
        options.add(new Option(name, shortName, true, description));
    }

    /**
     * Add an option which does not take an argument.
     * @see #addOption(java.lang.String, java.lang.Character, java.lang.String) 
     * @param name
     * @param shortName
     * @param description 
     */
    public void addFlag(String name, Character shortName, String description) {
        options.add(new Option(name, shortName, false, description));
    }

    public void usage() {
        System.out.println("Usage: " + runCommand + " arguments");
        System.out.println("Available arguments:");
        for (Option opt : options) {
            System.out.print("  ");
            if (opt.shortName != null) {
                System.out.print("-" + opt.shortName + ""
            }
            System.out.print(opt.name + " " + opt.argName + " :  " + opt.description);
        }
    }
}
