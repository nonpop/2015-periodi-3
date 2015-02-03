package utils;

/**
 * One option. An option consists of:
 * 1. An option string. The option is activated by a string of the form 
 *    "-option" in the command line arguments.
 * 2a. A flag indicating whether the option takes an argument.
 * 2b. The name of the argument if any.
 * 2c. The type of the argument if any.
 * 2d. The default value for the argument.
 * 3. A description of the option.
 */
public class Option {
    public final String option;
    public final boolean takesArgument;
    public final String argName;
    public final boolean integer;
    public final String defValue;
    public final String description;

    public String value = null;

    public Option(String option,
                  boolean takesArgument,
                  String argName,
                  boolean integer,
                  String defValue,
                  String description)
    {
        this.option = option;
        this.takesArgument = takesArgument;
        this.argName = argName;
        this.integer = integer;
        this.defValue = defValue;
        this.description = description;
    }
}
