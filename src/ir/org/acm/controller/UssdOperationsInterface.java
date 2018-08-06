package ir.org.acm.controller;

import ir.org.acm.utils.UssdMethod;

/**
 *
 */
public interface UssdOperationsInterface {


    public void divertTo(long phoneNumber);

    public void transfer(long phoneNumber,long amount, String password);

}
