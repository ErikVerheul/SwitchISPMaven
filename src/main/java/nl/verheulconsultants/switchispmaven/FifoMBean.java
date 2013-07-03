/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

/**
 *
 * @author Erik
 */
public interface FifoMBean {

    public void setSize(int size);

    public int getSize();

    public String showOutput();
}
