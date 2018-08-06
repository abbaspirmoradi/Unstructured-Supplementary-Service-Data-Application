package ir.org.acm.controller;

import ir.org.acm.utils.UssdMethod;
import ir.org.acm.utils.UssdService;

/**
 * base Ussd operations done in this controller
 */
@UssdService
public class UssdOperationsController implements UssdOperationsInterface{


    @Override
    @UssdMethod(expression="*21*(\\d*?)#")
    public void divertTo(long phoneNumber) {

    }

    @Override
    @UssdMethod(expression = "*142*12*18*456#")
    public void transfer(long phoneNumber, long amount, String password) {
        System.out.println( " phoneNumber :"+phoneNumber+" amount: "+amount + " password: "+password);
    }
}
