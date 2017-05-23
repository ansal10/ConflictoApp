package in.co.conflicto.conflictoapp.fragments.interfaces;

import in.co.conflicto.conflictoapp.models.Comment;

/**
 * Created by ansal on 5/24/17.
 */

public interface DialogBoxListenerInterface{
    public void commentUpdated(Comment comment);
    public void commentDeleted(Comment comment);
}