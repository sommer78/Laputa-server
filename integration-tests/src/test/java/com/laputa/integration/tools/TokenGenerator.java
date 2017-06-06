package com.laputa.integration.tools;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.Redeem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 08.02.16.
 */
public class TokenGenerator {

    public static void main(String[] args) throws Exception {
        //List<String> tokens = Files.readAllLines(Paths.get("/home/doom369/Downloads/x.csv"));
        Set<String> tokens = generate(10);

        List<Redeem> redeems = new ArrayList<>(tokens.size());
        for (String token : tokens) {
            redeems.add(new Redeem(token, "SparkFun", 15000));
        }

        DBManager dbManager = new DBManager("db.properties", new BlockingIOProcessor(4, 100), true);
        dbManager.insertRedeems(redeems);
    }

    private static Set<String> generate(int amount) {
        Set<String> tokens = new HashSet<>();

        for (int i = 0; i < amount; i++ ) {
            String token = UUID.randomUUID().toString().replace("-", "");
            tokens.add(token);
            System.out.println(token);
        }

        return tokens;
    }

    private static void write(String outputPath, Set<String> tokens) throws IOException {
        Path path = Paths.get(outputPath);
        write(path, tokens);
    }

    private static void write(Path path, Set<String> tokens) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String token : tokens) {
                writer.write(token);
                writer.newLine();
            }
        }
    }

}
