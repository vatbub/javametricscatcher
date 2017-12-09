package com.github.vatbub.javametricscatcher.server;

import com.github.vatbub.common.core.Common;
import com.github.vatbub.common.core.logging.FOKLogger;
import com.github.vatbub.javametricscatcher.server.reporters.ConfigurationManager;
import org.apache.commons.cli.*;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

public class Main {
    private static Options options;
    private static Option help;
    private static Option udpPortOption;
    private static Option tcpPortOption;
    private static Option configFileOption;

    public static void main(String[] args) throws IOException, URISyntaxException, JDOMException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Common.getInstance().setAppName("com.github.vatbub.javaMetricsCatcher.server");

        int tcpPort = -1;
        int udpPort = -1;
        File configFile;

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(getCliOptions(), args);

            if (commandLine.hasOption(getHelpOption().getOpt())) {
                printHelpMessage();
                System.exit(0);
            }

            if (!commandLine.hasOption(getTCPPortOption().getOpt())) {
                throw new IllegalArgumentException("TCP port must be specified");
            }
            tcpPort = Integer.parseInt(commandLine.getOptionValue(getTCPPortOption().getOpt()));

            if (!commandLine.hasOption(getConfigFileOption().getOpt())) {
                throw new IllegalArgumentException("Config file must be specified");
            }
            configFile = new File(commandLine.getOptionValue(getConfigFileOption().getOpt()));

            if (commandLine.hasOption(getUDPPortOption().getOpt())) {
                udpPort = Integer.parseInt(commandLine.getOptionValue(getUDPPortOption().getOpt()));
            }

            Server server = new Server(tcpPort, udpPort);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

            // ConfigurationManager.getInstance().readConfiguration(new File(Main.class.getResource("SampleConfig.xml").toURI()), server.getRegistry());
            ConfigurationManager.getInstance().readConfiguration(configFile, server.getRegistry());

        } catch (ParseException e) {
            FOKLogger.log(Main.class.getName(), Level.SEVERE, "Unable to parse the command line arguments", e);
            printHelpMessage();
            System.exit(1);
        } catch (Exception e) {
            FOKLogger.log(Main.class.getName(), Level.SEVERE, "Something went wrong while starting the server", e);
            printHelpMessage();
            System.exit(1);
        }
    }

    /**
     * Prints the help message to standard out that explains the command line args
     */
    private static void printHelpMessage() {
        new HelpFormatter().printHelp(Common.getInstance().getPathAndNameOfCurrentJar(), getCliOptions());
    }

    public static Options getCliOptions() {
        if (options == null) {
            options = new Options();

            options.addOption(getHelpOption());
            options.addOption(getUDPPortOption());
            options.addOption(getTCPPortOption());
            options.addOption(getConfigFileOption());
        }

        return options;
    }

    public static Option getHelpOption() {
        if (help == null) {
            help = new Option("h", "help", false, "Displays this text");
        }
        return help;
    }

    public static Option getUDPPortOption() {
        if (udpPortOption == null) {
            udpPortOption = new Option("udp", "udpPort", false, "Specifies the udp port to use");
        }
        return udpPortOption;
    }

    public static Option getTCPPortOption() {
        if (tcpPortOption == null) {
            tcpPortOption = new Option("tcp", "tcpPort", false, "Specifies the tcp port to use");
        }
        return tcpPortOption;
    }

    public static Option getConfigFileOption() {
        if (configFileOption == null) {
            configFileOption = new Option("config", "tcpconfigFile", false, "Specifies the location of the configuration file");
        }
        return configFileOption;
    }
}
