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

    static Character3D A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W, X,Y,Z,SA,SAA,SO;
    static Character3D ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE;

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
                if(A == null){
                    System.out.println("Creating character: " + c);
                    A = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj", 3.1f);
                }
                lastChar = A;
                return A.getModelInstance();
                //return loadCharacter(A, 'A', 3.1f);
            case 'B':
            case 'b':
                if(B == null){
                    System.out.println("Creating character: " + c);
                    B = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = B;
                return B.getModelInstance();
                //return loadCharacter(B, 'B');

            case 'C':
            case 'c':
                if(C == null){
                    System.out.println("Creating character: " + c);
                    C = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.6f);
                }
                lastChar = C;
                return C.getModelInstance();
                //return loadCharacter(C, 'C', 2.6f);

            case 'D':
            case 'd':
                if(D == null){
                    System.out.println("Creating character: " + c);
                    D = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.6f);
                }
                lastChar = D;
                return D.getModelInstance();
                //return loadCharacter(D, 'D');

            case 'E':
            case 'e':
                if(E == null){
                    System.out.println("Creating character: " + c);
                    E = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = E;
                return E.getModelInstance();
                //return loadCharacter(E, 'E');

            case 'F':
            case 'f':
                if(F == null){
                    System.out.println("Creating character: " + c);
                    F = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.5f);
                }
                lastChar = F;
                return F.getModelInstance();
                //return loadCharacter(F, 'F',2.5f);

            case 'G':
            case 'g':
                if(G == null){
                    System.out.println("Creating character: " + c);
                    G = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = G;
                return G.getModelInstance();
                //return loadCharacter(G, 'G');

            case 'H':
            case 'h':
                if(H == null){
                    System.out.println("Creating character: " + c);
                    H = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = H;
                return H.getModelInstance();
                //return loadCharacter(H, 'H');

            case 'I':
            case 'i':
                if(I == null){
                    System.out.println("Creating character: " + c);
                    I = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.0f);
                }
                lastChar = I;
                return I.getModelInstance();
                //return loadCharacter(I, 'I', 1.5f);

            case 'J':
            case 'j':
                if(J == null){
                    System.out.println("Creating character: " + c);
                    J = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",1.9f);
                }
                lastChar = J;
                return J.getModelInstance();
//                return loadCharacter(J, 'J', 1.9f);

            case 'K':
            case 'k':
                if(K == null){
                    System.out.println("Creating character: " + c);
                    K = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = K;
                return K.getModelInstance();
                //return loadCharacter(K, 'K');

            case 'L':
            case 'l':
                if(L == null){
                    System.out.println("Creating character: " + c);
                    L = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.6f);
                }
                lastChar = L;
                return L.getModelInstance();
                //return loadCharacter(L, 'L', 2.6f);

            case 'M':
            case 'm':
                if(M == null){
                    System.out.println("Creating character: " + c);
                    M = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.5f);
                }
                lastChar = M;
                return M.getModelInstance();
                //return loadCharacter(M,  'M',3.5f);

            case 'N':
            case 'n':
                if(N == null){
                    System.out.println("Creating character: " + c);
                    N = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = N;
                return N.getModelInstance();
                //return loadCharacter(N, 'N');

            case 'O':
            case 'o':
                if(O == null){
                    System.out.println("Creating character: " + c);
                    O = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = O;
                return O.getModelInstance();
                //return loadCharacter(O,'O');

            case 'P':
            case 'p':
                if(P == null){
                    System.out.println("Creating character: " + c);
                    P = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.8f);
                }
                lastChar = P;
                return P.getModelInstance();
                //return loadCharacter(P, 'P', 2.8f);

            case 'Q':
            case 'q':
                if(Q == null){
                    System.out.println("Creating character: " + c);
                    Q = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = Q;
                return Q.getModelInstance();
                //return loadCharacter(Q, 'Q');

            case 'R':
            case 'r':
                if(R == null){
                    System.out.println("Creating character: " + c);
                    R = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = R;
                return R.getModelInstance();
                //return loadCharacter(R, 'R');

            case 'S':
            case 's':
                if(S == null){
                    System.out.println("Creating character: " + c);
                    S = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.5f);
                }
                lastChar = S;
                return S.getModelInstance();
                //return loadCharacter(S,'S', 2.5f);

            case 'T':
            case 't':
                if(T == null){
                    System.out.println("Creating character: " + c);
                    T = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",2.8f);
                }
                lastChar = T;
                return T.getModelInstance();
                //return loadCharacter(T, 'T', 2.8f);

            case 'U':
            case 'u':
                if(U == null){
                    System.out.println("Creating character: " + c);
                    U = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = U;
                return U.getModelInstance();
                //return loadCharacter(U, 'U');

            case 'V':
            case 'v':
                if(V == null){
                    System.out.println("Creating character: " + c);
                    V = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = V;
                return V.getModelInstance();
                //return loadCharacter(V, 'V');

            case 'W':
            case 'w':
                if(W == null){
                    System.out.println("Creating character: " + c);
                    W = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.8f);
                }
                lastChar = W;
                return W.getModelInstance();
                //return loadCharacter(W, 'W', 3.8f);

            case 'X':
            case 'x':
                if(X == null){
                    System.out.println("Creating character: " + c);
                    X = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = X;
                return X.getModelInstance();
                //return loadCharacter(X, 'X');

            case 'Y':
            case 'y':
                if(Y == null){
                    System.out.println("Creating character: " + c);
                    Y = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = Y;
                return Y.getModelInstance();
                //return loadCharacter(Y, 'Y');

            case 'Z':
            case 'z':
                if(Z == null){
                    System.out.println("Creating character: " + c);
                    Z = new Character3D(c,Statics.ALPHA_PATH + c.toUpperCase(c) + ".g3dj",3.0f);
                }
                lastChar = Z;
                return Z.getModelInstance();
                //return loadCharacter(Z, 'Z');

            case 'Å':
            case 'å':
                if(SA == null){
                    System.out.println("Creating character: " + c);
                    SA = new Character3D(c,Statics.ALPHA_PATH + "SA" + ".g3dj",3.0f);
                }
                lastChar = SA;
                return SA.getModelInstance();
                //return loadCharacter(Å, 'Å');

            case 'Ä':
            case 'ä':
                if(SAA == null){
                    System.out.println("Creating character: " + c);
                    SAA = new Character3D(c,Statics.ALPHA_PATH + "SAA" + ".g3dj",3.0f);
                }
                lastChar = SAA;
                return SAA.getModelInstance();
                //return loadCharacter(Ä, 'Ä');

            case 'Ö':
            case 'ö':
                if(SO == null){
                    System.out.println("Creating character: " + c);
                    SO = new Character3D(c,Statics.ALPHA_PATH + "SO" + ".g3dj",3.0f);
                }
                lastChar = SO;
                return SO.getModelInstance();
                //return loadCharacter(Ö, 'Ö');

            case '0':
                if(ZERO == null){
                    System.out.println("Creating character: " + c);
                    ZERO = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = ZERO;
                return ZERO.getModelInstance();
                //return loadCharacter(ZERO,'0');

            case '1':
                if(ONE == null){
                    System.out.println("Creating character: " + c);
                    ONE = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",2.8f);
                }
                lastChar = ONE;
                return ONE.getModelInstance();
                //return loadCharacter(ONE, '1', 2.8f);

            case '2':
                if(TWO == null){
                    System.out.println("Creating character: " + c);
                    TWO = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = TWO;
                return TWO.getModelInstance();
                //return loadCharacter(TWO, '2');

            case '3':
                if(THREE == null){
                    System.out.println("Creating character: " + c);
                    THREE = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = THREE;
                return THREE.getModelInstance();
                //return loadCharacter(THREE, '3');

            case '4':
                if(FOUR == null){
                    System.out.println("Creating character: " + c);
                    FOUR = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = FOUR;
                return FOUR.getModelInstance();
                //return loadCharacter(FOUR, '4');

            case '5':
                if(FIVE == null){
                    System.out.println("Creating character: " + c);
                    FIVE = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = FIVE;
                return FIVE.getModelInstance();
                //return loadCharacter(FIVE, '5');

            case '6':
                if(SIX == null){
                    System.out.println("Creating character: " + c);
                    SIX = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = SIX;
                return SIX.getModelInstance();
                //return loadCharacter(SIX, '6');

            case '7':
                if(SEVEN == null){
                    System.out.println("Creating character: " + c);
                    SEVEN = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = SEVEN;
                return SEVEN.getModelInstance();
                //return loadCharacter(SEVEN, '7');

            case '8':
                if(EIGHT == null){
                    System.out.println("Creating character: " + c);
                    EIGHT = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = EIGHT;
                return EIGHT.getModelInstance();
                //return loadCharacter(EIGHT, '8');

            case '9':
                if(NINE == null){
                    System.out.println("Creating character: " + c);
                    NINE = new Character3D(c,Statics.ALPHA_PATH + c + ".g3dj",3.0f);
                }
                lastChar = NINE;
                return NINE.getModelInstance();
                //return loadCharacter(NINE, '9');

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
            System.out.println("Creating character: " + l);
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
