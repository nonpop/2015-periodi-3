package tl15;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * A class to handle command line arguments.
 */
public class Options {
    private final String runCommand;
    ArrayList<Entry<String, Option>> options = new ArrayList<>();

    /**
     * 
     * @param runCommand The name of the executable as seen in usage() output.
     */
    public Options(String runCommand) {
        this.runCommand = runCommand;
    }
    
    /**
     * Add an option which takes an argument.
     * @param defValue
     * @see #addFlag(java.lang.String, java.lang.Character, java.lang.String) 
     * @param name
     * @param option
     * @param argName
     * @param description 
     */
    public void addOption(String name, String option, String argName, String defValue, String description) {
        assert(option != null);
        assert(name != null);
        assert(argName != null);
        assert(description != null);
        options.add(new SimpleEntry<>(name, new Option(option, true, argName, defValue, description)));
    }

    /**
     * Add an option which does not take an argument.
     * @see #addOption(java.lang.String, java.lang.Character, java.lang.String) 
     * @param name
     * @param option
     * @param description 
     */
    public void addFlag(String name, String option, String description) {
        assert(option != null);
        assert(name != null);
        assert(description != null);
        options.add(new SimpleEntry<>(name, new Option(option, false, null, null, description)));
    }

    public void usage() {
        System.out.println("Usage: " + runCommand + " *arguments*");
        System.out.println("Available arguments:");
        int longest = 0;
        for (Entry<String, Option> kv : options) {
            Option opt = kv.getValue();
            if (opt.takesArgument) {
                longest = Math.max(longest, (opt.option + " " + opt.argName).length());
            } else {
                longest = Math.max(longest, opt.option.length());
            }
        }
        String fmt = "  -%-" + longest + "s  :  %s";
        for (Entry<String, Option> kv : options) {
            Option opt = kv.getValue();
            String name = opt.option;
            if (opt.takesArgument) {
                name += " " + opt.argName;
            }
            System.out.printf(fmt, name, opt.description);
            if (opt.defValue != null) {
                System.out.print(" [default: " + opt.defValue + "]");
            }
            System.out.println();
        }
    }
}
