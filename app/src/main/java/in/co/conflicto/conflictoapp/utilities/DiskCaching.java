package in.co.conflicto.conflictoapp.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import in.co.conflicto.conflictoapp.models.Post;

/**
 * Created by ansal on 5/24/17.
 */

public class DiskCaching {


    private static DiskCaching ds;
    String path;
    File cacheDir;
    private DiskCaching(){
        path = MyApplication.getInstance().getFilesDir().getPath();
        cacheDir = new File(path, "cache_dir");
        if (!cacheDir.exists())
            cacheDir.mkdir();
        for (File file:cacheDir.listFiles())
                file.delete();
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

        File f = new File(cacheDir, type);
        if(f.exists())
            f.delete();

        try {
            f.createNewFile();
            FileOutputStream fos= new FileOutputStream(f);
            ObjectOutputStream oos= new ObjectOutputStream(fos);
            oos.writeObject(posts);
            oos.close();
            fos.close();

        } catch (IOException e) {
            Utilis.exc("file", e);
        }

    }

    public List<Post> getPosts(String type){
        File f = new File(cacheDir, type);
        if(f.exists()){
            try {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                return (List<Post>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                Utilis.exc("file", e);
            }
        }else {
            return null;
        }
        return null;

    }

    public void removePosts(String type){
        File f = new File(cacheDir, type);
        if(f.exists())
            f.delete();
    }
}
