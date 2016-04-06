package operations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.NineMenMorrisRules;

/**
 * Created by Datacom on 2015-12-03.
 */
public class SaveManager {
    ArrayList<NineMenMorrisRules> saves;
    int slot;
    final String SAVEFILENAME = "saves.dat";

    public SaveManager() {
        slot = -1;
        saves = new ArrayList<NineMenMorrisRules>(3);
        readSaves();
    }

    public void readSaves() {
        try {
            FileHandle file = Gdx.files.local(SAVEFILENAME);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.file()));
            saves = (ArrayList<NineMenMorrisRules>) in.readObject();
            fillSavesArray();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void saveThisGame(NineMenMorrisRules m) {
        if (slot >= 0) {

            fillSavesArray();
            System.out.println("SLOT IS: " + m.toString());
            saves.set(slot, m);
        }
    }

    private void fillSavesArray() {
        while (saves.size() < 3) {
            saves.add(new NineMenMorrisRules());
        }
    }

    public void writeSaves() {
        FileHandle file = Gdx.files.local(SAVEFILENAME);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.file()));
            out.writeObject(saves);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NineMenMorrisRules getGame(int i) {
        return saves.get(i);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
