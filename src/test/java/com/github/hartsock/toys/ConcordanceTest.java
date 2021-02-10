package com.github.hartsock.toys;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class ConcordanceTest {

    private Concordance concordance;

    @Before
    public void setUp() throws Exception {
        concordance = new Concordance();
        final URL fileUrl = getClass().getClassLoader().getResource("frankenstein.txt");
        Assert.assertNotNull(fileUrl);
        concordance.setUrl(fileUrl.toString());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() {
        concordance.run();
        Assert.assertEquals(556, concordance.count("you"));
        Assert.assertEquals(1, concordance.count("yon"));
        Assert.assertEquals(12, concordance.count("doubt"));
        Assert.assertEquals(0, concordance.count("doubtish"));
    }
}