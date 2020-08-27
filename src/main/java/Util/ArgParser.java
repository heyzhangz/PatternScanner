package Util;

import org.apache.commons.cli.*;

public class ArgParser {
    public String apkDir;
    public String resultJsonPath;
    public String sdkName;
    public int threadsNumber = 32;

    public void parse(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .build());

        options.addOption(Option.builder("d")
                .longOpt("dir")
                .hasArg()
                .argName("apk dir")
                .desc("the dir contains a lot of apk")
                .build());
        options.addOption(Option.builder("r")
                .longOpt("res")
                .hasArg()
                .argName("result report")
                .desc("apk scan result json path dir")
                .build());

        options.addOption(Option.builder("j")
                .longOpt("threads")
                .hasArg()
                .argName("threads number")
                .desc("fixthread pools number")
                .build());

        options.addOption(Option.builder("n")
                .longOpt("sdkname")
                .hasArg()
                .argName("target sdk name")
                .desc("you can input like com.google.gms.xxxx")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine result = null;

        try {
            result = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (result.hasOption("h")) {
            helpFormatter.printHelp("ArgParse", options, true);
            System.exit(0);
        }
        if (!result.hasOption("r") || !result.hasOption("d")) {
            System.out.println("you should input the apk directory and res json store path");
            System.exit(0);
        }
        if(result.hasOption("d")) {
            apkDir = result.getOptionValue("d");
        }
        if(result.hasOption("r")) {
            resultJsonPath = result.getOptionValue("r");
        }

        if (result.hasOption("j")) {
            threadsNumber = Integer.parseInt(result.getOptionValue("j"));
        }

        if (result.hasOption("n")) {
            sdkName = result.getOptionValue("n");
        }
    }
}
