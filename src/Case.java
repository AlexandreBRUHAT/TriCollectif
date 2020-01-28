public class Case {

    private Objet objet;
    private Agent agent;

    private int x;
    private int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        objet = null;
        agent = null;
    }

    public Objet getObjet() {
        return objet;
    }

    public void setObjet(Objet objet) {
        this.objet = objet;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
