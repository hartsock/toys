package com.github.hartsock.toys;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConcordanceTest {

    private Concordance concordance;

    @Before
    public void setUp() throws Exception {
        concordance = new Concordance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() {
        concordance.setUrl("http://www.gutenberg.org/files/84/84-0.txt");
        concordance.run();
        Assert.assertEquals(647, concordance.count("you"));
        Assert.assertEquals(1, concordance.count("yon"));
        Assert.assertEquals(12, concordance.count("doubt"));
        Assert.assertEquals(0, concordance.count("doubtish"));
    }
}