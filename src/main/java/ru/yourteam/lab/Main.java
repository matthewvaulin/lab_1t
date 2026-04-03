package ru.yourteam.lab;

import ru.yourteam.lab.cli.CommandProcessor;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        new CommandProcessor(new Scanner(System.in)).run();
    }
}
