package in.co.conflicto.conflictoapp.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent;

/**
 * Created by ansal on 5/14/17.
 */

public class Post {
    public Integer id ;
    public String title ;
    public String description ;
    public String category ;
    public Boolean sharedPost ;
    public String uuid ;
    public List<String> tags ;
    public Integer likes ;
    public Integer dislikes ;
    public Integer endorse ;
    public Integer supports ;
    public Integer conflicts ;
    public Integer reports ;
    public List<String> reactions;

}
