package br.com.leinadlarama.diadobatecabeca;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.leinadlarama.diadobatecabeca.helper.Constants;
import br.com.leinadlarama.diadobatecabeca.helper.DataHolder;
import br.com.leinadlarama.diadobatecabeca.model.Comment;
import br.com.leinadlarama.diadobatecabeca.model.User;

import static android.content.ContentValues.TAG;

public class ViewEventActivity extends BaseActivity {

    private CollapsingToolbarLayout header;
    private TextView text_body;
    private Context context;
    private ImageView img;
    private ImageView ivFlyer;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ShareActionProvider mShareActionProvider;
    private DatabaseReference mCommentsReference;
    private EditText mCommentField;
    private RecyclerView mCommentsRecycler;
    private CommentAdapter mAdapter;
    private Button btPostComment;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        header = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        img = (ImageView) findViewById(R.id.img);

        text_body = (TextView) findViewById(R.id.text_body);
        text_body.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        shareInfo();
                        return true;
                    }
                }
        );
        text_body.setText(mountEventBodymessage());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shareInfo();
                //Snackbar.make(view, "Issae! Quanto mais cabeças para bater melhor!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                addToFavourite();
            }
        });

        setTitle(DataHolder.getInstance().getEventSelected().getNomeBanda());

        context = this;
        loadBanner();

        ivFlyer = (ImageView) findViewById(R.id.ivFlyer);

        if (DataHolder.getInstance().getEventSelected().getImage() != null &&
                !"".equals(DataHolder.getInstance().getEventSelected().getImage())) {

            ivFlyer.setVisibility(View.VISIBLE);

            Picasso.with(context)
                    .load(DataHolder.getInstance().getEventSelected().getImage())
                    .placeholder(R.drawable.banner)
                    .resize(300, 500)
                    .into(ivFlyer);

            // Hook up clicks on the thumbnail views.
            ivFlyer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(ivFlyer, ivFlyer.getDrawable());
                }
            });

            // Retrieve and cache the system's default "short" animation time.
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        } else {
            ivFlyer.setVisibility(View.GONE);
        }


        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        btPostComment = (Button) findViewById(R.id.btPostComment);
        btPostComment.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postComment();
                    }
                }
        );

        mCommentField = (EditText) findViewById(R.id.etComment);
        mCommentField.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_NULL
                                && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            if (!"".equals(mCommentField)) {
                                postComment();
                            }
                            return true;

                        } else {
                            return false;
                        }
                    }
                }
        );

        if (DataHolder.getInstance().getEventSelected().getNomeBanda() != null) {
            String nomeBandaTratado = DataHolder.getInstance().getEventSelected().getNomeBanda()
                    .replace(".", "")
                    .replace("#", "")
                    .replace("$", "")
                    .replace("[", "")
                    .replace("]", "");

            mCommentsReference = FirebaseDatabase.getInstance().getReference()
                    .child("post-comments").child(nomeBandaTratado);
        } else {
            mCommentsReference = FirebaseDatabase.getInstance().getReference()
                    .child("post-comments").child("erro");
        }

    }


    private String mountEventBodymessage() {
        StringBuffer bodyMessage = new StringBuffer();
        bodyMessage.append("Informações:\n ")
                .append(DataHolder.getInstance().getEventSelected()
                        .getInfoComplementar()
                        .replaceAll("(?i)Data:", "\n\nData: ")
                        .replaceAll("(?i)Endereço:", "\n\nEndereço:\n\n")
                        .replaceAll("(?i)Local:", "\n\nLocal: ")
                        .replaceAll("(?i)Formas", "\n\nFormas")
                        .replaceAll("(?i)PONTOS DE", "\n\nPontos de")
                        .replaceAll("(?i)Ingressos: ", "\n\nIngressos:\n")
                        .replaceAll("(?i)SETOR", "\n\nSetor:")
                        .replaceAll("(?i)Informações: ", "\n\nInformações:\n")
                ).append("\n\n");

        return bodyMessage.toString();
    }


    private void loadBanner() {

        try {
            URL thumb_u = new URL(DataHolder.getInstance().getEventSelected().getFotoBanda());
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            header.setBackground(thumb_d);
        } catch (Exception e) {
            e.printStackTrace();
        }


        header.setBackground(DataHolder.getInstance().getImageView().getDrawable());

    }

    private void addToFavourite() {
        if (mAuth.getCurrentUser() != null) {
            mDatabase.child(Constants.COLLECTION_FAVOURITES)
                    .child(mAuth.getCurrentUser()
                            .getUid())
                    .child(DataHolder.getInstance().getEventSelected().getNomeBanda())
                    .setValue(DataHolder.getInstance().getEventSelected());

            String nomeBandaTratado = StringUtils.capitalize(StringUtils.lowerCase(DataHolder.getInstance().getEventSelected().getNomeBanda()));
            Snackbar.make(findViewById(R.id.viewRoot), "Favoritado com sucesso '" + nomeBandaTratado + "' foi!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            Snackbar.make(findViewById(R.id.viewRoot), "Opa! Antes de favoritar é necessário logar!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                shareInfo();
                return true;
            default:
                break;
        }
        return false;
    }


    private void shareInfo() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.msg_share_part1) + DataHolder.getInstance().getEventSelected().getNomeBanda());

        String msgShare = getString(R.string.msg_share_part1) + DataHolder.getInstance().getEventSelected().getNomeBanda() + "\n\n" + mountEventBodymessage();

        if (DataHolder.getInstance().getEventSelected().getImage() != null &&
                !"".equals(DataHolder.getInstance().getEventSelected().getImage())) {
            msgShare += "\n" + DataHolder.getInstance().getEventSelected().getImage();
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msgShare);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.msg_share)));
    }


    private void zoomImageFromThumb(final View thumbView, Drawable imageResDrawable) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        Picasso.with(context)
                .load(DataHolder.getInstance().getEventSelected().getImage())
                .placeholder(R.drawable.banner)
                .resize(1000, 1200)
                .into(expandedImageView);


        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.viewRoot)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            return "NAO_LOGADO";
        }
    }

    private void postComment() {
        if (DataHolder.getInstance().getEventSelected().getNomeBanda() != null) {
            //final String uid = DataHolder.getInstance().getVideoDocumentSelected().getVideoId();

            final String uid = getUid();

            if ("NAO_LOGADO".equals(uid)) {
                Toast.makeText(this, R.string.msg_login_necessario_para_postar, Toast.LENGTH_SHORT).show();
            } else {
                FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user information
                                User user = dataSnapshot.getValue(User.class);
                                String authorName = user.username;

                                // Create new comment object
                                String commentText = mCommentField.getText().toString();
                                Comment comment = new Comment(uid, authorName, commentText);

                                // Push the comment, it will appear in the list
                                mCommentsReference.push().setValue(comment);

                                // Clear the field
                                mCommentField.setText(null);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }


    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.comment_author);
            bodyView = (TextView) itemView.findViewById(R.id.comment_body);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;


            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }



        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }

}
