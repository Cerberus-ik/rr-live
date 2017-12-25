package net.cerberus.gtad.common;

import java.util.TreeMap;

public class TimeStep {

    public int id;
    public long timeStepSize;
    public TreeMap<Role, TreeMap<Integer, Integer>> usageOfRunes = new TreeMap<>();
    public String patch;
}
