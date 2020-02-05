import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Agent extends Thread {

    public Agent() {
        memoire = new LinkedList<>();
    }

    // CARACTERISTIQUES

    private Objet porte;
    private LinkedList<String> memoire;

    private void addMemoire(String type) {
        if (memoire.size() >= 10) {
            memoire.push(type);
            memoire.poll();
        } else {
            memoire.push(type);
        }
    }

    private void addMemoireMouvement(List<String> types) {
        types.forEach(s -> addMemoire(s));
    }

    // ACTIONS

    private void seDeplacer() {
        Environnement env = Environnement.getInstance();
        Random random = new Random();

        int dir = random.nextInt(4);
        String direction;

        switch (dir) {
            case 0: direction = "N";
                break;
            case 1: direction = "S";
                break;
            case 2: direction = "E";
                break;
            default: direction = "O";
                break;
        }

        addMemoireMouvement(env.seDeplacer(direction, this));
    }

    private void prendre() {
        Environnement env = Environnement.getInstance();

        porte = env.prendre(this);
    }

    private void deposer() {
        Environnement env = Environnement.getInstance();

        porte = env.deposer(this, porte);
    }

    // PERCEPTIONS

    private Case caseActuelle() {
        Environnement env = Environnement.getInstance();

        return env.getCase(this);
    }

    private List<Case> casesVoisines() {
        Environnement env = Environnement.getInstance();

        return env.getVoisins(this);
    }

    // Probabilités

    private boolean doitPrendre(String type, List<Case> voisins) {
        Environnement env = Environnement.getInstance();
        Random random = new Random();

        float f = 0;
        for (Case c : voisins) {
            if (c.getObjet() != null && c.getObjet().getType() == type)
                ++f;
        }

        f /= voisins.size();
        float proba = (env.getkPlus() / (env.getkPlus() + f)) * (env.getkPlus() / (env.getkPlus() + f));
        float randValue = random.nextFloat();

        return randValue < proba;
    }

    private boolean doitDeposer(String type, List<Case> voisins) {
        Environnement env = Environnement.getInstance();
        Random random = new Random();

        float f = 0;
        for (Case c : voisins) {
            if (c.getObjet() != null && c.getObjet().getType() == type)
                ++f;
        }

        f /= voisins.size();
        float proba = (f / (env.getkMoins() + f)) * (f / (env.getkMoins() + f));
        float randValue = random.nextFloat();

        return randValue < proba;
    }

    private String reconnaissanceObjet(String typeInitial) {
        Environnement env = Environnement.getInstance();
        Random random = new Random();

        String autreType;
        if (typeInitial.equals("A")) {
            autreType = "B";
        } else {
            autreType = "A";
        }

        float proba = (nbTypeEnMemoire(typeInitial) + nbTypeEnMemoire(autreType) * env.getErreur()) / memoire.size();

        float randValue = random.nextFloat();
        if (randValue < proba) {
            return autreType;
        } else {
            return typeInitial;
        }
    }

    private int nbTypeEnMemoire(String type) {
        int counter = 0;
        for (String s: memoire) {
            if (s.equals(type))
                ++counter;
        }

        return counter;
    }

    // COMPORTEMENT

    @Override
    public void run() {

        Environnement env = Environnement.getInstance();
        int iter = 0;

        while (iter < env.getNbIter()) {
            // Perceptions :

            Case caseActuelle = caseActuelle();
            List<Case> voisins = casesVoisines();

            // Actions

            if (porte == null) {
                // Pas d'objet sur mon dos.
                if (caseActuelle.getObjet() != null && doitPrendre(reconnaissanceObjet(caseActuelle.getObjet().getType()), voisins)) {
                    prendre();
                }
            } else {
                // Je porte déjà un objet (sur mon dos).
                if (caseActuelle.getObjet() == null && doitDeposer(reconnaissanceObjet(porte.getType()), voisins)) {
                    deposer();
                }
            }

            seDeplacer();

            ++iter;
        }
    }
}
