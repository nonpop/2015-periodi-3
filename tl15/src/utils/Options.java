package utils;

/**
 * A class to handle command line arguments.
 */
public class Options {
    private final String runCommand;
    List<Pair<String, Option>> options = new List<>();

    /**
     * 
     * @param runCommand The name of the executable as seen in usage() output.
     */
    public Options(String runCommand) {
        this.runCommand = runCommand;
    }
    
    /**
     * Add an option which takes a string argument.
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
        options.add(new Pair<>(name, new Option(option, true, argName, false, defValue, description)));
    }

    /**
     * Add an option which takes an integer argument.
     * @param defValue
     * @see #addFlag(java.lang.String, java.lang.Character, java.lang.String) 
     * @param name
     * @param option
     * @param argName
     * @param description 
     */
    public void addOption(String name, String option, String argName, int defValue, String description) {
        assert(option != null);
        assert(name != null);
        assert(argName != null);
        assert(description != null);
        options.add(new Pair<>(name, new Option(option, true, argName, true, "" + defValue, description)));
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
        options.add(new Pair<>(name, new Option(option, false, null, false, null, description)));
    }

    public void usage() {
        System.out.println("Usage: " + runCommand + " *arguments*");
        System.out.println("Available arguments:");
        int longest = 0;
        for (Pair<String, Option> kv : options) {
            Option opt = kv.second;
            if (opt.takesArgument) {
                longest = Math.max(longest, (opt.option + " " + opt.argName).length());
            } else {
                longest = Math.max(longest, opt.option.length());
            }
        }
        String fmt = "  -%-" + longest + "s  :  %s";
        for (Pair<String, Option> kv : options) {
            Option opt = kv.second;
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

    private Option getOptionByOptionString(String arg) {
        for (Pair<String, Option> kv : options) {
            if (kv.second.option.equals(arg)) {
                return kv.second;
            }
        }
        return null;
    }

    private Option getOptionByName(String name) {
        for (Pair<String, Option> kv : options) {
            if (kv.first.equals(name)) {
                return kv.second;
            }
        }
        return null;
    }
    
    public boolean parse(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            Option opt = getOptionByOptionString(args[i].substring(1));
            if (opt == null || args[i].charAt(0) != '-') {
                System.out.println("Unknown option: " + args[i]);
                return false;
            }

            if (!opt.takesArgument) {
                opt.value = "non-null";
            } else {
                if (i + 1 >= args.length) {
                    System.out.println("Option -" + opt.option + " needs an argument");
                    return false;
                }
                opt.value = args[++i];
                if (opt.integer) {
                    try {
                        Integer.parseInt(opt.value);
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Option -" + opt.option + " needs an integer argument but was given \"" + opt.value + "\"");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getOptionString(String name) {
        Option opt = getOptionByName(name);
        if (opt == null) {
            throw new IllegalArgumentException("no such option: " + name);
        }
        if (!opt.takesArgument) {
            throw new IllegalArgumentException("option " + name + " does not take an argument");
        }
        if (opt.value == null) {
            return opt.defValue;
        } else {
            return opt.value;
        }
    }

    public int getOptionInteger(String name) {
        Option opt = getOptionByName(name);
        if (opt == null) {
            throw new IllegalArgumentException("no such option: " + name);
        }
        if (!opt.takesArgument) {
            throw new IllegalArgumentException("option " + name + " does not take an argument");
        }
        if (!opt.integer) {
            throw new IllegalArgumentException("option " + name + " is not an integer");
        }
        if (opt.value == null) {
            return Integer.parseInt(opt.defValue);
        } else {
            return Integer.parseInt(opt.value);
        }
    }

    public boolean getFlagState(String name) {
        // TODO: remove copy-paste
        for (Pair<String, Option> kv : options) {
            if (kv.first.equals(name)) {
                Option opt = kv.second;
                if (opt.takesArgument) {
                    throw new IllegalArgumentException("option " + name + " is not a flag");
                }
                return opt.value != null;
            }
        }
        throw new IllegalArgumentException("no such option: " + name);
    }

    private String formatValue(Option opt, String s) {
        if (s == null) {
            return "(null)";
        }

        String fmt = "";
        if (!opt.integer) {
            fmt += "\"";
        }
        fmt += s;
        if (!opt.integer) {
            fmt += "\"";
        }
        return fmt;
    }
    
    private String formatValue(Option opt) {
        if (!opt.takesArgument) {
            return (opt.value != null)? "true" : "false";
        }
        if (opt.value == null) {
            return formatValue(opt, opt.defValue);
        }
        String fmt = formatValue(opt, opt.value);
        if (opt.defValue != null) {
            fmt += " [default: " + formatValue(opt, opt.defValue) + "]";
        }
        return fmt;
    }

    public void dump() {
        for (Pair<String, Option> kv : options) {
            System.out.println(kv.first + " = " + formatValue(kv.second));
        }
    }
}
