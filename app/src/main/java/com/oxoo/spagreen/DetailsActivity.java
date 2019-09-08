package com.oxoo.spagreen;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.oxoo.spagreen.adapters.CommentsAdapter;
import com.oxoo.spagreen.adapters.DirectorApater;
import com.oxoo.spagreen.adapters.EpisodeAdapter;
import com.oxoo.spagreen.adapters.HomePageAdapter;
import com.oxoo.spagreen.adapters.LiveTvHomeAdapter;
import com.oxoo.spagreen.adapters.ServerApater;
import com.oxoo.spagreen.models.CommentsModel;
import com.oxoo.spagreen.models.CommonModels;
import com.oxoo.spagreen.models.EpiModel;
import com.oxoo.spagreen.utils.ApiResources;
import com.oxoo.spagreen.utils.ToastMsg;
import com.oxoo.spagreen.utils.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static android.view.View.GONE;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvName,tvDirector,tvRelease,tvCast,tvDes,tvGenre,tvRelated;


    private RecyclerView rvDirector,rvServer,rvRelated,rvComment;

    public static RelativeLayout lPlay;


    private DirectorApater directorApater;
    private ServerApater serverApater;
    private EpisodeAdapter episodeAdapter;
    private HomePageAdapter relatedAdapter;
    private LiveTvHomeAdapter relatedTvAdapter;


    private List<CommonModels> listDirector =new ArrayList<>();
    private List<CommonModels> listEpisode =new ArrayList<>();
    private List<CommonModels> listRelated =new ArrayList<>();
    private List<CommentsModel> listComment =new ArrayList<>();


    private String strDirector="",strCast="",strGenre="";
    public static LinearLayout llBottom,llBottomParent,llcomment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String type="",id="";

    private ImageView imgAddFav;

    private ImageView imgBack;
    public static VideoView videoView;

    public static MediaController mediacontroller;

    private String V_URL = "";
    public static WebView webView;
    public static ProgressBar progressBar;
    private boolean isFav = false;


    private ShimmerFrameLayout shimmerFrameLayout;

    private Button btnComment;
    private EditText etComment;
    private CommentsAdapter commentsAdapter;

    private String commentURl;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        adView=findViewById(R.id.adView);
        llBottom=findViewById(R.id.llbottom);
        tvDes=findViewById(R.id.tv_details);
        tvCast=findViewById(R.id.tv_cast);
        tvRelease=findViewById(R.id.tv_release_date);
        tvName=findViewById(R.id.text_name);
        tvDirector=findViewById(R.id.tv_director);
        tvGenre=findViewById(R.id.tv_genre);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
        imgAddFav=findViewById(R.id.add_fav);
        imgBack=findViewById(R.id.img_back);
        webView=findViewById(R.id.webView);
        progressBar=findViewById(R.id.progressBar);
        llBottomParent=findViewById(R.id.llbottomparent);
        lPlay=findViewById(R.id.play);
        rvRelated=findViewById(R.id.rv_related);
        tvRelated=findViewById(R.id.tv_related);
        shimmerFrameLayout=findViewById(R.id.shimmer_view_container);
        btnComment=findViewById(R.id.btn_comment);
        etComment=findViewById(R.id.et_comment);
        rvComment=findViewById(R.id.recyclerView_comment);
        llcomment=findViewById(R.id.llcomments);

        shimmerFrameLayout.startShimmer();



        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        videoView=findViewById(R.id.videoView);
        rvServer=findViewById(R.id.rv_server_list);


        type = getIntent().getStringExtra("vType");
        id = getIntent().getStringExtra("id");


        final SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);

        if (preferences.getBoolean("status",false)){
            imgAddFav.setVisibility(View.VISIBLE);
        }else {
            imgAddFav.setVisibility(GONE);
        }


        commentsAdapter=new CommentsAdapter(this,listComment);
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        rvComment.setHasFixedSize(true);
        rvComment.setNestedScrollingEnabled(false);
        rvComment.setAdapter(commentsAdapter);

        commentURl = new ApiResources().getCommentsURL().concat("&&id=").concat(id);

        getComments(commentURl);



        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!preferences.getBoolean("status",false)){
                    startActivity(new Intent(DetailsActivity.this,LoginActivity.class));
                    new ToastMsg(DetailsActivity.this).toastIconError("you need to login first !");
                }

                else if (etComment.getText().toString().equals("")){

                    new ToastMsg(DetailsActivity.this).toastIconError("Comment can't empty..");

                }else {

                    String commentUrl = new ApiResources().getAddComment()
                            .concat("&&videos_id=")
                            .concat(id).concat("&&user_id=")
                            .concat(preferences.getString("id","0"))
                            .concat("&&comment=").concat(etComment.getText().toString());


                    addComment(commentUrl);

                }

            }
        });

        imgAddFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String url = new ApiResources().getAddFav()+"&&user_id="+preferences.getString("id","0")+"&&videos_id="+id;

                if (isFav){
                    String removeURL = new ApiResources().getRemoveFav()+"&&user_id="+preferences.getString("id","0")+"&&videos_id="+id;
                    removeFromFav(removeURL);
                }else {
                    addToFav(url);
                }
            }
        });


        if (!isNetworkAvailable()){
            new ToastMsg(DetailsActivity.this).toastIconError("There is no internet connection");
        }

        initGetData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initGetData();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_full_ad_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                Random rand = new Random();
                int i = rand.nextInt(10)+1;

                if (i%2==0){
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

            }
        });



    }


    private void initGetData(){

        if (!type.equals("tv")){

            //----related rv----------
            relatedAdapter=new HomePageAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedAdapter);

            if (type.equals("tvseries")){

                rvRelated.removeAllViews();
                listRelated.clear();
                rvServer.removeAllViews();
                listDirector.clear();
                listEpisode.clear();

                episodeAdapter=new EpisodeAdapter(this,listDirector,listDirector);
                rvServer.setLayoutManager(new LinearLayoutManager(this));
                rvServer.setHasFixedSize(true);
                rvServer.setAdapter(episodeAdapter);
                getSeriesData(type,id);
            }else {
                rvServer.removeAllViews();
                listDirector.clear();
                rvRelated.removeAllViews();
                listRelated.clear();

                serverApater=new ServerApater(this,listDirector);
                rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                rvServer.setHasFixedSize(true);
                rvServer.setAdapter(serverApater);
                getData(type,id);

                final ServerApater.OriginalViewHolder[] viewHolder = {null};
                serverApater.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                        iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                        serverApater.chanColor(viewHolder[0],position);
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder[0] =holder;
                    }
                });
            }

            SharedPreferences sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);
            String url = new ApiResources().getFavStatusURl()+"&&user_id="+sharedPreferences.getString("id","0")+"&&videos_id="+id;

            if (sharedPreferences.getBoolean("status",false)){
                getFavStatus(url);
            }

        }else {

            llcomment.setVisibility(GONE);

            tvRelated.setText("All TV :");

            rvServer.removeAllViews();
            listDirector.clear();
            rvRelated.removeAllViews();
            listRelated.clear();

            //----related rv----------
            relatedTvAdapter=new LiveTvHomeAdapter(this,listRelated);
            rvRelated.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvRelated.setHasFixedSize(true);
            rvRelated.setAdapter(relatedTvAdapter);



            imgAddFav.setVisibility(GONE);



            serverApater=new ServerApater(this,listDirector);
            rvServer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            rvServer.setHasFixedSize(true);
            rvServer.setAdapter(serverApater);
            getTvData(type,id);
            llBottom.setVisibility(GONE);

            final ServerApater.OriginalViewHolder[] viewHolder = {null};
            serverApater.setOnItemClickListener(new ServerApater.OnItemClickListener() {
                @Override
                public void onItemClick(View view, CommonModels obj, int position, ServerApater.OriginalViewHolder holder) {
                    iniMoviePlayer(obj.getStremURL(),obj.getServerType(),DetailsActivity.this);

                    serverApater.chanColor(viewHolder[0],position);
                    holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder[0] =holder;
                }
            });


        }
    }


    private void initWeb(String s){

        progressBar.setVisibility(GONE);

        webView.loadUrl(s);
        webView.setVisibility(View.VISIBLE);
        videoView.setVisibility(GONE);
    }


    public void iniMoviePlayer(String url,String type,Context context){

        if (type.equals("embed") || type.equals("vimeo") || type.equals("youtube") || type.equals("gdrive")){
            initWeb(url);
        }else {
            initVideoPlayer(url,context);
        }
    }

    public void initVideoPlayer(String url,Context context){

        webView.setVisibility(GONE);
        videoView.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.VISIBLE);

        mediacontroller = new CustomMediaController(context,new DetailsActivity());
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                progressBar.setVisibility(GONE);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        videoView.setMediaController(mediacontroller);
                        mediacontroller.setAnchorView(videoView);
                    }
                });
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                progressBar.setVisibility(GONE);
                return false;
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //mp.release();
            }
        });

    }


    private void addToFav(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.getString("message"));
                        isFav=true;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                    }else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                    }

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError("Something went wrong ! try later");
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }

    private void getTvData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;
        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);

                try {
                    tvName.setText(response.getString("tv_name"));
                    tvDes.setText(response.getString("description"));
                    V_URL=response.getString("stream_url");


                    CommonModels model=new CommonModels();
                    model.setTitle("HD");
                    model.setStremURL(V_URL);
                    model.setServerType(response.getString("stream_from"));
                    listDirector.add(model);



                    JSONArray jsonArray = response.getJSONArray("all_tv_channel");
                    for (int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        CommonModels models =new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster_url"));
                        models.setTitle(jsonObject.getString("tv_name"));
                        models.setVideoType("tv");
                        models.setId(jsonObject.getString("live_tv_id"));
                        listRelated.add(models);

                    }
                    relatedTvAdapter.notifyDataSetChanged();



                    JSONArray serverArray = response.getJSONArray("additional_media_source");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("url"));
                        models.setServerType(jsonObject.getString("source"));


                        listDirector.add(models);
                    }
                    serverApater.notifyDataSetChanged();


                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void getSeriesData(String vtype,String vId){

        String type = "&&type="+vtype;
        String id = "&id="+vId;
        String url = new ApiResources().getDetails()+type+id;

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                try {
                    tvName.setText(response.getString("title"));
                    tvRelease.setText("Release On "+response.getString("release"));
                    tvDes.setText(response.getString("description"));

                    //----director---------------
                    JSONArray directorArray = response.getJSONArray("director");
                    for (int i = 0;i<directorArray.length();i++){
                        JSONObject jsonObject=directorArray.getJSONObject(i);
                        if (i==directorArray.length()-1){
                            strDirector=strDirector+jsonObject.getString("name");
                        }else {
                            strDirector=strDirector+jsonObject.getString("name")+",";
                        }
                    }
                    tvDirector.setText(strDirector);


                    //----cast---------------
                    JSONArray castArray = response.getJSONArray("cast");
                    for (int i = 0;i<castArray.length();i++){
                        JSONObject jsonObject=castArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strCast=strCast+jsonObject.getString("name");
                        }else {
                            strCast=strCast+jsonObject.getString("name")+",";
                        }
                    }
                    tvCast.setText(strCast);


                    //---genre---------------
                    JSONArray genreArray = response.getJSONArray("genre");
                    for (int i = 0;i<genreArray.length();i++){
                        JSONObject jsonObject=genreArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strGenre=strGenre+jsonObject.getString("name");
                        }else {
                            strGenre=strGenre+jsonObject.getString("name")+",";
                        }
                    }
                    tvGenre.setText(strGenre);

                    //----realted post---------------
                    JSONArray relatedArray = response.getJSONArray("related_tvseries");
                    for (int i = 0;i<relatedArray.length();i++){
                        JSONObject jsonObject=relatedArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("title"));
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setId(jsonObject.getString("videos_id"));
                        models.setVideoType("tvseries");

                        listRelated.add(models);
                    }
                    relatedAdapter.notifyDataSetChanged();



                    //----episode------------
                    JSONArray mainArray = response.getJSONArray("season");
                    ArrayList<EpiModel> epList=new ArrayList<>();

                    for (int i = 0;i<mainArray.length();i++){
                        //epList.clear();

                        JSONObject jsonObject=mainArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        String season_name=jsonObject.getString("seasons_name");
                        models.setTitle(jsonObject.getString("seasons_name"));

                        JSONArray episodeArray=jsonObject.getJSONArray("episodes");

                        for (int j=0;j<episodeArray.length();j++){

                            JSONObject object =episodeArray.getJSONObject(j);

                            EpiModel model=new EpiModel();
                            model.setSeson(season_name);
                            model.setEpi(object.getString("episodes_name"));
                            model.setStreamURL(object.getString("file_url"));
                            model.setServerType(object.getString("file_type"));
                            epList.add(model);
                        }
                        models.setEpiModels(epList);
                        listDirector.add(models);

                    }
                    episodeAdapter=new EpisodeAdapter(DetailsActivity.this,listDirector,listDirector);
                    rvServer.setAdapter(episodeAdapter);
                    episodeAdapter.notifyDataSetChanged();

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }


    private void getData(String vtype,String vId){


        String type = "&&type="+vtype;
        String id = "&id="+vId;


        String url = new ApiResources().getDetails()+type+id;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    tvName.setText(response.getString("title"));
                    tvRelease.setText("Release On "+response.getString("release"));
                    tvDes.setText(response.getString("description"));

                    //----director---------------
                    JSONArray directorArray = response.getJSONArray("director");
                    for (int i = 0;i<directorArray.length();i++){
                        JSONObject jsonObject=directorArray.getJSONObject(i);
                        if (i==directorArray.length()-1){
                            strDirector=strDirector+jsonObject.getString("name");
                        }else {
                            strDirector=strDirector+jsonObject.getString("name")+",";
                        }
                    }

                    tvDirector.setText(strDirector);

                    //----cast---------------
                    JSONArray castArray = response.getJSONArray("cast");
                    for (int i = 0;i<castArray.length();i++){
                        JSONObject jsonObject=castArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strCast=strCast+jsonObject.getString("name");
                        }else {
                            strCast=strCast+jsonObject.getString("name")+",";
                        }
                    }
                    tvCast.setText(strCast);


                    //---genre---------------
                    JSONArray genreArray = response.getJSONArray("genre");
                    for (int i = 0;i<genreArray.length();i++){
                        JSONObject jsonObject=genreArray.getJSONObject(i);
                        if (i==castArray.length()-1){
                            strGenre=strGenre+jsonObject.getString("name");
                        }else {
                            strGenre=strGenre+jsonObject.getString("name")+",";
                        }
                    }
                    tvGenre.setText(strGenre);

                    //----server---------------
                    JSONArray serverArray = response.getJSONArray("videos");
                    for (int i = 0;i<serverArray.length();i++){
                        JSONObject jsonObject=serverArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("label"));
                        models.setStremURL(jsonObject.getString("file_url"));
                        models.setServerType(jsonObject.getString("file_type"));

                        if (jsonObject.getString("file_type").equals("mp4")){
                            V_URL=jsonObject.getString("file_url");
                        }


                        listDirector.add(models);
                    }
                    serverApater.notifyDataSetChanged();

                    //----realted post---------------
                    JSONArray relatedArray = response.getJSONArray("related_movie");
                    for (int i = 0;i<relatedArray.length();i++){
                        JSONObject jsonObject=relatedArray.getJSONObject(i);

                        CommonModels models=new CommonModels();
                        models.setTitle(jsonObject.getString("title"));
                        models.setImageUrl(jsonObject.getString("thumbnail_url"));
                        models.setId(jsonObject.getString("videos_id"));
                        models.setVideoType("movie");

                        listRelated.add(models);
                    }
                    relatedAdapter.notifyDataSetChanged();

                }catch (Exception e){

                }finally {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);


    }


    private void getFavStatus(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        isFav=true;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                        imgAddFav.setVisibility(View.VISIBLE);
                    }else {
                        isFav=false;
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_border_24);
                        imgAddFav.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }

    private void removeFromFav(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getString("status").equals("success")){
                        isFav=false;
                        new ToastMsg(DetailsActivity.this).toastIconSuccess(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_border_24);
                    }else {
                        isFav=true;
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                        imgAddFav.setBackgroundResource(R.drawable.outline_favorite_24);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError("unable to fetch data");
            }
        });

        new VolleySingleton(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }



    public class CustomMediaController extends MediaController {

        private ImageButton fullScreen;
        private boolean isFullScreen=false;
        private Activity activity;
        private Context context;

        public CustomMediaController(Context context,Activity activity) {
            super(context);
            this.activity=activity;
            this.context=context;
        }

        @Override
        public void setAnchorView(final View view) {

            super.setAnchorView(view);

            //image button for full screen to be added to media controller
            fullScreen = new ImageButton (super.getContext());

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);


            params.gravity = Gravity.RIGHT;
            params.rightMargin = 20;
            params.topMargin=30;
            addView(fullScreen, params);

            if(isFullScreen){
                fullScreen.setImageResource(R.drawable.ic_fullscreen);
            }else{
                fullScreen.setImageResource(R.drawable.ic_fullscreen);
            }

            //add listener to image button to handle full screen and exit full screen events
            fullScreen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isFullScreen){
                        isFullScreen=false;
                        llBottomParent.setVisibility(VISIBLE);
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                    }else{

                        isFullScreen=true;
                        llBottomParent.setVisibility(GONE);
                        //llcomment.setVisibility(GONE);
                        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    }
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void addComment(String url){


        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equals("success")){

                        rvComment.removeAllViews();
                        listComment.clear();
                        getComments(commentURl);
                        etComment.setText("");

                    }else {
                        new ToastMsg(DetailsActivity.this).toastIconError(response.getString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(DetailsActivity.this).toastIconError("can't comment now ! try later");
            }
        });

        VolleySingleton.getInstance(DetailsActivity.this).addToRequestQueue(jsonObjectRequest);

    }


    private void getComments(String url){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i =0;i<response.length();i++){

                    try {

                        JSONObject jsonObject=response.getJSONObject(i);

                        CommentsModel model=new CommentsModel();

                        model.setName(jsonObject.getString("user_name"));
                        model.setImage(jsonObject.getString("user_img_url"));
                        model.setComment(jsonObject.getString("comments"));
                        model.setId(jsonObject.getString("comments_id"));

                        listComment.add(model);

                        commentsAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(DetailsActivity.this).addToRequestQueue(jsonArrayRequest);

    }

}
