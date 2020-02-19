/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plantasvszombies;

/**
 *
 * @author Francis
 */
public class bicho
{
    private int ataque, defensa;
    private String nombre;

    public bicho()
    {
        ataque = defensa = 0;
        nombre = "";
    }

    public bicho(String nom, int ataq, int defen)
    {
        nombre = nom;
        ataque = ataq;
        defensa = defen;
    }

    public String getNombre()
    {
        return nombre;
    }

    public int getAtaque()
    {
        return ataque;
    }

    public int getDefensa()
    {
        return defensa;
    }

    public void setAtaque(int ata)
    {
        ataque = ata;
    }

    public void setDefensa(int defen)
    {
        defensa = defen;
    }

    public void setNombre(String n)
    {
        nombre = n;
    }
}
