package de.myronx.hbupdater.client;

import de.myronx.hbupdater.config.HBPackUpdaterConfig;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.text.Text;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.Files;
import java.security.MessageDigest;

public class HBPackUpdaterClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("hb-updater");
    private static boolean updateSuccessful = false;

    @Override
    public void onInitializeClient() {
        HBPackUpdaterConfig config = HBPackUpdaterConfig.getInstance();
        config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (updateSuccessful && client.player != null) {
                client.player.sendMessage(Text.of("§6§lH§e§lB-§6§lU§e§lpdater §8» §aRessourcenpaket erfolgreich runtergeladen/aktualisiert."), false);
                updateSuccessful = false;
            }
        });

        if (config.enableUpdates) {
            downloadPack();
        }
    }

    public static void downloadPack() {
        downloadResourcePack("HorstBlocks");
    }

    public static void downloadResourcePack(String pack) {
        try {
            URL checksumUrl = new URL("https://resource.horstblocks.de/checksum.php");
            BufferedReader reader = new BufferedReader(new InputStreamReader(checksumUrl.openStream()));
            String serverChecksum = reader.readLine();

            File packFile = new File("resourcepacks/" + pack + ".zip");

            if (!packFile.exists()) {
                LOGGER.info(pack + " Ressourcenpaket konnte nicht gefunden werden. Downloading...");
                downloadFile(new URL("https://horst.to/resource/" + pack + ".zip"), packFile.getAbsolutePath());
                LOGGER.info(pack + " Ressourcenpaket erfolgreich runtergeladen.");
                updateSuccessful = true;
            } else {
                String localChecksum = calculateSHA1(packFile);
                if (!localChecksum.equalsIgnoreCase(serverChecksum)) {
                    LOGGER.info(pack + " Ressourcenpaket ist nicht aktuell. Downloading Update...");
                    if (packFile.delete()) {
                        downloadFile(new URL("https://horst.to/resource/" + pack + ".zip"), packFile.getAbsolutePath());
                        LOGGER.info(pack + " Ressourcenpaket Update erfolgreich.");
                        updateSuccessful = true;
                    } else {
                        LOGGER.warn(pack + " Ressourcenpaket Update war nicht erfolgreich, bitte entferne zuerst das Ressourcenpaket aus den ausgewählten Ressourcenpaketen!");
                    }
                } else {
                    LOGGER.info(pack + " Ressourcenpaket ist aktuell.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Download oder Update ist fehlgeschlagen vom " + pack + " Ressourcenpaket!");
            e.printStackTrace();
        }
    }

    public static void downloadFile(URL url, String fileName) throws Exception {
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String calculateSHA1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        return Hex.encodeHexString(bytes);
    }
}
