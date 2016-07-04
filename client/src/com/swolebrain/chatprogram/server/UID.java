
package com.swolebrain.chatprogram.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UID {
    private static List<Integer> ids = new ArrayList<>();
    private static int currentID = 0;
    private static final int RANGE = 10000;
    
    static {
        for (int i = 0; i < RANGE; i++){
            ids.add(i);
        }
        Collections.shuffle(ids);
    }
    
    private UID(){
    }
    
    public static int getID(){
        if (currentID > ids.size()-1){
            for (int i = ids.size(); i < ids.size()+RANGE; i++){
                ids.add(i);
            }
        }
        return ids.get(currentID++).intValue();
    }
}
