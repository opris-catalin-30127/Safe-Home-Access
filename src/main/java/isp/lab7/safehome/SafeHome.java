package isp.lab7.safehome;

import isp.lab7.safehome.exceptions.InvalidPinException;
import isp.lab7.safehome.exceptions.TenantAlreadyExistsException;
import isp.lab7.safehome.exceptions.TenantNotFoundException;
import isp.lab7.safehome.exceptions.TooManyAttemptsException;

public class SafeHome {

    public static void main(String[] args) {
        DoorLockController doorLockController = new DoorLockController();

        System.out.println("ATTEMPTS TO ADD TENANTS:");
        //Add tenants and some exception
        try {
            doorLockController.addTenant("1234", "Kevin");
            doorLockController.addTenant("1122", "Jack");
            doorLockController.addTenant("1010", "Jack");
        } catch (TenantAlreadyExistsException e) {
            System.out.println("Error: Tenant with same name cannot be added");
        } finally {
            try {
                doorLockController.addTenant("9081", "Anderson");
                doorLockController.addTenant("9081", "Eva");
            } catch (TenantAlreadyExistsException e) {
                System.out.println("Error: Tenant with same name cannot be added");
            }
        }

        System.out.println("\nATTEMPTS TO ENTER PIN:");
        //Enter some pins
        try {
            doorLockController.enterPin("1234");
            doorLockController.enterPin("9081");
            doorLockController.enterPin("2345");
        } catch (InvalidPinException e) {
            System.out.println("Error: The pin is invalid");
        } catch (TooManyAttemptsException e) {
            System.out.println("Error: too many attempts");
        }
        try {
            //second pin invalid
            doorLockController.enterPin("1765");
        } catch (InvalidPinException e) {
            System.out.println("Error: The pin is invalid");
        } catch (TooManyAttemptsException e) {

        }
        try {
            //third pin invalid
            doorLockController.enterPin("1345");
        } catch (InvalidPinException e) {

        } catch (TooManyAttemptsException e) {
            System.out.println("Error: too many attempts");
        } finally {
            try {
                //master key unlock
                doorLockController.enterPin(ControllerInterface.MASTER_KEY);
            } catch (InvalidPinException e) {
                System.out.println("Error: The pin is invalid");
            } catch (TooManyAttemptsException e) {
                System.out.println("Error: too many attempts");
            }
        }

        System.out.println("\nATTEMPTS TO REMOVE TENANT: ");
        //remove tenant and then verify exception
        try {
            doorLockController.removeTenant("Eva");
        } catch (TenantNotFoundException e) {
            System.out.println("Error: Tenant not found");
        } finally {
            try {
                doorLockController.removeTenant("Eva");
            } catch (TenantNotFoundException e) {
                System.out.println("Error: Tenant not found");
            }
        }

        System.out.println("\nACCESS LOG: ");
        //added an list for all attempts
        for (AccessLog accessLog : doorLockController.getAccessLogs()) {
            System.out.println(accessLog.toString());
        }

    }
}

