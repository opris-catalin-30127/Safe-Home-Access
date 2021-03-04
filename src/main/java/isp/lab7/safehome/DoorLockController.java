package isp.lab7.safehome;

import isp.lab7.safehome.exceptions.InvalidPinException;
import isp.lab7.safehome.exceptions.TenantAlreadyExistsException;
import isp.lab7.safehome.exceptions.TenantNotFoundException;
import isp.lab7.safehome.exceptions.TooManyAttemptsException;

import java.time.LocalDateTime;
import java.util.*;

public class DoorLockController implements ControllerInterface {

    private Map<Tenant, AccessKey> validAccess;

    private List<AccessLog> accessLogs;

    private Door door = new Door();

    private int attempts;

    public DoorLockController() {
        this.validAccess = new HashMap<>();
        this.validAccess.put(new Tenant(MASTER_TENANT_NAME), new AccessKey(MASTER_KEY));
        this.accessLogs = new ArrayList<>();
    }

    public DoorLockController(Map<Tenant, AccessKey> validAccess, List<AccessLog> accessLogs, Door door) {
        this.validAccess = validAccess;
        this.accessLogs = accessLogs;
        this.door = door;
    }

    public Map<Tenant, AccessKey> getValidAccess() {
        return validAccess;
    }

    public void setValidAccess(Map<Tenant, AccessKey> validAccess) {
        this.validAccess = validAccess;
    }

    public List<AccessLog> getAccessLogs() {
        return accessLogs;
    }

    public void setAccessLogs(List<AccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    /**
     * If master key pin is used, door will be unlocked and retries count will be reset to 0
     * If pin is correct, door shall be opened or closed depending on current state (if open will be closed, if closed will be opened)
     * If 3 consecutive attempts are made to enter pin then door is locked(until master key pin is used)
     *
     * @param pin - pin value
     * @return open  or close door if the pin is correctly
     * @throws InvalidPinException      when the pin is wrong
     * @throws TooManyAttemptsException when 3 consecutive pin entered is wrong
     */
    @Override
    public DoorStatus enterPin(String pin) throws InvalidPinException, TooManyAttemptsException {
        Tenant tenant = new Tenant();
        boolean doorStatus = validAccess.containsValue(new AccessKey(pin));
        if (!Objects.equals(tenant.getName(), MASTER_TENANT_NAME)) {
            if (!doorStatus) {
                attempts++;
                if (attempts >= 3) {
                    accessLogs.add(new AccessLog(tenant.getName(), LocalDateTime.now(), "Enter pin", door.getStatus(), "Operation finished with TooManyAttemptsException "));
                    throw new TooManyAttemptsException("Too many attempts to entered pin");
                } else {
                    accessLogs.add(new AccessLog(tenant.getName(), LocalDateTime.now(), "Enter pin", door.getStatus(), "Operation finished with InvalidPinException"));
                    throw new InvalidPinException("The pin entered is invalid");
                }
            }
            attempts = 0;
            if (door.getStatus() == DoorStatus.OPEN && doorStatus) {
                door.lockDoor();
            } else {
                door.unlockDoor();
            }
            accessLogs.add(new AccessLog(tenant.getName(), LocalDateTime.now(), "Enter pin", door.getStatus(), "Successful operation"));
            System.out.println("The pin is valid");

        } else {
            attempts = 0;
            door.unlockDoor();
            System.out.println("The entry was unlocked by master key");
            accessLogs.add(new AccessLog(tenant.getName(), LocalDateTime.now(), "Enter pin", door.getStatus(), "Successful operation"));

        }
        return door.getStatus();
    }

    /**
     * This method should add tenant in the system
     *
     * @param pin  - pin to be added in the system
     * @param name - tenant name to be added in the system
     * @throws TenantAlreadyExistsException when tenant already exists by name
     */
    @Override
    public void addTenant(String pin, String name) throws TenantAlreadyExistsException {

        Tenant tenant = new Tenant(name);
        AccessKey accessKey = new AccessKey(pin);
        if (!validAccess.containsKey(tenant)) {
            validAccess.put(tenant, accessKey);
            System.out.println("Added tenant with name: " + name);
            accessLogs.add(new AccessLog(name, LocalDateTime.now(), "Add tenant", door.getStatus(), "successful operation "));
        } else {
            accessLogs.add(new AccessLog(name, LocalDateTime.now(), "Add tenant", door.getStatus(), "Operation finished with TenantAlreadyExistsException"));
            throw new TenantAlreadyExistsException("The tenant with name: " + name + " already exists");
        }

    }

    /**
     * This method should remove tenant from system
     *
     * @param name - tenant name to be removed
     * @throws TenantNotFoundException when tenant not found
     */
    @Override
    public void removeTenant(String name) throws TenantNotFoundException {
        Tenant tenant = new Tenant(name);
        if (this.validAccess.containsKey(tenant)) {
            this.validAccess.remove(tenant);
            accessLogs.add(new AccessLog(name, LocalDateTime.now(), "Remove Tenant", door.getStatus(), "successful operation"));
            System.out.println("The tenant with name: " + name + " was removed");
        } else {
            accessLogs.add(new AccessLog(name, LocalDateTime.now(), "Remove tenant", door.getStatus(), "Operation finished with TenantNotFoundException"));
            throw new TenantNotFoundException("The tenant with name: " + name + " not found");
        }
    }
}
