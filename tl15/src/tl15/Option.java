package tl15;

/**
 * One option. An option consists of:
 * 1. A name. The option is activated by a string of the form "--name" in the
 *    command line arguments.
 * 2. An optional short name, which is one character. A string of the form 
 *    "-shortName" is equivalent to "--name". Combining "-a -b -c" into "-abc"
 *    is supported as long as none of the combined options takes any arguments.
 * 3. A flag indicating whether the option takes an argument.
 * 4. A description of the option.
 */
public class Option {
    public final String name;
    public final Character shortName;
    public final boolean takesArgument;
    public final String argName;
    public final String description;

    public Option(String name,
                  Character shortName,
                  boolean takesArgument,
                  String argName,
                  String description)
    {
        assert(name != null);
        assert(description != null);

        this.name = name;
        this.shortName = shortName;
        this.takesArgument = takesArgument;
        this.argName = argName;
        this.description = description;
    }
}
