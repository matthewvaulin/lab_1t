package main.java;

import main.java.cli.CommandProcessor;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        new CommandProcessor(new Scanner(System.in)).run();
    }
}
