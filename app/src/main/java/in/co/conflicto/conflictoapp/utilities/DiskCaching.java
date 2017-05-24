package in.co.conflicto.conflictoapp.utilities;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

import java.util.HashMap;
import java.util.List;

import in.co.conflicto.conflictoapp.models.Post;

/**
 * Created by ansal on 5/24/17.
 */

public class DiskCaching {


    private static DiskCaching ds;
    private DiskCaching(){

    }

    public static DiskCaching getInstance(){
        if(ds != null)
            return ds;
        else {
            ds  = new DiskCaching();
            return ds;
        }
    }

    public  void putPosts(String type, List<Post> posts){

    }

    public List<Post> getPosts(String type){
        return null;
    }

    public void removePosts(String type){
        
    }
}
