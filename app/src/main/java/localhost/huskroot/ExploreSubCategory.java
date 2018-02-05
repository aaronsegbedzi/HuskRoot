package localhost.huskroot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class ExploreSubCategory extends AppCompatActivity {

    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String[][] action = {{bundle.getString("category_id")}};
            ExploreSubWorker exploreSubWorker = new ExploreSubWorker(this);
            exploreSubWorker.execute(action);
            getSupportActionBar().setTitle("Explore / " + bundle.getString("category_name"));
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

    public void setSubCategories(final String[][] subcategories) {
        SubCategoryAdapter categoryAdapter = new SubCategoryAdapter(this, subcategories);
        grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(categoryAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    Intent intent = new Intent(ExploreSubCategory.this, ExploreSubSubCategory.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("category_id", bundle.getString("category_id"));
                    bundle1.putString("category_name", bundle.getString("category_name"));
                    bundle1.putString("subCategory_id", subcategories[position][0]);
                    bundle1.putString("subCategory_name", subcategories[position][1]);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
            }
        });
    }
}
