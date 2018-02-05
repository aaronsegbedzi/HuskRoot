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
 * Created by Aaron Segbedzi on 11/23/2017.
 */

class ProductAdapter extends BaseAdapter {

    private Context mContext;
    private final String[][] products;

    ProductAdapter(Context context, String[][] products){
        mContext = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return (products == null) ? 0 : products.length;
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
    public View getView(final int i, View view, ViewGroup parent) {
        View list;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null){
            list = new View(mContext);
            list = inflater.inflate(R.layout.list_single, null);
            TextView rowTitle = (TextView) list.findViewById(R.id.title);
            TextView rowPrice = (TextView) list.findViewById(R.id.productPrice2);
            TextView rowLocation = (TextView) list.findViewById(R.id.location);
            TextView rowPublished = (TextView) list.findViewById(R.id.published);
            TextView rowFarmer = (TextView) list.findViewById(R.id.farmer);
            ImageView imageView = (ImageView) list.findViewById(R.id.thumbnail);
            rowTitle.setText(products[i][1]);
            rowPrice.setText(products[i][2]);
            rowLocation.setText(products[i][3]);
            rowPublished.setText(products[i][4]);
            rowFarmer.setText(products[i][6]);
            Glide.with(mContext).load(Config.remoteHost + "/assets/img/products/" + products[i][5] + "/" + products[i][0] + ".jpg").into(imageView);
        }else{list= (View)view;}
        return list;
    }

}
