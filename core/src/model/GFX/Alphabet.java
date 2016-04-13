package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;

import data.Statics;

public class Alphabet implements Disposable{
    private final Array<Disposable> disposables;
    private float step;
    private float scale;
    private Character3D lastChar;
    private Vector3 origin;
    private float originX;

    @Override
    public void dispose() {
        for(Disposable d: disposables){
            d.dispose();
        }
    }

    Character3D A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W, X,Y,Z,Å,Ä,Ö;
    Character3D ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE;

    public Alphabet(){
        disposables = new Array<Disposable>();
    }

    public Array<ModelInstance> load3DText(String s, Vector3 o, float scale){
        Array<ModelInstance> chars = new Array<ModelInstance>(s.length());
        this.origin = o;
        originX = o.x;
        //For each character, create a corresponding model instance
        step  = 0;
        this.scale = scale;
        for(Character c: s.toCharArray()){
            ModelInstance mi = getChar(c);
            if(mi != null){
                mi.transform.setTranslation(origin.x + step,
                        origin.y,
                        origin.z);
                //Set the correct spacing between letters
                //Scale
                mi.transform.scale(scale,scale,scale);
                chars.add(mi);
                step += lastChar.spacing * scale;
            }
        }
        return chars;
    }

    private ModelInstance getChar(Character c) {
        switch (c){
            case 'A':
            case 'a':
                return loadCharacter(A, 'A', 3.1f);

            case 'B':
            case 'b':
                return loadCharacter(B, 'B');

            case 'C':
            case 'c':
                return loadCharacter(C, 'C', 2.6f);

            case 'D':
            case 'd':
                return loadCharacter(D, 'D');

            case 'E':
            case 'e':
                return loadCharacter(E, 'E');

            case 'F':
            case 'f':
                return loadCharacter(F, 'F',2.5f);

            case 'G':
            case 'g':
                return loadCharacter(G, 'G');

            case 'H':
            case 'h':
                return loadCharacter(H, 'H');

            case 'I':
            case 'i':
                return loadCharacter(I, 'I', 1.5f);

            case 'J':
            case 'j':
                return loadCharacter(J, 'J', 1.9f);

            case 'K':
            case 'k':
                return loadCharacter(K, 'K');

            case 'L':
            case 'l':
                return loadCharacter(L, 'L', 2.6f);

            case 'M':
            case 'm':
                return loadCharacter(M,  'M',3.5f);

            case 'N':
            case 'n':
                return loadCharacter(N, 'N');

            case 'O':
            case 'o':
                return loadCharacter(O,'O');

            case 'P':
            case 'p':
                return loadCharacter(P, 'P', 2.8f);

            case 'Q':
            case 'q':
                return loadCharacter(Q, 'Q');

            case 'R':
            case 'r':
                return loadCharacter(R, 'R');

            case 'S':
            case 's':
                return loadCharacter(S,'S', 2.5f);

            case 'T':
            case 't':
                return loadCharacter(T, 'T', 2.8f);

            case 'U':
            case 'u':
                return loadCharacter(U, 'U');

            case 'V':
            case 'v':
                return loadCharacter(V, 'V');

            case 'W':
            case 'w':
                return loadCharacter(W, 'W', 3.8f);

            case 'X':
            case 'x':
                return loadCharacter(X, 'X');

            case 'Y':
            case 'y':
                return loadCharacter(Y, 'Y');

            case 'Z':
            case 'z':
                return loadCharacter(Z, 'Z');

            case 'Å':
            case 'å':
                return loadCharacter(Å, 'Å');

            case 'Ä':
            case 'ä':
                return loadCharacter(Ä, 'Ä');

            case 'Ö':
            case 'ö':
                return loadCharacter(Ö, 'Ö');

            case '0':
                return loadCharacter(ZERO,'0');

            case '1':
                return loadCharacter(ONE, '1', 2.8f);

            case '2':
                return loadCharacter(TWO, '2');

            case '3':
                return loadCharacter(THREE, '3');

            case '4':
                return loadCharacter(FOUR, '4');

            case '5':
                return loadCharacter(FIVE, '5');

            case '6':
                return loadCharacter(SIX, '6');

            case '7':
                return loadCharacter(SEVEN, '7');

            case '8':
                return loadCharacter(EIGHT, '8');

            case '9':
                return loadCharacter(NINE, '9');

            case ' ':
                step += 3f * scale;
                return null;

            case '\n':
                //Create new line
                origin.y -= 3f * scale;
                //reset X
                step = 0;
                //origin.x = originX;
                return null;

            default:
                step += 3f * scale;
                return null;
        }
    }

    private ModelInstance loadCharacter(Character3D c, char l, float space) {
        if(c == null){
            c = new Character3D(l,Statics.ALPHA_PATH + l + ".g3dj", space);
            lastChar = c;
        }
        return c.getModelInstance();
    }

    private ModelInstance loadCharacter(Character3D c, char l) {
        return loadCharacter(c, l, 3f);
    }

    public class Character3D{
        Model mod;
        char letter;
        float spacing;
        public Character3D(char l, String path, float spacing){
            //System.out.println("new character " + l);
            this.spacing = spacing;
            letter = l;
            mod = create3DModel(path);
            mod.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));

            disposables.add(mod);
        }

        public ModelInstance getModelInstance(){
            return new ModelInstance(mod);
        }
    }

    public Model create3DModel(String modelPath){
        //Load
        ModelLoader modelLoader = new G3dModelLoader(new JsonReader());
        return  modelLoader.loadModel(Gdx.files.internal(modelPath));
    }

}
