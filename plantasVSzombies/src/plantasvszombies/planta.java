/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plantasvszombies;

/**
 *
 * @author Francis
 */
public class planta extends bicho
{
    private int coste;

    public planta()
    {
        super();
        coste = 0;
    }

    public planta(String nom, int ataq, int defen, int cost)
    {
        super(nom, ataq, defen);
        coste = cost;
    }

    public int getCoste()
    {
        return coste;
    }

}
