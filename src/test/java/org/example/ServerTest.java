package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

public class ServerTest {

    Server testServer = new Server();

    @Test
    public void checkSettingPort() throws IOException {

        File setting = new File("src/test/resources/testSetting.txt");
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(setting), 200);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int expected = 9999;
        int actual = testServer.settingPort(bis);

        Assertions.assertEquals(expected, actual);
    }
}
