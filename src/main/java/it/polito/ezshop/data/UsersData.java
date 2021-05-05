package it.polito.ezshop.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UsersData {

    private HashMap<Integer, User> users_data;
    private LinkedList<User> users_list;
    private HashMap<User, JSONObject> users_map;
    private FileReader fin;
    private FileWriter fout;
    private JSONArray usersArray;

    public UsersData(){
        JSONParser jsonParser = new JSONParser();
        this.users_list = new LinkedList<User>();
        this.users_data = new HashMap<Integer, User>();
        this.users_map = new HashMap<User, JSONObject>();

        try {
            this.fin = new FileReader("src/main/persistent_data/users.json");
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        }

        try
        {
            //Read JSON file
            Object obj = jsonParser.parse(this.fin);

            usersArray = (JSONArray) obj;

            //Iterate over employee array
            usersArray.forEach( user -> parseUserObject( (JSONObject) user ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void parseUserObject(JSONObject user){

        //Get employee first name
        String ID =  (String) user.get("id");
        Integer id = Integer.parseInt((ID));
        System.out.println("ciao andre");

        //Get employee last name
        String username = (String) user.get("username");

        //Get employee website name
        String password = (String) user.get("password");

        String role = (String) user.get("role");

        User new_user = new UserImplementation(id, username, password, role);
        this.users_data.put(id, new_user);
        this.users_map.put(new_user, user);


    }

    boolean searchForLogin(String username, String password){
        for(User user: this.users_data.values()){
            if(user.getUsername() == username){
                if(user.getPassword() == password){
                    return true;
                }
            }
        }
        return false;
    }

    User getUser(String username){
        for(User user: this.users_data.values()){
            if(user.getUsername() == username){
                return user;
            }
        }
        return null;
    }

    User getUser(Integer id){
        if(this.users_data.containsKey(id)){
            return this.users_data.get(id);
        }
        else{
            return  null;
        }
    }

    boolean addUser(User user){
        if(getUser(user.getId()) != null){
            return false;
        }
        this.users_data.put(user.getId(), user);

        JSONObject userDetails = new JSONObject();
        userDetails.put("id", user.getId().toString());
        userDetails.put("username", user.getUsername());
        userDetails.put("password", user.getPassword());
        userDetails.put("role", user.getRole());

        this.users_map.put(user, userDetails);

        this.usersArray.add(userDetails);

        try
        {
            this.fout = new FileWriter("src/main/persistent_data/users.json");
            this.fout.write(usersArray.toJSONString());
            this.fout.flush();
            this.fout.close();

        }
        catch(IOException f) {
            f.printStackTrace();
            return false;
        }
        return true;

    }

    boolean removeUser(Integer id){
        User user;
        if((user = getUser(id)) == null){
            return false;
        }
        this.users_data.remove(id);

        this.usersArray.remove(this.users_map.get(user));

        try
        {
            this.fout = new FileWriter("../persistent_data(users.json");
            this.fout.write(usersArray.toJSONString());
            this.fout.flush();
            this.fout.close();

        }
        catch(IOException f) {
            f.printStackTrace();
            return false;
        }
        return true;

    }

    LinkedList<User> getUserslist(){
        for(User user: this.users_data.values()){
            this.users_list.add(user);
        }
        return users_list;
    }



}
