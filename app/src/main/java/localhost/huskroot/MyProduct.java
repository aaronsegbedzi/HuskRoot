package localhost.huskroot;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MyProduct extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);
        getSupportActionBar().setTitle("Husk Root - My Crops");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[][] action = {{"myProduct", }};
        ProductListWorker productListWorker = new ProductListWorker(this);
        productListWorker.execute(action);
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
        MyProductAdapter myProductAdapter = new MyProductAdapter(this , products);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(myProductAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
              public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(MyProduct.this, EditProduct.class);
                Bundle bundle = new Bundle();
                bundle.putString("product_id", products[position][0]);
                intent.putExtras(bundle);
                startActivity(intent);
              }
       });
    }
}
