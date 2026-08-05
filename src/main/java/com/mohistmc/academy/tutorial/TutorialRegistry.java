package com.mohistmc.academy.tutorial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.player.Player;

/**
 * 教程注册表。
 */
public class TutorialRegistry {

    private static final Map<String, ACTutorial> tutorials = new LinkedHashMap<>();

    public static void addTutorials(ACTutorial... tutorial) {
        for (ACTutorial t : tutorial) {
            if (tutorials.containsKey(t.id))
                throw new RuntimeException("Already have a tutorial with this id:" + t.id);
            tutorials.put(t.id, t);
        }
    }

    public static ACTutorial addTutorial(String string) {
        ACTutorial t = new ACTutorial(string);
        addTutorials(t);
        return t;
    }

    public static ACTutorial getTutorial(String s) {
        if (!tutorials.containsKey(s))
            throw new RuntimeException("No such a tutorial: " + s);
        return tutorials.get(s);
    }

    /** 已激活(已学)与未激活(未学)分组 */
    public static List<ACTutorial>[] groupByLearned(Player player) {
        List<ACTutorial> learned = new ArrayList<>();
        List<ACTutorial> unlearned = new ArrayList<>();
        for (ACTutorial tut : tutorials.values()) {
            if (tut.isActivated(player)) learned.add(tut);
            else unlearned.add(tut);
        }
        return new List[]{learned, unlearned};
    }

    public static Collection<ACTutorial> enumeration() {
        return List.copyOf(tutorials.values());
    }
}
