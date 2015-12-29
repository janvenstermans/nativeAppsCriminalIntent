package com.bignerdranch.android.criminalintent;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class CriminalIntentGenerator {
    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.bignerdranch.android.criminalintent.model");

        Entity redditPost = schema.addEntity("Crime");
        redditPost.addLongProperty("id").primaryKey();
        redditPost.addStringProperty("title");
        redditPost.addDateProperty("date");
        redditPost.addBooleanProperty("solved");
        redditPost.addStringProperty("suspect");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }
}
