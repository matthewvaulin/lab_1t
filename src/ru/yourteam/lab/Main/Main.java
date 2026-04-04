package ru.yourteam.lab.Main;

import ru.yourteam.lab.Main.cli.CommandProcessor;

import java.util.Scanner;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        new CommandProcessor(new Scanner(System.in)).run();
    }
}
