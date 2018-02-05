package localhost.huskroot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by Aaron Segbedzi on 11/22/2017.
 */

public class SubSubCategoryAdapter extends BaseAdapter {

    private Context mContext;
    private final String[][] subSubCategories;

    public SubSubCategoryAdapter(Context context, String[][] subSubCategories){
        mContext = context;
        this.subSubCategories = subSubCategories;
    }

    @Override
    public int getCount() {
        return (subSubCategories == null) ? 0 :  subSubCategories.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View grid;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        if (view == null){
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView)grid.findViewById(R.id.grid_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
            TextView gridCount = (TextView)grid.findViewById(R.id.grid_count);

            textView.setText(subSubCategories[i][1]);
            gridCount.setText(subSubCategories[i][2] + " CROP(S)");
            Glide.with(mContext).load(Config.remoteHost + "/assets/img/subsubcategories/" + subSubCategories[i][1] + ".jpg").into(imageView);
        }else{
            grid = (View)view;
        }
        return grid;
    }
}
