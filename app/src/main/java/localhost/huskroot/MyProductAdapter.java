package localhost.huskroot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * Created by Aaron Segbedzi on 11/23/2017.
 */

class MyProductAdapter extends BaseAdapter {

    private Context mContext;
    private final String[][] products;

    MyProductAdapter(Context context, String[][] products){
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
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        if (view == null){
            list = new View(mContext);
            list = inflater.inflate(R.layout.myproduct_list_single, null);

            final ImageButton popUpButton = (ImageButton)list.findViewById(R.id.contextMenu);
            popUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popupMenu = new PopupMenu(mContext, popUpButton);
                    popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int j = menuItem.getItemId();
                            if (j == R.id.view) {
                                Intent intent = new Intent(mContext, ViewProduct.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("product_id", products[i][0]);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                                return true;
                            }else if(j == R.id.edit){
                                Intent intent = new Intent(mContext, EditProduct.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("product_id", products[i][0]);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                                return true;
                            }else if(j == R.id.delete){
                                new AlertDialog.Builder(mContext).setTitle("Confirm Delete")
                                        .setMessage("Are you sure?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                DeleteProductWorker deleteProductWorker = new DeleteProductWorker(mContext);
                                                deleteProductWorker.execute(products[i][0]);
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
                                return true;
                            }else{
                                return  onMenuItemClick(menuItem);
                            }
                        }
                    });
                    popupMenu.show();
                }
            });

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
        }else{
            list = (View)view;
        }

        return list;
    }

}
