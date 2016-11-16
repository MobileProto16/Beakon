package erica.beakon;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mafaldaborges on 11/7/16.
 */
public class Movement {

    String id;
    private String name;
    private String description;
    private String steps;
    private String resources;
    private ArrayList<String> users;

    public Movement() {}



    public Movement(String id, String name, String description, String steps, String resources){
        this.id = id;
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.resources = resources;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public ArrayList<String> getUsers() { return users; }

    public void setUsers(ArrayList<String> users){ this.users = users; }

    public void addUser(User user) {
        if (this.users == null) {
            setEmptyUsers();
        }
        this.users.add(user.getId());
    }

    private void setEmptyUsers() { this.setUsers(new ArrayList<String>()); }

    public void removeUser(User user) { this.users.remove(user.getId()); }
}
