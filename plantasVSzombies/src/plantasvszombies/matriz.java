/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plantasvszombies;

/**
 *
 * @author Francis
 */
public class matriz
{
    private double[][] datos;
    private int filas, columnas;

    public matriz(int fil, int col)
    {
        filas = fil;
        columnas = col;
        datos = new double[filas][columnas];
    }

    public int getFilas()
    {
        return filas;
    }

    public int getColumnas()
    {
        return columnas;
    }

    public void setElemento(int fil, int col, double elemento)
    {
        datos[fil][col] = elemento;
    }

    public double getElemento(int fil, int col)
    {
        return datos[fil][col];
    }
}
