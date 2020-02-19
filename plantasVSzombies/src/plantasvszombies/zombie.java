/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plantasvszombies;

/**
 *
 * @author Francis
 */
public class zombie extends bicho
{
    public zombie()
    {
        super();
    }

    public zombie(String nom, int ataq, int defen)
    {
        super(nom, ataq, defen);
    }

    public zombie(zombie z)
    {
        setNombre(z.getNombre());
        setAtaque(z.getAtaque());
        setDefensa(z.getDefensa());
    }
}
