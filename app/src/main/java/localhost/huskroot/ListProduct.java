package localhost.huskroot;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ListProduct extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String[][] action = {{"subSubCategory",bundle.getString("subSubCategory_id")}};
            ProductListWorker productListWorker = new ProductListWorker(this);
            productListWorker.execute(action);
            getSupportActionBar().setTitle("List Crops - " + bundle.getString("subSubCategory_name"));
        }else{
            String[][] action = {{"all"}};
            ProductListWorker productListWorker = new ProductListWorker(this);
            productListWorker.execute(action);
            getSupportActionBar().setTitle("Husk Root - Crops");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_explore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(this, Dashboard.class));
                return true;
            case R.id.refresh:
                finish(); startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setProducts(final String[][] products){
        ProductAdapter productAdapter = new ProductAdapter(this , products);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(productAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
              public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(ListProduct.this, ViewProduct.class);
                Bundle bundle = new Bundle();
                bundle.putString("product_id", products[position][0]);
                intent.putExtras(bundle);
                startActivity(intent);
              }
       });
    }
}
