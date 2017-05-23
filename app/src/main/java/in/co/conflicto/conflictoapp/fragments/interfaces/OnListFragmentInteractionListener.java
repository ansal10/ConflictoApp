package in.co.conflicto.conflictoapp.fragments.interfaces;

import in.co.conflicto.conflictoapp.models.Post;

/**
 * Created by ansal on 5/24/17.
 */

public interface OnListFragmentInteractionListener {
    // TODO: Update argument type and name
    void onListFragmentInteraction(Post post);

    void onCommentActionListener(Post post);
}