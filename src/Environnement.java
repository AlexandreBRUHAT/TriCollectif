import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Environnement {

    private ArrayList<Case> grille;
    private HashMap<Agent, Case> agentPosition;

    private int N, M;

    private int nbIter;
    private float erreur;

    private int i;
    float kPlus, kMoins;

    private static Environnement instance;

    public static Environnement getInstance() {
        return instance;
    }

    public static void createInstance(int n, int m, int nbAgents, int nbA, int nbB, int i, float kPlus, float kMoins, int nbIter, float erreur, int tailleMemoireAgent) {
        instance = new Environnement(n, m, nbAgents, nbA, nbB, i, kPlus, kMoins, nbIter, erreur, tailleMemoireAgent);

        instance.lancer();
    }

    private Environnement(int n, int m, int nbAgents, int nbA, int nbB, int i, float kPlus, float kMoins, int nbIter, float erreur, int tailleMemoireAgent) {

        this.N = n;
        this.M = m;

        this.nbIter = nbIter;
        this.erreur = erreur;

        this.i = i;
        this.kPlus = kPlus;
        this.kMoins = kMoins;

        grille = new ArrayList<>();
        agentPosition = new HashMap<>();

        for (int k = 0; k < n; ++k) {
            for (int j = 0; j < m; ++j) {
                grille.add(new Case(k, j));
            }
        }

        Random random = new Random();
        int randomValue;

        for (int k = 0; k < nbA; ++k) {
            do {
                randomValue = random.nextInt(grille.size());
            } while (grille.get(randomValue).getObjet() != null);

            grille.get(randomValue).setObjet(new Objet("A"));
        }

        for (int k = 0; k < nbB; ++k) {
            do {
                randomValue = random.nextInt(grille.size());
            } while (grille.get(randomValue).getObjet() != null);

            grille.get(randomValue).setObjet(new Objet("B"));
        }

        Agent agent;

        for (int k = 0; k < nbAgents; ++k) {
            do {
                randomValue = random.nextInt(grille.size());
            } while (grille.get(randomValue).getAgent() != null);

            agent = new Agent(tailleMemoireAgent);
            grille.get(randomValue).setAgent(agent);
            agentPosition.put(agent, grille.get(randomValue));
        }

        afficher();
    }

    private void lancer() {
        for (Agent agent: agentPosition.keySet()) {
            agent.start();
        }
        try {
            for (Agent agent: agentPosition.keySet()) {
                agent.join();
            }
        } catch (Exception e) {

        }

        afficher();
    }

    // GETTER

    public int getI() {
        return i;
    }

    public float getkPlus() {
        return kPlus;
    }

    public float getkMoins() {
        return kMoins;
    }

    public int getNbIter() {
        return nbIter;
    }

    public float getErreur() {
        return erreur;
    }

    // Retour des PERCEPTIONS

    public Case getCase(Agent agent) {
        return agentPosition.get(agent);
    }

    public List<Case> getVoisins(Agent agent) {

        ArrayList<Case> voisins = new ArrayList<>();
        Case caseAgent = agentPosition.get(agent);

        // Case Nord
        voisins.addAll(getVoisinsUnitaire(-1, 0, caseAgent));

        // Case Sud
        voisins.addAll(getVoisinsUnitaire(1, 0, caseAgent));


        // Case Est
        voisins.addAll(getVoisinsUnitaire(0, 1, caseAgent));


        // Case Ouest
        voisins.addAll(getVoisinsUnitaire(0, -1, caseAgent));


        return voisins;
    }

    // Actions sur l'environnement

    public List<String> seDeplacer(String direction, Agent agent) {

        List<String> memoires = new ArrayList<>();
        String s;
        for (int k = 0; k < i; ++k) {
            s = seDeplacerUnitaire(direction, agent);
            if (s == null)
                return memoires;

            memoires.add(s);
        }

        return  memoires;
    }

    public Objet prendre(Agent agent) {
        Case caseAgent = agentPosition.get(agent);

        Objet objet = caseAgent.getObjet();

        caseAgent.setObjet(null);

        return objet;
    }

    public Objet deposer(Agent agent, Objet objet) {
        Case caseAgent = agentPosition.get(agent);

        if (caseAgent.getObjet() == null) {
            caseAgent.setObjet(objet);

            return null;
        } else {
            return objet;
        }
    }

    // Utilitaires

    private String seDeplacerUnitaire(String direction, Agent agent) {
        Case caseAgent = agentPosition.get(agent);

        if (direction.equals("N") && caseAgent.getX() > 0 && grille.get((caseAgent.getX() - 1) * N + caseAgent.getY()).getAgent() == null) {

            setAgentCase(caseAgent,  grille.get((caseAgent.getX() - 1) * N + caseAgent.getY()), agent);

        } else if (direction.equals("S") && caseAgent.getX() < N-1 && grille.get((caseAgent.getX()+1) * N + caseAgent.getY()).getAgent() == null) {

            setAgentCase(caseAgent,  grille.get((caseAgent.getX() + 1) * N + caseAgent.getY()), agent);

        } else if (direction.equals("E") && caseAgent.getY() < M-1 && grille.get((caseAgent.getX()) * N + caseAgent.getY() + 1).getAgent() == null) {

            setAgentCase(caseAgent,  grille.get((caseAgent.getX()) * N + caseAgent.getY() + 1), agent);

        } else  if (direction.equals("O") && caseAgent.getY() > 0 && grille.get((caseAgent.getX()) * N + caseAgent.getY() - 1).getAgent() == null) {

            setAgentCase(caseAgent,  grille.get((caseAgent.getX()) * N + caseAgent.getY() - 1), agent);

        } else {
            return null;
        }

        Objet objet = agentPosition.get(agent).getObjet();
        return (objet != null ? objet.getType() : "");
    }

    private List<Case> getVoisinsUnitaire(int diffX, int diffY, Case caseOrigine) {
        List<Case> cases = new ArrayList<>();

        for (int k = 0; k < i; ++k) {

            int newX = caseOrigine.getX() + diffX;
            int newY = caseOrigine.getY() + diffY;

            if (newX < 0 || newX > N-1 || newY < 0 || newY > M-1) {
                return cases;
            }
            caseOrigine = grille.get(newX * N + newY);

            cases.add(caseOrigine);
        }

        return cases;
    }

    private void setAgentCase(Case oldCase,Case newCase, Agent agent) {
        newCase.setAgent(agent);
        oldCase.setAgent(null);
        agentPosition.replace(agent,  newCase);
    }


    // AFFICHAGE

    public void afficher() {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.setContentPane(panel);

        GridLayout gridLayout = new GridLayout(N, M);
        panel.setLayout(gridLayout);

        for(Case c: grille) {
            if (c.getAgent() != null)
                panel.add(new JLabel("C"));
            else if (c.getObjet() != null)
                panel.add(new JLabel(c.getObjet().getType()));
            else
                panel.add(new JLabel(""));
        }

        frame.setSize(600, 600);
        frame.setVisible(true);
    }
}
