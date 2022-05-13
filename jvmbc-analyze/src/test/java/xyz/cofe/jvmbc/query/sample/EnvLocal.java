package xyz.cofe.jvmbc.query.sample;

import xyz.cofe.iter.Eterable;

import java.util.ArrayList;
import java.util.List;

public class EnvLocal implements IEnv {
    private List<User> users;
    {
        users = new ArrayList<>();
        var lastNames = List.of("Petrov","Sidorov","Ivanov");
        var firstNames = List.of("Boris","Ivan","Egor");
        for( var ln : lastNames ){
            for( var fn : firstNames ){
                users.add(new User(
                    fn+" "+ln, fn.toLowerCase()+"-"+ln.toLowerCase()+"@gmail.com"
                ));
            }
        }
    }
    @Override
    public Eterable<User> getUsers(){
        return Eterable.of(users);
    }
}
