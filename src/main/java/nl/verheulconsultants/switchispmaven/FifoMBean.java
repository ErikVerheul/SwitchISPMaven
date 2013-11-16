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

    void setSize(int size);

    int getSize();

    String showOutput();
}
